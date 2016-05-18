package com.ethlo.blackboxit.concurrent;

import org.junit.runners.model.Statement;
import org.springframework.util.StopWatch;

public class ConcurrentStatement extends Statement
{
	public static final String WARMUP_STAGE_NAME = "warmup";
	public static final String RUN_STAGE_NAME_PREFIX = "run ";
	
	private final Statement statement;
	
	// Test configuration
	private final int repeats;
	private final int warmupRuns;
	private final int concurrency;
	
	// Timing
	private final StopWatch stopWatch = new StopWatch();
	
	public ConcurrentStatement(Statement statement, int repeats, int warmupRuns, int concurrency)
	{
		this.statement = statement;
		this.repeats = repeats;
		this.warmupRuns = warmupRuns;
		this.concurrency = concurrency;
	}
	
	@Override
	public void evaluate() throws Exception
	{
		warmup();
		runTest();
	}

	private void runTest() throws Exception
	{
		for (int i = 0; i < repeats; i++)
		{
			stopWatch.start(RUN_STAGE_NAME_PREFIX + Integer.toString(i));
			doRun();
		}
	}

	private void warmup() throws Exception
	{
		if (warmupRuns > 0)
		{
			stopWatch.start(WARMUP_STAGE_NAME);
			doRun();
		}
	}
	
	private void doRun() throws Exception
	{
		try
		{
			statement.evaluate();
		}
		catch (Exception e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
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