package com.ethlo.blackboxit.concurrent;

import java.util.concurrent.Callable;

public class ConcurrentCallable implements Callable<Void>
{
	private final ConcurrentStatement concurrentStatement;
	
	public ConcurrentCallable(ConcurrentStatement concurrentStatement)
	{
		this.concurrentStatement = concurrentStatement;
	}

	@Override
	public Void call() throws Exception
	{
		concurrentStatement.evaluate();
		return null;
	}
}