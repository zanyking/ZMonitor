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
					Strings.append((Object[])errorMessages);
		
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
					"the given integer[" +integer+
					"] doesn't satisfy:[ <= " + i + ", >=" + j
							+ "]");
	}

	public static void checkEquals(Object obj1, Object obj2, String... errMesgs) {
		if(obj1==null && obj2==null){
			return;// both are null...
		}
		if(obj1==null){
			StringBuffer sb = new StringBuffer();
			Strings.append(sb, errMesgs);
			Strings.append(sb, ", obj1 is null but obj2 has value, obj2:",obj2);
			throw new IllegalArgumentException(sb.toString());
		}else if(!obj1.equals(obj2)){
			throw new IllegalArgumentException(
					"obj1 is not equals to obj2: obj1=" + obj1 + ", obj2="
							+ obj2);
		}
	}

}
