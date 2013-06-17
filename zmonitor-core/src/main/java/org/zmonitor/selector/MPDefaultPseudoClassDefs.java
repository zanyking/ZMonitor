/**
 * 
 */
package org.zmonitor.selector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.zmonitor.MonitorPoint;
import org.zmonitor.selector.impl.PseudoClassDef;
import org.zmonitor.util.RangeRetriever;

import static org.zmonitor.selector.MonitorPointSelection.*;
/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MPDefaultPseudoClassDefs {
	private MPDefaultPseudoClassDefs() {}

	private static final Map<String, PseudoClassDef<MonitorPoint>> DEFs;
	
	/**
	 * for MPSelection specific pseudo class definition.
	 */
	static{
		Map<String, PseudoClassDef<MonitorPoint>> tempDefs = 
				new HashMap<String, PseudoClassDef<MonitorPoint>>();
		
		tempDefs.put(PSEUDO_CLASS_GREATER_THAN, new PseudoClassDef<MonitorPoint>() {
			public boolean accept(Entry<MonitorPoint> ctx, String... parameters) {
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
					return rr.retrieve(ctx.getValue()).greaterThan(millis);
				}catch(Exception e){
					throw new SelectorEvalException("invalid millis:"+parameters[1]);
				}
			}
		});
		tempDefs.put(PSEUDO_CLASS_LESS_THAN, new PseudoClassDef<MonitorPoint>() {
			public boolean accept(Entry<MonitorPoint> ctx, String... parameters) {
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
					return rr.retrieve(ctx.getValue()).lessThan(millis);
				}catch(Exception e){
					throw new SelectorEvalException("invalid millis:"+parameters[1]);
				}
			}
		});
		tempDefs.put(PSEUDO_CLASS_BETWEEN, new PseudoClassDef<MonitorPoint>() {
			public boolean accept(Entry<MonitorPoint> ctx, String... parameters) {
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
				
				return rr.retrieve(ctx.getValue()).between(startMillis, endMillis);
			}
		});
		DEFs = Collections.unmodifiableMap(tempDefs);
	}
	public static Map<String, PseudoClassDef<MonitorPoint>> getDefaults(){
		return DEFs;
	}
}
