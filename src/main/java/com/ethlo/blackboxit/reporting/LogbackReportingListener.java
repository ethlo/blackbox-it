package com.ethlo.blackboxit.reporting;

import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackReportingListener implements ReportingListener
{
	private final static Logger logger = LoggerFactory.getLogger(LogbackReportingListener.class);
	
	@Override
	public void started(Object test)
	{
		logger.info("Starting test class {}", test.getClass().getCanonicalName());
	}

	@Override
	public void fireTestStarted(Description description)
	{
		logger.info("Starting test {}", description);
	}

	@Override
	public void fireTestFailure(Description description, Throwable e)
	{
		logger.info("Failed test {}: {}", description, e);
	}

	@Override
	public void fireTestFinished(TestResult testResult)
	{
		logger.info("Finished test {}", testResult);
	}
}
