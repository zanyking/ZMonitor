/**
 * 
 */
package test;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Finally_TEST {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("test() "+test());
		
	}

	static Object obj = null;
	
	private static boolean test(){
		try{
			return true;
		}finally{
			try{
				obj.toString();
			}catch(Throwable e){
				return false;
			}
		}
	}
}
