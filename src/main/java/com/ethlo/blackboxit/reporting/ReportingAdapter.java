package com.ethlo.blackboxit.reporting;

import java.util.Date;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;

public class ReportingAdapter implements ReportingListener
{
	@Override
	public void started(Object test){}

	@Override
	public void fireTestStarted(Description description){}
	
	@Override
	public void fireTestFailure(Description description, Throwable e) {}
	
	@Override
	public void fireTestFinished(Description description) {}
	
	@Override
	public void fireConcurrentTestFinished(Object test, FrameworkMethod method, boolean success, Date time, PerformanceReport report){}
}