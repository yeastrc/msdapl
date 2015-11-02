/**
 * SchedulerException.java
 * @author Vagisha Sharma
 * Jul 15, 2011
 */
package org.uwpr.scheduler;

public class SchedulerException extends Exception {

	public SchedulerException(String message) {
		super(message);
	}
	
	public SchedulerException(String message, Throwable t) {
		super(message, t);
	}
}
