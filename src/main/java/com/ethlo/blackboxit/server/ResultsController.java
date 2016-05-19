package com.ethlo.blackboxit.server;

import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ethlo.blackboxit.model.Test;
import com.ethlo.blackboxit.model.TestPerformance;
import com.ethlo.blackboxit.model.TestRun;
import com.ethlo.blackboxit.model.TestSearchSpecification;
import com.ethlo.blackboxit.server.dao.PerformanceResultDao;
import com.ethlo.blackboxit.server.dao.TestDao;
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
	
	//@Autowired
	//private TestEnvironmentDao testEnvironmentDao;
	
	@RequestMapping(value="/results", method=RequestMethod.GET)
	public Page<TestRun> getResults(@RequestParam(value="testId", required=false) Integer testId, Pageable pageable)
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
	
	@RequestMapping(value="/results/performance", method=RequestMethod.GET)
	public Page<TestPerformance> getPerformanceResults(@RequestParam(value="testId") int testId, @PageableDefault(sort="name|asc") Pageable pageable)
	{
		final Test test = testDao.findOne(testId);
		if (test != null)
		{
			return this.performanceResultDao.findAllByTestId(test.getId(), pageable);
		}
		throw new EmptyResultDataAccessException("No test with ID " + testId, 1);
	}

	
	@RequestMapping(value="/tests", method=RequestMethod.GET)
	public Page<Test> getTests(
		@RequestParam(value="nameFilter", required=false) String nameFilter,
		@RequestParam(value="tagFilter", required=false) String tagFilter,
		Pageable pageable)
	{
		final TestSearchSpecification tss = new TestSearchSpecification();
		tss.setName(nameFilter);
		tss.setTag(tagFilter);
		
		return  testDao.findAll(tss, pageable);
	}

	@RequestMapping(value="/results", method=RequestMethod.POST)
	public void addResult(@RequestBody TestResultDto testResultDto)
	{
		final Test test = ensureTestExists(testResultDto);
		
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

	private Test ensureTestExists(TestResultDto testResultDto)
	{
		final Test byName = this.testDao.findByTestClassAndMethodName(testResultDto.getTestClass(), testResultDto.getMethodName());
		if (byName == null)
		{
			final Test test = new Test();
			final String simpleClassName = FilenameUtils.getName(StringUtils.replace(testResultDto.getTestClass(), ".", "/"));
			test.setName(testResultDto.getName() != null ? testResultDto.getName() : simpleClassName + "." + testResultDto.getMethodName());
			test.setTestClass(testResultDto.getTestClass());
			test.setMethodName(testResultDto.getMethodName());
			test.setRepeats(testResultDto.getRepeats());
			test.setConcurrency(testResultDto.getConcurrency());
			test.setWarmupRuns(testResultDto.getWarmupRuns());
			test.setTags(StringUtils.arrayToCommaDelimitedString(testResultDto.getTags()));
			return testDao.save(test);
		}
		return byName;
	}
}
