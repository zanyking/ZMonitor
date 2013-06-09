/**
 * 
 */
package org.zmonitor.selector;

import org.zmonitor.MonitorPoint;
import org.zmonitor.util.RangeRetriever;

/**
 * 
 * An enhanced selection interface designed specifically for {@link MonitorPoint}.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorPointSelection extends Selection<MonitorPoint, MonitorPointSelection> {
	
	
	/**
	 * This method iterate selected {@link MonitorPoint}s and let by those with
	 * range greater than given milliseconds, in other words, the
	 * {@link MonitorPoint} which range (according to given
	 * {@link RangeRetriever}) is smaller or equals to given millis will be
	 * ignored.
	 * 
	 * @param retriever
	 *            retrieve a range based on occurring mp during the iteration of 
	 *            selection.
	 *            .
	 * @param millis
	 *            the milliseconds that the retrieved range's interval should
	 *            greater than.
	 * @return
	 */
	MonitorPointSelection greaterThan(RangeRetriever retriever, long millis);
	
	/**
	 * This method iterate selected {@link MonitorPoint}s and let by those with
	 * range smaller than given milliseconds, in other words, the
	 * {@link MonitorPoint} which range (according to given
	 * {@link RangeRetriever}) is greater or equals to given millis will be
	 * ignored.
	 * 
	 * @param retriever
	 *           retrieve a range based on occurring mp during the iteration of 
	 *            selection.
	 * @param millis
	 *            the milliseconds that the retrieved range's interval should
	 *            smaller than.
	 * @return
	 */
	MonitorPointSelection smallerThan(RangeRetriever retriever, long millis);
	
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
	 *            smaller than.
	 * @return
	 */
	MonitorPointSelection between(RangeRetriever retriever, long startMillis, long endMillis);
}
