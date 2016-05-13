package com.ethlo.blackboxit.reporting;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

public class PerformanceReport
{
	private String testName;
	private int warmupRuns;
	private int repeats;
	private int concurrency;
	private int invocations;
	private long min;
	private long max;
	private long median;
	private double average;
	private long total;
	private double standardDeviation;
	
	public int getWarmupRuns() {
		return warmupRuns;
	}

	public void setWarmupRuns(int warmupRuns) {
		this.warmupRuns = warmupRuns;
	}

	public int getRepeats() {
		return repeats;
	}

	public void setRepeats(int repeats) {
		this.repeats = repeats;
	}

	public int getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(int concurrency) {
		this.concurrency = concurrency;
	}

	public int getInvocations() {
		return invocations;
	}

	public void setInvocations(int invocations) {
		this.invocations = invocations;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getMedian() {
		return median;
	}

	public void setMedian(long median) {
		this.median = median;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double d) {
		this.average = d;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public String toString()
	{
		return 
			"\nWarmup: \t" + formatNum(warmupRuns) 
			+ "\nRepeats: \t" + formatNum(repeats)
			+ "\nConcurrency: \t" + formatNum(concurrency) 
			+ "\nInvocations: \t" + formatNum(invocations) 
			+ "\nMin: \t\t" + formatNum(min) + " ms" 
			+ "\nMax: \t\t" + formatNum(max) + " ms" 
			+ "\nMedian: \t" + formatNum(median) + " ms" 
			+ "\nAverage: \t" + formatNum(average) + " ms"
			+ "\nDeviation: \t" + formatNum(standardDeviation) + " ms"
			+ "\nTotal: \t\t" + formatNum(total) + " ms";
	}
	
	private String formatNum(Number num)
	{
		final DecimalFormat dec = new DecimalFormat();     
		dec.setGroupingUsed(true);
		if (num instanceof Long || num instanceof Integer)
		{
			dec.setMinimumFractionDigits(0);
		}
		else
		{
			dec.setMinimumFractionDigits(2);
		}
		dec.setMaximumFractionDigits(2);
		return StringUtils.leftPad(dec.format(num), 8, ' ');
	}

	public void setStandardDeviation(double stdDeviation)
	{
		this.standardDeviation = stdDeviation;
	}

	public double getStandardDeviation()
	{
		return standardDeviation;
	}

	public String getTestName()
	{
		return testName;
	}

	public void setTestName(String testName)
	{
		this.testName = testName;
	}
}
