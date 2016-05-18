package com.ethlo.blackboxit.reporting;

import org.junit.runner.Description;

public class ReportingAdapter implements ReportingListener
{
	@Override
	public void started(Object test){}

	@Override
	public void fireTestStarted(Description description){}
	
	@Override
	public void fireTestFailure(Description description, Throwable e) {}
	
	@Override
	public void fireTestFinished(TestResult testResult) {}
}