package com.ethlo.blackboxit.reporting;

import java.util.Date;
import java.util.Set;

import org.junit.runner.Description;
import org.springframework.util.Assert;

public class TestResult
{
	private final String name;
	private final Set<String> tags;
	private final Description description;
	private final PerformanceReport performanceReport;
	private final Throwable error;
	private final Date timestamp;

	private TestResult(String name, Set<String> tags, Description description, PerformanceReport performanceReport, Throwable error)
	{
		this.name = name;
		this.tags = tags;
		this.description = description;
		this.performanceReport = performanceReport;
		this.error = error;
		this.timestamp = new Date();
	}
	
	public static TestResult success(String testName, Set<String> tags, Description description, PerformanceReport performanceReport)
	{
		return new TestResult(testName, tags, description, performanceReport, null);
	}
	
	public static TestResult error(String testName, Set<String> tags, Description description, Throwable throwable)
	{
		Assert.notNull(throwable);
		return new TestResult(testName, tags, description, null, throwable);
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

	public String getName()
	{
		return name;
	}

	public Set<String> getTags()
	{
		return tags;
	}

	@Override
	public String toString() {
		return "TestResult [name=" + name + ", tags=" + tags + ", description=" + description
				+ ", performanceReport=" + performanceReport + ", error=" + error + ", timestamp=" + timestamp + "]";
	}
}
