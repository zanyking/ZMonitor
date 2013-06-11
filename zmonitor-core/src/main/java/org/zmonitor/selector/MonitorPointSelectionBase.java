/**
 * 
 */
package org.zmonitor.selector;

import java.util.Iterator;
import java.util.List;

import org.zmonitor.MonitorPoint;
import org.zmonitor.selector.impl.MatchCtx;
import org.zmonitor.selector.impl.PseudoClassDef;
import org.zmonitor.util.Predicate;
import org.zmonitor.util.RangeRetriever;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MonitorPointSelectionBase extends SelectionEntryBase<MonitorPoint, MonitorPointSelection> implements MonitorPointSelection{

	
	public MonitorPointSelectionBase(Entry<MonitorPoint> entry) {
		super(entry);
		initPseudoClassDefs();
	}

	public MonitorPointSelectionBase(EntryContainer<MonitorPoint> container) {
		super(container);
		initPseudoClassDefs();
	}

	public MonitorPointSelectionBase(Iterator<Entry<MonitorPoint>> itor) {
		super(itor);
		initPseudoClassDefs();
	}

	public MonitorPointSelectionBase(List list) {
		super(list);
		initPseudoClassDefs();
	}
	/**
	 * for MPSelection specific pseudo class definition.
	 */
	private void initPseudoClassDefs(){
		super.addPseudoClassDef(PSEUDO_CLASS_GREATER_THAN, new PseudoClassDef<MonitorPoint>() {
			public boolean accept(MatchCtx<MonitorPoint> ctx, String... parameters) {
				if(parameters.length!=2)
					throw new SelectorEvalException("must has two arguments! parameters:"+parameters);
				RangeRetriever rr = null;
				try{
					rr = RangeRetriever.Default.valueOf(parameters[0]);
				}catch(Exception e){
					throw new SelectorEvalException("invalid RangeRetriever name:"+parameters[0], e);
				}
				if(rr==null)
					throw new SelectorEvalException("invalid RangeRetriever name:"+parameters[0]);
				try{
					long millis = Long.parseLong(parameters[1]);
					return rr.retrieve(ctx.getEntry().getValue()).greaterThan(millis);
				}catch(Exception e){
					throw new SelectorEvalException("invalid millis:"+parameters[1]);
				}
			}
		});
		super.addPseudoClassDef(PSEUDO_CLASS_LESS_THAN, new PseudoClassDef<MonitorPoint>() {
			public boolean accept(MatchCtx<MonitorPoint> ctx, String... parameters) {
				if(parameters.length!=2)
					throw new SelectorEvalException("must has two arguments! parameters:"+parameters);
				
				RangeRetriever rr = null;
				try{
					rr = RangeRetriever.Default.valueOf(parameters[0]);
				}catch(Exception e){
					throw new SelectorEvalException("invalid RangeRetriever name:"+parameters[0], e);
				}
				if(rr==null)
					throw new SelectorEvalException("invalid RangeRetriever name:"+parameters[0]);
				try{
					long millis = Long.parseLong(parameters[1]);
					return rr.retrieve(ctx.getEntry().getValue()).lessThan(millis);
				}catch(Exception e){
					throw new SelectorEvalException("invalid millis:"+parameters[1]);
				}
			}
		});
		super.addPseudoClassDef(PSEUDO_CLASS_BETWEEN, new PseudoClassDef<MonitorPoint>() {
			public boolean accept(MatchCtx<MonitorPoint> ctx, String... parameters) {
				if(parameters.length!=3)
					throw new SelectorEvalException("must has three arguments! parameters:"+parameters);
				RangeRetriever rr = null;
				try{
					rr = RangeRetriever.Default.valueOf(parameters[0]);
				}catch(Exception e){
					throw new SelectorEvalException("invalid RangeRetriever name:"+parameters[0], e);
				}
				if(rr==null)
					throw new SelectorEvalException("invalid RangeRetriever name:"+parameters[0]);
				long startMillis = 0;
				try{
					startMillis = Long.parseLong(parameters[1]);
				}catch(Exception e){
					throw new SelectorEvalException("invalid start millis:"+parameters[1]);
				}
				long endMillis = 0;
				try{
					endMillis = Long.parseLong(parameters[2]);
				}catch(Exception e){
					throw new SelectorEvalException("invalid end millis:"+parameters[2]);
				}
				
				return rr.retrieve(ctx.getEntry().getValue()).between(startMillis, endMillis);
			}
		});
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
