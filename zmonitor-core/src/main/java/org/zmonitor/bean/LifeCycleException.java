/**
 * 
 */
package org.zmonitor.bean;

import java.io.PrintStream;
import java.util.List;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class LifeCycleException extends RuntimeException {

	private static final long serialVersionUID = -4330203556254239563L;

	/**
	 * 
	 */
	public LifeCycleException() {
	}

	/**
	 * @param message
	 */
	public LifeCycleException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LifeCycleException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LifeCycleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param errs
	 */
	public LifeCycleException(List<Throwable> errs) {
		super(mesg(errs), cause(errs));
	}
	
	private static String mesg(List<Throwable> errs){
		StringBuffer sb = new StringBuffer();
		for (Throwable b : errs) {
			sb.append(b.getMessage()).append("\t");	
		}
		return sb.toString();
	}
	
	private static Throwable cause(List<Throwable> errs){
		if(errs.size()==1)return errs.get(0);
		return new MultiProblemException(errs);
	}

	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public static class MultiProblemException extends RuntimeException{
		private static final long serialVersionUID = -6351110002685741786L;
		private List<Throwable> errs;
		public MultiProblemException(List<Throwable> errs){
			this.errs = errs;
		}
		
		public void printStackTrace(PrintStream s){
			s.println(this);
			s.println("total error amount: "+ errs.size());
			
			for(int i=0, j=errs.size();i<j;i++){
				Throwable e = errs.get(i);
				s.println("error: ["+i+"]");
				e.printStackTrace(s);
			}
		}
		
		
	}//end of class...
}
