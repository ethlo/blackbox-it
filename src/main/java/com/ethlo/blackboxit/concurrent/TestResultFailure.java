package com.ethlo.blackboxit.concurrent;

import org.junit.internal.runners.model.EachTestNotifier;

public class TestResultFailure extends TestResult
{
	private final Throwable error;

	public TestResultFailure(Throwable error)
	{
		this.error = error;
	}

	@Override
	public void addTestNotifier(EachTestNotifier notifier)
	{
		notifier.addFailure(error);
	}
}