/**
 * 
 */
package test.marshall;

import java.io.Serializable;

/**
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class SerializedObject implements Serializable{
	private static final long serialVersionUID = -6793977180805090387L;
	private int array[] = null;

	public SerializedObject() {
	}

	public void setArray(int array[]) {
		this.array = array;
	}

	public int[] getArray() {
		return array;
	}
}
