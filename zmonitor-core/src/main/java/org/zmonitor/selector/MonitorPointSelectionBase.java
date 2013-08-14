/**
 * 
 */
package org.zmonitor.selector;

import java.util.Iterator;
import java.util.List;

import org.zmonitor.MonitorPoint;
import org.zmonitor.util.Predicate;
import org.zmonitor.util.RangeRetriever;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MonitorPointSelectionBase extends SelectionEntryBase<MonitorPoint, MonitorPointSelection> implements MonitorPointSelection{

	
	public MonitorPointSelectionBase(Entry<MonitorPoint> entry) {
		super(entry);
		super.pseudoClassDefs.putAll(MPDefaultPseudoClassDefs.getDefaults());
	}

	public MonitorPointSelectionBase(EntryContainer<MonitorPoint> container) {
		super(container);
		super.pseudoClassDefs.putAll(MPDefaultPseudoClassDefs.getDefaults());
	}

	public MonitorPointSelectionBase(Iterator<Entry<MonitorPoint>> itor) {
		super(itor);
		super.pseudoClassDefs.putAll(MPDefaultPseudoClassDefs.getDefaults());
	}

	public MonitorPointSelectionBase(@SuppressWarnings("rawtypes") List list) {
		super(list);
		super.pseudoClassDefs.putAll(MPDefaultPseudoClassDefs.getDefaults());
	}


	@Override
	protected MonitorPointSelectionBase toSelection(
			Iterator<Entry<MonitorPoint>> itor) {
		return new MonitorPointSelectionBase(itor);
	}
	
	public MonitorPointSelection greaterThan(final RangeRetriever retriever, 
			final long millis) {
		return (MonitorPointSelection) this.filter(
				new Predicate<MonitorPoint>(){
					
			public boolean apply(MonitorPoint mp) {
				return retriever.retrieve(mp).greaterThan(millis);
			}});
	}

	public MonitorPointSelection lessThan(final RangeRetriever retriever, 
			final long millis) {
		return (MonitorPointSelection) this.filter(
				new Predicate<MonitorPoint>(){
					
			public boolean apply(MonitorPoint mp) {
				return retriever.retrieve(mp).greaterThan(millis);
			}});
	}

	public MonitorPointSelection between(final RangeRetriever retriever, 
			final long startMillis, final long endMillis) {
		return (MonitorPointSelection) this.filter(
				new Predicate<MonitorPoint>(){
					
			public boolean apply(MonitorPoint mp) {
				return retriever.retrieve(mp).between(startMillis, endMillis);
			}});
	}

	

	
}//end of class...
