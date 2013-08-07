/**
 * 
 */
package zmonitor.test.gson;

import org.junit.Test;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitor;
import org.zmonitor.impl.MonitorMetaBase;
import org.zmonitor.marker.Marker;
import org.zmonitor.marker.Markers;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;
import org.zmonitor.util.StackTraceElementFinder;
import org.zmonitor.util.json.DefaultMarkerAdapter;
import org.zmonitor.util.json.DefaultMonitorPointAdapter;

import zmonitor.test.clz.BusinessObject;
import zmonitor.test.clz.Dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class GSon_TEST  extends TestBase{

	protected void runCase(){
		ZMonitor.push("this is a test mesg!", true);
		new Dao().getBean();
		new BusinessObject().doBiz();
		ZMonitor.pop(true);	
	}
	@Test
	public void monitorSequence2JSon(){ 
		MonitoredResult result = this.getMonitoredResult();
//		MonitorPointSelection mpSel = result.asSelection().select(".Dao.getBean");
//		TestUtils.assertMPAmount(mpSel, 4);
		MonitorSequence ms = result.get(0);
		printGSonResult(ms);
	}
	
	
	@Test
	public void MonitorMeta2JSon(){
		StackTraceElement[] elements = StackTraceElementFinder.truncate(0);
		MonitorMetaBase mm = new MonitorMetaBase(
			Markers.MK_PUSH_ZM, 
			Markers.TRACKER_NAME_ZM, 
			elements,
			Thread.currentThread().getName());
		printGSonResult(mm);
	}
	@Test
	public void JSon2MonitorMeta(){
		StackTraceElement[] elements = StackTraceElementFinder.truncate(0);
		
		MonitorMetaBase mm = new MonitorMetaBase(
			Markers.MK_PUSH_ZM, 
			Markers.TRACKER_NAME_ZM, 
			elements,
			Thread.currentThread().getName());
		printGSonResult(mm);
	}
	
	private static void printGSonResult(Object target){
		GsonBuilder gBuilder = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapter(MonitorPoint.class, new DefaultMonitorPointAdapter())
				.registerTypeAdapter(Marker.class, new DefaultMarkerAdapter());
		Gson gson = gBuilder.create();
		String jsonOutput = gson.toJson(target);
		System.out.println("+++++++++++++++++++++++++");
		System.out.println(jsonOutput);
	}
}
