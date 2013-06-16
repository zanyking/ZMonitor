/**
 * 
 */
package org.zmonitor.util;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Arguments {
	private Arguments(){};

	/**
	 * 
	 * @param target
	 * @param errorMessages
	 */
	public static void checkNotNull(Object target, Object... errorMessages) {
		String mesg = (errorMessages.length==0)? 
			"The input object cannot be null" : 
				Strings.append(errorMessages);
		
		if(target==null){
			throw new IllegalArgumentException(mesg);
		}
	}

	/**
	 * 
	 * @param attribute
	 */
	public static void checkNotEmpty(String str, String... errorMessages) {
		String mesg = (errorMessages.length==0)? 
				"The input string cannot be null or empty" : 
					Strings.append(errorMessages);
		
		if(str==null || str.isEmpty()){
			throw new IllegalArgumentException(mesg);
		}
	}

	public static void checkInterval(int integer, int i, int j) {
		if (i > j)
			throw new IllegalArgumentException(
					"The given conditions are incorrect: " + i + " > " + j);
		if (integer < i || integer > j)
			throw new IndexOutOfBoundsException(
					"the given integer doesn't satisfy:[ <= " + i + ", >=" + j
							+ "]");
	}

}
