package com.ethlo.blackboxit.model;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

import com.ethlo.blackboxit.reporting.PerformanceReport;

@Entity
public class TestPerformance
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name="min")
	private long min;
	
	@Column(name="max")
	private long max;

	@Column(name="median")
	private long median;
	
	@Column(name="average")
	private double average;
	
	@Column(name="standard_deviation")
	private double standardDeviation;
	
	protected TestPerformance()
	{
	
	}
	
	public TestPerformance(PerformanceReport performance)
	{
		this.average = performance.getAverage();
		this.max = performance.getMax();
		this.min = performance.getMin();
		this.median = performance.getMedian();
		this.standardDeviation = performance.getStandardDeviation();
	}

	public long getMin()
	{
		return min;
	}

	public long getMax()
	{
		return max;
	}

	public long getMedian()
	{
		return median;
	}

	public void setMedian(long median)
	{
		this.median = median;
	}

	public double getAverage()
	{
		return average;
	}

	public void setAverage(double d)
	{
		this.average = d;
	}

	@Override
	public String toString()
	{
		return 
				"Min: \t\t" + formatNum(min) + " ms" 
			+ "\nMax: \t\t" + formatNum(max) + " ms" 
			+ "\nMedian: \t" + formatNum(median) + " ms" 
			+ "\nAverage: \t" + formatNum(average) + " ms"
			+ "\nDeviation: \t" + formatNum(standardDeviation) + " ms";
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
}
