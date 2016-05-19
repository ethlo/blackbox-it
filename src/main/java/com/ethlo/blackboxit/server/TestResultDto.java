package com.ethlo.blackboxit.server;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.ethlo.blackboxit.reporting.PerformanceReport;

public class TestResultDto
{
	@NotNull
	private String testClass;
	
	@NotNull
	private String methodName;
	
	@NotNull
	private Date timestamp;

	@NotNull
	private Integer repeats;

	@NotNull
	private Integer concurrency;

	@NotNull
	private Integer warmupRuns;
	
	@NotNull
	private Boolean success;
	
	private Set<String> tags;
	
	private String name;
	
	private PerformanceReport performance;
	
	public String getMethodName()
	{
		return methodName;
	}
	
	public void setMethodName(String methodName) 
	{
		this.methodName = methodName;
	}
	
	public Date getTimestamp()
	{
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public PerformanceReport getPerformance()
	{
		return performance;
	}
	
	public void setPerformance(PerformanceReport performance)
	{
		this.performance = performance;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}
	
	public boolean isSuccess()
	{
		return success;
	}

	public String[] getTags()
	{
		return tags.toArray(new String[tags.size()]);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setTags(Set<String> tags)
	{
		this.tags = tags;
	}

	public String getTestClass()
	{
		return testClass;
	}

	public void setTestClass(String testClass)
	{
		this.testClass = testClass;
	}

	public Integer getRepeats()
	{
		return this.repeats;
	}

	public Integer getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(Integer concurrency) {
		this.concurrency = concurrency;
	}

	public Integer getWarmupRuns() {
		return warmupRuns;
	}

	public void setWarmupRuns(Integer warmupRuns) {
		this.warmupRuns = warmupRuns;
	}

	public void setRepeats(Integer repeats) {
		this.repeats = repeats;
	}
}
