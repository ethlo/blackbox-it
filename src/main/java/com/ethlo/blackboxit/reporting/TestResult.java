package com.ethlo.blackboxit.reporting;

import java.util.Date;

import org.junit.runner.Description;
import org.springframework.util.Assert;

public class TestResult
{
	private final Description description;
	private final PerformanceReport performanceReport;
	private final Throwable error;
	private final Date timestamp;

	private TestResult(Description description, PerformanceReport performanceReport, Throwable error)
	{
		this.description = description;
		this.performanceReport = performanceReport;
		this.error = error;
		this.timestamp = new Date();
	}
	
	public static TestResult success(Description description, PerformanceReport performanceReport)
	{
		return new TestResult(description, performanceReport, null);
	}
	
	public static TestResult error(Description description, Throwable throwable)
	{
		Assert.notNull(throwable);
		return new TestResult(description, null, throwable);
	}

	public Description getDescription()
	{
		return description;
	}

	public PerformanceReport getPerformanceReport()
	{
		return performanceReport;
	}

	public boolean isSuccess()
	{
		return error == null;
	}

	public Throwable getError()
	{
		return error;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	@Override
	public String toString() {
		return "TestResult [description=" + description + ", performanceReport=" + performanceReport + ", error="
				+ error + ", timestamp=" + timestamp + "]";
	}
}
