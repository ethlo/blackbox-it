package com.ethlo.blackboxit.reporting;

import java.util.Date;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
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
	public void fireTestFinished(Description description)
	{
		logger.info("Finished test {}", description);
	}

	@Override
	public void fireConcurrentTestFinished(Object test, FrameworkMethod method, boolean success, Date time, PerformanceReport report)
	{
		logger.info("Performance report for {}: {}", method, report);
	}
}
