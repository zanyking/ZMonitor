/**ParcelDecoder.java
 * 2011/10/8
 * 
 */
package org.zkoss.monitor.server.grizzly;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.Attribute;
import org.glassfish.grizzly.attributes.AttributeStorage;

/**
 * This class implements a {@link org.glassfish.grizzly.Transformer} which decodes data
 * comes from {@link java.io.ObjectOutputStream}.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class JavaObjectDecoder extends AbstractTransformer<Buffer, Serializable>  {

	
	protected final Attribute<ObjSerStreamByteArray> finalResultAttr = 
			attributeBuilder.createAttribute("JavaObjectDecoder.finalResult");
	
	
	public String getName() {
		return "JavaObjectDecoder";
	}

	public boolean hasInputRemaining(AttributeStorage storage, Buffer input) {
		return input != null && input.hasRemaining();
	}
	private static final AtomicInteger AINT = new AtomicInteger();
	/*
	 * (non-Javadoc)
	 * @see org.glassfish.grizzly.AbstractTransformer#transformImpl(org.glassfish.grizzly.attributes.AttributeStorage, java.lang.Object)
	 */
	@Override
	protected TransformationResult<Buffer, Serializable> transformImpl(
			AttributeStorage storage, 
			Buffer input) throws TransformationException {
		
		if(!input.hasRemaining()){
			return TransformationResult.createIncompletedResult(null);
		}
		
		ByteBuffer bb = input.toByteBuffer();
		byte[] arr = null;
		arr = fetchAll(bb);
		input.position(input.limit());
		
		debugFileLog(arr);
		
		// please read "java Object serialization Stream Protocol": http://download.oracle.com/javase/6/docs/platform/serialization/spec/output.html
		//http://java.sun.com/javase/technologies/core/basic/serializationFAQ.jsp
		//0. verify stream header, check if this is a java Object serialization Stream header. 
		//1. get resultCtxt from attributes
		//2. if resultCtxt is null.
		//2.1 try to read the data using ObjectInputStream(), if EOF, createCompletedResult
		//2.2 if didn't reach EOF, start new resultCtxt, set content to current data, createInCompletedResult
		
		
		//3. if resultCtxt exists, append data behind the resultCtxt.

		if(arr.length < 3)
			throw new TransformationException("the input is even shorter than possible header!");
		
		Serializable result = null;
		

		System.out.println(">>>>>>>>>Current Stream Header: "+new String(new byte[]{arr[0],arr[1]}));
		
		
		if(ObjSerStreamByteArray.startWithHead(arr)){// this is the start of a Stream.
			result = deserialize(arr);
//			System.out.println(">>>>>>> Does arr end is TC_ENDBLOCKDATA? "+
//					(ObjectStreamConstants.TC_ENDBLOCKDATA==arr[arr.length-1]));
			if(result!=null){
				System.out.println(">>>>>>> transmission Completed!");
				return TransformationResult.createCompletedResult(	result, input);
			}else{
				finalResultAttr.set(storage, new ObjSerStreamByteArray(arr));
				System.out.println(">>>>>>> transmission incomplete, wait for other chunks...");
				return TransformationResult.createIncompletedResult(null);
			}
		}else{//this is rest chunk of an existing Stream.
			ObjSerStreamByteArray bArray = finalResultAttr.get(storage);
			if(bArray==null){
				throw new TransformationException("unknown Protocol detected, " +
						"this might be some kind of attack! or version missmatching!");
			}
			bArray.append(arr);
			result = deserialize(bArray.toArray());
			if(result!=null){
				System.out.println(">>>>>>> transmission Completed!, after processed a CHUNK ");
				return TransformationResult.createCompletedResult(	result, input);
			}else{
				System.out.println(">>>>>>> transmission incomplete, wait for other chunks... after processed a CHUNK ");
				return TransformationResult.createIncompletedResult(null);
			}
		}
	}
	

	/**
	 * 
	 * @param arr
	 * @return
	 */
	private static Serializable deserialize(byte[] arr) {
		
		ObjectInputStream oin = null;
		try {

			//TODO: will throw exception if this is not the Java Object Streaming Header.
			oin = new ObjectInputStream( new ByteArrayInputStream(arr));
		}catch(StreamCorruptedException e){
			throw new TransformationException("this is not an Object Input Stream supported format!", e);
		}catch (IOException e) {
			//this section is just for testing...
			throw new TransformationException(e);
		}
		
		try {
			return (Serializable) oin.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TransformationException(e);
		}
	}
	
	private byte[] fetchAll(ByteBuffer bb){
		int length = bb.remaining();
		byte[] arr = new byte[length];
		for(int i=0;i<length;i++){
			arr[i] = bb.get();
		}
		return arr;
	}

	
	private static void debugFileLog(byte[] arr){
		try {//TODO: this section is just for debugging, need to be removed.
			PrintWriter fWrite = new PrintWriter(new File("C:\\ZM_LOG\\zmonitor_receive_" +
					AINT.getAndIncrement()+".log"));
			fWrite.write(new String(arr));
			fWrite.close();
		}catch (IOException e) {
			//this section is just for testing...
		}
	}

	

}
