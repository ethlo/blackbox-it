package com.ethlo.blackboxit.reporting;

import org.junit.runner.Description;

public interface ReportingListener
{
	void started(Object test);

	void fireTestStarted(Description description);

	void fireTestFailure(Description description, Throwable e);

	void fireTestFinished(TestResult result);
}
