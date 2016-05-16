package com.ethlo.blackboxit.server;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.ethlo.blackboxit.reporting.PerformanceReport;

public class TestResultDto
{
	@NotNull
	private String testName;
	
	@NotNull
	private Date timestamp;

	@NotNull
	private Boolean success;
	
	private PerformanceReport performance;
	
	public String getTestName()
	{
		return testName;
	}
	
	public void setTestName(String testName) 
	{
		this.testName = testName;
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
}
