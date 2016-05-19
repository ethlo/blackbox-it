package com.ethlo.blackboxit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="test", uniqueConstraints=@UniqueConstraint(columnNames={"class", "method"}))
public class Test
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="name", nullable=false)
    private String name;
    
    @Column(name="class", nullable=false)
    private String testClass;
    
    @Column(name="method", nullable=false)
    private String methodName;
    
    @Column(name="concurrency", nullable=false)
    private Integer concurrency;
    
    @Column(name="repeats", nullable=false)
    private Integer repeats;
    
    @Column(name="warmup_runs", nullable=false)
    private Integer warmupRuns;
    
    @Column(name="tags", nullable=false)
    private String tags;
    
	public Integer getId()
	{
		return this.id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTestClass()
	{
		return testClass;
	}

	public void setTestClass(String testClass)
	{
		this.testClass = testClass;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public Integer getConcurrency()
	{
		return concurrency;
	}

	public void setConcurrency(Integer concurrency)
	{
		this.concurrency = concurrency;
	}

	public Integer getRepeats()
	{
		return repeats;
	}

	public void setRepeats(Integer repeats)
	{
		this.repeats = repeats;
	}

	public Integer getWarmupRuns()
	{
		return warmupRuns;
	}

	public void setWarmupRuns(Integer warmupRuns)
	{
		this.warmupRuns = warmupRuns;
	}

	public String getTags()
	{
		return tags;
	}

	public void setTags(String tags)
	{
		this.tags = tags;
	}
}
