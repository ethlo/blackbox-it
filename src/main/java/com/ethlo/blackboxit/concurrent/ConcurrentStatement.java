package com.ethlo.blackboxit.concurrent;

import java.util.LinkedList;
import java.util.List;

import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runners.model.Statement;
import org.springframework.util.StopWatch;

public class ConcurrentStatement extends Statement
{
	public static final String WARMUP_STAGE_NAME = "warmup";
	public static final String RUN_STAGE_NAME_PREFIX = "run ";
	
	private final Statement statement;
	private final  EachTestNotifier eachTestNotifier;
	private final List<TestResult> testResults = new LinkedList<>();
	
	// Test configuration
	private final int repeats;
	private final int warmupRuns;
	private final int concurrency;
	
	// Timing
	private final StopWatch stopWatch = new StopWatch();
	
	public ConcurrentStatement(Statement statement, EachTestNotifier eachTestNotifier, int repeats, int warmupRuns, int concurrency)
	{
		this.statement = statement;
		this.eachTestNotifier = eachTestNotifier;
		this.repeats = repeats;
		this.warmupRuns = warmupRuns;
		this.concurrency = concurrency;
	}
	
	public void addFailures()
	{
		for (TestResult testResult : testResults)
		{
			testResult.addTestNotifier(eachTestNotifier);
		}
	}
	
	@Override
	public void evaluate()
	{
		warmup();
		runTest();
	}

	private void runTest()
	{
		for (int i = 0; i < repeats; i++)
		{
			stopWatch.start(RUN_STAGE_NAME_PREFIX + Integer.toString(i));
			final TestResult testResult = StatementEvaluator.evaluateStatement(statement);
			if (! (testResult instanceof TestResultSuccess))
			{
				stopWatch.stop();
				addFailures();
				return;
			}
			stopWatch.stop();
			testResults.add(testResult);
		}
	}

	private void warmup()
	{
		if (warmupRuns > 0)
		{
			stopWatch.start(WARMUP_STAGE_NAME);
			for (int i = 0; i < warmupRuns; i++)
			{
				final TestResult testResult = StatementEvaluator.evaluateStatement(statement);
				if (! (testResult instanceof TestResultSuccess))
				{
					stopWatch.stop();
					addFailures();
					return;
				}
				testResults.add(testResult);
			}
			stopWatch.stop();
		}
	}
	
	public StopWatch getStopWatch()
	{
		return this.stopWatch;
	}

	public int getRepeats()
	{
		return repeats;
	}

	public int getWarmupRuns()
	{
		return warmupRuns;
	}

	public int getConcurrency()
	{
		return concurrency;
	}
}