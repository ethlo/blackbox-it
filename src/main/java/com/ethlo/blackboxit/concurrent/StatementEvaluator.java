package com.ethlo.blackboxit.concurrent;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;

public class StatementEvaluator
{
	public static TestResult evaluateStatement(Statement statement)
	{
		try
		{
			statement.evaluate();
		}
		catch (AssumptionViolatedException e)
		{
			return new TestResultAssumptionViolated(e);
		}
		catch (Throwable e) {
			return new TestResultFailure(e);
		}
		return new TestResultSuccess();
	}
}