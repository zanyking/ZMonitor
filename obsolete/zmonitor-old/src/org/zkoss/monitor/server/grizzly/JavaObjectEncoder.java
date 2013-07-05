/**
 * 
 */
package org.zkoss.monitor.server.grizzly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.AttributeStorage;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class JavaObjectEncoder extends AbstractTransformer<Serializable, Buffer>  {

	public String getName() {
		return "JavaObjectEncoder";
	}

	public boolean hasInputRemaining(AttributeStorage storage, Serializable input) {
		return input != null;
	}

	@Override
	protected TransformationResult<Serializable, Buffer> transformImpl(
			AttributeStorage storage, 
			Serializable input)
			throws TransformationException {
        if (input == null) {
//        	return TransformationResult.createIncompletedResult(null);
            throw new TransformationException("Input could not be null");
        }
       
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
			ObjectOutputStream oOut = new ObjectOutputStream(bout);
			oOut.writeObject(input);
			oOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 byte[] byteRepresentation = bout.toByteArray();
		 
		 final Buffer output =
             obtainMemoryManager(storage).allocate(byteRepresentation.length);
		 
        output.put(byteRepresentation);

        output.flip();
        output.allowBufferDispose(true);
		 
        TransformationResult<Serializable, Buffer> result = 
        	TransformationResult.createCompletedResult(output, null);
		return result;
	}

}
