package com.ethlo.blackboxit.concurrent;

public class ConcurrentRunnable implements Runnable
{
	private final ConcurrentStatement concurrentStatement;
	
	public ConcurrentRunnable(ConcurrentStatement concurrentStatement)
	{
		this.concurrentStatement = concurrentStatement;
	}

	@Override
	public void run()
	{
		try
		{
			concurrentStatement.evaluate();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}