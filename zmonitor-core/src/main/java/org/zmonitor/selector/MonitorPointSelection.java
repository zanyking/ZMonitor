/**
 * 
 */
package org.zmonitor.selector;

import java.util.List;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.selector.impl.Selection;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MonitorPointSelection extends Selection {

	/**
	 * 
	 */
	public MonitorPointSelection(List list) {
		super(list);
	}

	public MonitorPointSelection(Entry entry) {
		super(entry);
	}

	public MonitorPointSelection(EntryContainer container) {
		super(container);
	}

	public MonitorPointSelection greaterThan(long millis){
		return null;//TODO:
	}
	
}
