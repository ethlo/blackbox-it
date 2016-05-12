
package com.ethlo.blackboxit.concurrent;

import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;

public class TestResultAssumptionViolated extends TestResult
{
	private final AssumptionViolatedException error;

	public TestResultAssumptionViolated(AssumptionViolatedException error)
	{
		this.error = error;
	}

	@Override
	public void addTestNotifier(EachTestNotifier notifier)
	{
		notifier.addFailedAssumption(error);
	}
}