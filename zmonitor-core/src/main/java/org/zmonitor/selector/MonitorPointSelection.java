/**
 * 
 */
package org.zmonitor.selector;

import org.zmonitor.MonitorPoint;
import org.zmonitor.util.RangeRetriever;

/**
 * 
 * An enhanced selection interface designed specifically for
 * {@link MonitorPoint}.
 * 
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public interface MonitorPointSelection extends
		Selection<MonitorPoint, MonitorPointSelection> {
	/**
	 * the default pseudo class ("greater-than") definition that developer can
	 * use in this selection, usage example:<br>
	 * <i>:greater-than('END', 300)</i>
	 * <p>
	 * Must has two arguments:
	 * <ol>
	 * <li> A String representing a predefined {@link RangeRetriever} which comes from {@link org.zmonitor.util.RangeRetriever.Default}
	 * <li> A long of milliseconds, the indicated range interval should greater than this value.
	 * </ol>
	 */
	String PSEUDO_CLASS_GREATER_THAN = "greater-than";

	/**
	 * the default pseudo class ("less-than") definition that developer can
	 * use in this selection,  usage example:<br>
	 * <i>:less-than('PARENT', 2000)</i>
	 * <p>
	 * Must has two arguments:
	 * <ol>
	 * <li> A String representing a predefined {@link RangeRetriever} which comes from {@link org.zmonitor.util.RangeRetriever.Default}
	 * <li> A long of milliseconds, the indicated range interval should less than this value.
	 * </ol>
	 */
	String PSEUDO_CLASS_LESS_THAN = "less-than";
	/**
	 * the default pseudo class ("between") definition that developer can use in
	 * this selection, usage example:<br>
	 * <i>:between('NEXT', 300, 1000)</i>
	 * <p>
	 * Must has three arguments:
	 * <ol>
	 * <li> A String representing a predefined {@link RangeRetriever} which comes from {@link org.zmonitor.util.RangeRetriever.Default}
	 * <li> A long of milliseconds, the indicated range interval should greater than this value.
	 * <li> A long of milliseconds, the indicated range interval should less than this value.
	 * </ol>
	 * .
	 */
	String PSEUDO_CLASS_BETWEEN = "between";

	/**
	 * This method iterate selected {@link MonitorPoint}s and let by those with
	 * range greater than given milliseconds, in other words, the
	 * {@link MonitorPoint} which range (according to given
	 * {@link RangeRetriever}) is less or equals to given millis will be
	 * ignored.
	 * 
	 * @param retriever
	 *            retrieve a range based on occurring mp during the iteration of
	 *            selection. .
	 * @param millis
	 *            the milliseconds that the retrieved range's interval should
	 *            greater than.
	 * @return
	 */
	MonitorPointSelection greaterThan(RangeRetriever retriever, long millis);

	/**
	 * This method iterate selected {@link MonitorPoint}s and let by those with
	 * range less than given milliseconds, in other words, the
	 * {@link MonitorPoint} which range (according to given
	 * {@link RangeRetriever}) is greater or equals to given millis will be
	 * ignored.
	 * 
	 * @param retriever
	 *            retrieve a range based on occurring mp during the iteration of
	 *            selection.
	 * @param millis
	 *            the milliseconds that the retrieved range's interval should
	 *            less than.
	 * @return
	 */
	MonitorPointSelection lessThan(RangeRetriever retriever, long millis);

	/**
	 * This method iterate selected {@link MonitorPoint}s and let by those with
	 * range between startMillis and endMillis, in other words, the
	 * {@link MonitorPoint} which range (according to given
	 * {@link RangeRetriever}) is outside or same as the two millis will be
	 * ignored.
	 * 
	 * @param retriever
	 *            retrieve a range based on occurring mp during the iteration of
	 *            selection.
	 * @param startMillis
	 *            the milliseconds that the retrieved range's interval should
	 *            greater than.
	 * @param endMillis
	 *            the milliseconds that the retrieved range's interval should
	 *            less than.
	 * @return
	 */
	MonitorPointSelection between(RangeRetriever retriever, long startMillis,
			long endMillis);
}
