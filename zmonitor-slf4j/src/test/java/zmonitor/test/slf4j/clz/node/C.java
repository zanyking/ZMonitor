/**
 * 
 */
package zmonitor.test.slf4j.clz.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class C extends Slf4jTestNode {
	private static final Logger logger = LoggerFactory.getLogger(C.class);
	
	private String doC_mesgPtn;
	private Object[] doC_Arguments;
	
	
	
	public C(Slf4jTestNode parent, Slf4jTestNode previousSibling,
			String doC_mesgPtn, Object[] doC_Arguments) {
		super(parent, previousSibling);
		this.doC_mesgPtn = doC_mesgPtn;
		this.doC_Arguments = doC_Arguments;
	}
	
	public C(){
		this(null,null, C_FAC.doC_mesgPtn, C_FAC.doC_Arguments);
	}


	protected void selfStart() {
		doC1();
	}
	private void doC1(){
		logger.trace(">>"+doC_mesgPtn, doC_Arguments);
	
	}

	@Override
	protected void selfEnd() {
		doC2();
	}
	
	private void doC2(){
		logger.trace("<< end of doC().");
	}
	
	public static class CFac implements NodeFac{
		private final String doC_mesgPtn;
		private final Object[] doC_Arguments;

		public CFac(String doC_mesgPtn, Object[] doC_Arguments) {
			this.doC_mesgPtn = doC_mesgPtn;
			this.doC_Arguments = doC_Arguments;
		}

		private CFac(){
			doC_mesgPtn = "doC() arg1:{}, arg2:{}";
			doC_Arguments = new Object[]{"Ian Tsai", 32};
		}
		public Slf4jTestNode newNode(Slf4jTestNode parent,
				Slf4jTestNode previousSibling) {
			return new C(parent, previousSibling, doC_mesgPtn, doC_Arguments);
		}
		
		public CFac toCFac(String doC_mesgPtn, Object... doC_Arguments){
			return new CFac(doC_mesgPtn, doC_Arguments);
		}
		
	}
	
	public static final CFac C_FAC = new CFac();


}
