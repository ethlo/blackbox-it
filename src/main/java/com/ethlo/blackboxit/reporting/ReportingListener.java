package com.ethlo.blackboxit.reporting;

import java.util.Date;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;

public interface ReportingListener
{
	void started(Object test);

	void fireTestStarted(Description description);

	void fireTestFailure(Description description, Throwable e);

	void fireTestFinished(Description description);
	
	void fireConcurrentTestFinished(Object test, FrameworkMethod method, boolean success, Date date, PerformanceReport report);
}
