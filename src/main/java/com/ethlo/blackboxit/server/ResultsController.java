package com.ethlo.blackboxit.server;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ethlo.blackboxit.model.Test;
import com.ethlo.blackboxit.model.TestPerformance;
import com.ethlo.blackboxit.model.TestRun;
import com.ethlo.blackboxit.server.dao.PerformanceResultDao;
import com.ethlo.blackboxit.server.dao.TestDao;
import com.ethlo.blackboxit.server.dao.TestEnvironmentDao;
import com.ethlo.blackboxit.server.dao.TestRunDao;

@RequestMapping("/api/v1")
@RestController
@Transactional
public class ResultsController
{
	@Autowired
	private PerformanceResultDao performanceResultDao;
	
	@Autowired
	private TestRunDao testResultDao;
	
	@Autowired
	private TestDao testDao;
	
	@Autowired
	private TestEnvironmentDao testEnvironmentDao;
	
	@RequestMapping(value="/results", method=RequestMethod.GET)
	public Page<TestRun> getPerformanceReports(@RequestParam(value="testId", required=false) Integer testId, Pageable pageable)
	{
		if (testId != null)
		{
			final Test test = testDao.findOne(testId);
			if (test != null)
			{
				return this.testResultDao.findAllByTestId(test.getId(), pageable);
			}
			throw new EmptyResultDataAccessException("No test with ID " + testId, 1);
		}
		return this.testResultDao.findAll(pageable);
	}
	
	@RequestMapping(value="/tests", method=RequestMethod.GET)
	public Page<Test> getTests(@RequestParam(value="nameFilter", required=false) String nameFilter, Pageable pageable)
	{
		return  testDao.findAll(pageable);
	}

	@RequestMapping(value="/results", method=RequestMethod.POST)
	public void addResult(@RequestBody TestResultDto testResultDto)
	{
		final String name = testResultDto.getTestName();
		final Test test = ensureTestExists(name);
		
		TestPerformance savedPerformance = null;
		if (testResultDto.getPerformance() != null)
		{
			savedPerformance = this.performanceResultDao.save(new TestPerformance(testResultDto.getPerformance()));
		}
		
		final TestRun testRun = new TestRun();
		testRun.setSuccess(testResultDto.isSuccess());
		testRun.setTimestamp(testResultDto.getTimestamp());
		testRun.setTest(test);
		testRun.setPerformance(savedPerformance);
		this.testResultDao.save(testRun);
			
	}

	private Test ensureTestExists(String name)
	{
		final Test byName = this.testDao.findByName(name);
		if (byName == null)
		{
			final Test test = new Test();
			test.setName(name);
			return testDao.save(test);
		}
		return byName;
	}
}
