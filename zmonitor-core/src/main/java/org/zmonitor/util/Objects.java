/* Objects.java


	Purpose: Utilities related to Object.
	Description:
	History:
	 2001/5/12, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zmonitor.util;

import java.math.BigDecimal;

/**
 * Utilities related to the Object class.
 *
 * @author tomyeh
 */
public class Objects {
	private Objects() {}
	
	/**
	 * Tests whether two objects are equals.
	 *
	 * <p>It takes care of the null case. Thus, it is helpful to implement
	 * Object.equals.
	 *
	 * <p>Notice: it uses compareTo if BigDecimal is found. So, in this case,
	 * a.equals(b) might not be the same as Objects.equals(a, b).
	 *
	 * <p>If both a and b are Object[], they are compared item-by-item.
	 */
	public static final boolean equals(Object a, Object b) {
		if (a == b || (a != null && b != null && a.equals(b)))
			return true;
		if ((a instanceof BigDecimal) && (b instanceof BigDecimal))
			return ((BigDecimal)a).compareTo((BigDecimal) b) == 0;

		if (a == null || !a.getClass().isArray())
			return false;

		if ((a instanceof Object[]) && (b instanceof Object[])) {
			final Object[] as = (Object[])a;
			final Object[] bs = (Object[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (!equals(as[j], bs[j])) //recursive
					return false;
			return true;
		}
		if ((a instanceof int[]) && (b instanceof int[])) {
			final int[] as = (int[])a;
			final int[] bs = (int[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof byte[]) && (b instanceof byte[])) {
			final byte[] as = (byte[])a;
			final byte[] bs = (byte[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof char[]) && (b instanceof char[])) {
			final char[] as = (char[])a;
			final char[] bs = (char[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof long[]) && (b instanceof long[])) {
			final long[] as = (long[])a;
			final long[] bs = (long[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof short[]) && (b instanceof short[])) {
			final short[] as = (short[])a;
			final short[] bs = (short[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof double[]) && (b instanceof double[])) {
			final double[] as = (double[])a;
			final double[] bs = (double[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (Double.compare(as[j], bs[j]) != 0)
					return false;
			return true;
		}
		if ((a instanceof float[]) && (b instanceof float[])) {
			final float[] as = (float[])a;
			final float[] bs = (float[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (Float.compare(as[j], bs[j]) != 0)
					return false;
			return true;
		}
		if ((a instanceof boolean[]) && (b instanceof boolean[])) {
			final boolean[] as = (boolean[])a;
			final boolean[] bs = (boolean[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		return false;
	}

}
