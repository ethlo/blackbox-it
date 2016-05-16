package com.ethlo.blackboxit.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class TestRun
{
    @Id
    @Column(name = "run_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "test_id", nullable=false)
    private Test test;
    
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "performance_id")
    private TestPerformance testPerformance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="timestamp", nullable=false)
    private Date timestamp;
    
    @ManyToOne
    @JoinColumn(name="env_id")
    private TestEnvironment env; 
    
    @Column(name="success", nullable=false)
    private Boolean success;

	public void setPerformance(TestPerformance performance)
	{
		this.testPerformance = performance;
	}

	public void setTest(Test test)
	{
		this.test = test;
	}

	public Integer getId() {
		return id;
	}

	public Test getTest() {
		return test;
	}

	public TestPerformance getTestPerformance() {
		return testPerformance;
	}

	public TestEnvironment getEnv() {
		return env;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public Date getTimestamp()
	{
		return this.timestamp;
	}
}
