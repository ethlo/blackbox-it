package com.ethlo.blackboxit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.statements.Fail;
import org.junit.rules.MethodRule;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DefaultTestContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import com.ethlo.blackboxit.concurrent.Concurrent;
import com.ethlo.blackboxit.concurrent.ConcurrentCallable;
import com.ethlo.blackboxit.concurrent.ConcurrentStatement;
import com.ethlo.blackboxit.concurrent.EmptyStatement;
import com.ethlo.blackboxit.concurrent.StatementList;
import com.ethlo.blackboxit.reporting.PerformanceReport;
import com.ethlo.blackboxit.reporting.ReportGenerator;
import com.ethlo.blackboxit.reporting.ReportingListener;

public class BlackboxTestRunner extends SpringJUnit4ClassRunner
{
	public BlackboxTestRunner(Class<?> clazz) throws InitializationError
	{
		super(clazz);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods()
	{
		final List<FrameworkMethod> list = new ArrayList<>(super.computeTestMethods());
		Collections.sort(list, READ_ONLY_FIRST);
		return list;
	}

	@Override
	protected Statement childrenInvoker(final RunNotifier notifier)
	{
		return new Statement()
		{
			@Override
			public void evaluate()
			{
				runTestMethods(notifier);
			}
		};
	}

	private void runTestMethods(final RunNotifier notifier)
	{
		final List<EachTestNotifier> notifiers = new LinkedList<EachTestNotifier>();
		
		final Object test = getTestInstance();
		
		// Get application context
		final TestContextManager testCtx = getTestContextManager();
		final DefaultTestContext dtc = (DefaultTestContext) ReflectionTestUtils.getField(testCtx, "testContext");
		final Map<String, ReportingListener> reportingListeners = dtc.getApplicationContext().getBeansOfType(ReportingListener.class);

		reportingListeners.values().forEach(v ->{v.started(test);});
		
		for (FrameworkMethod method : getChildren())
		{
			final Description description = describeChild(method);
			
			if (method.getAnnotation(Ignore.class) != null)
			{
				notifier.fireTestIgnored(description);
			}
			else
			{
				final List<ConcurrentStatement> concurrentStatements = createSingleConcurrentTest(method, notifier, notifiers, test);
				evaluateStatement(createBefores(test), notifiers);
				
				// Mark test started
				notifier.fireTestStarted(description);
				reportingListeners.values().forEach(v ->{v.fireTestStarted(description);});
				
				final AtomicBoolean success = new AtomicBoolean(true);
				try
				{
					executeInPool(concurrentStatements);
				}
				catch (Throwable exc)
				{
					final Throwable cause = ExceptionUtils.getRootCause(exc) != null ? ExceptionUtils.getRootCause(exc) : exc;
					
					notifier.fireTestFailure(new Failure(description, cause));
					notifiers.forEach(n->{n.fireTestFinished();});
					reportingListeners.values().forEach(v ->{v.fireTestFailure(description, cause);});
					success.set(false);
				}
				
				final Date date = new Date();
				evaluateStatement(createAfters(test), notifiers);
				
				// Mark test finished
				notifier.fireTestFinished(description);
				reportingListeners.values().forEach(v ->{v.fireTestFinished(description);});
				
				// Log performance report
				if (success.get())
				{
					final PerformanceReport report = ReportGenerator.createPerformanceReport(method, concurrentStatements);
					if (report != null)
					{
						reportingListeners.values().forEach(v ->{v.fireConcurrentTestFinished(test, method, success.get(), date, report);});
					}
				}
			}
		}
	}

	private Object getTestInstance()
	{
		try
		{
			return super.createTest();
		}
		catch (Exception exc)
		{
			throw new RuntimeException(exc);
		}
	}

	private void executeInPool(final List<ConcurrentStatement> concurrentStatements) throws Throwable
	{
		final ExecutorService pool = Executors.newFixedThreadPool(concurrentStatements.size());
		final List<ConcurrentCallable> runnables = new ArrayList<>(concurrentStatements.size());
		for (ConcurrentStatement st : concurrentStatements)
		{
			runnables.add(new ConcurrentCallable(st));
		}

		try
		{
			final List<Future<Void>> answers = pool.invokeAll(runnables);
			for (Future<Void> f : answers)
			{
				f.get();
			}
		}
		catch (ExecutionException e)
		{
			throw e.getCause();
		}
		finally
		{
			pool.shutdown();
		}
	}

	private List<ConcurrentStatement> createSingleConcurrentTest(FrameworkMethod method, final RunNotifier notifier, final List<EachTestNotifier> eachTestNotifierList, Object test)
	{
		final Description description = describeChild(method);
	
		final EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
		eachTestNotifierList.add(eachNotifier);
		final Statement st = createMethodStatement(method, test);
		
		final Concurrent concurrentAnnotation = method.getAnnotation(Concurrent.class);
		if (concurrentAnnotation != null)
		{
			return doCreateConcurrent(eachNotifier, st, concurrentAnnotation);
		}
		else
		{
			return Collections.singletonList(new ConcurrentStatement(st, 1, 0, 1));
		}
	}

	private List<ConcurrentStatement> doCreateConcurrent(final EachTestNotifier eachNotifier, final Statement st, final Concurrent concurrentAnnotation)
	{
		
		final int threads = concurrentAnnotation.threads();
		final int repeats = concurrentAnnotation.repeats();
		final int warmupRuns = concurrentAnnotation.warmupRuns();
		
		Assert.isTrue(threads > 0, "threads must be 1 or higher");
		Assert.isTrue(repeats > 0, "repeats must be 1 or higher");
		Assert.isTrue(warmupRuns >= 0, "warmupRuns must be 0 or higher");
		
		final List<ConcurrentStatement> concurrentStatements = new ArrayList<>();
		for (int i = 0; i < threads; i++)
		{
			concurrentStatements.add(new ConcurrentStatement(st, repeats, warmupRuns, threads));
		}
		return concurrentStatements;
	}



	protected Statement createMethodStatement(FrameworkMethod method, Object test)
	{
		Statement statement;
		try
		{
			statement = methodInvoker(method, test);
			statement = possiblyExpectingExceptions(method, test, statement);
			statement = withRules(method, test, statement);
		}
		catch (Throwable ex)
		{
			return new Fail(ex);
		}
		return statement;
	}

	private Statement withTestRules(FrameworkMethod method, List<TestRule> testRules, Statement statement)
	{
		return testRules.isEmpty() ? statement : new RunRules(statement, testRules, describeChild(method));
	}

	private Statement withRules(FrameworkMethod method, Object target, final Statement statement)
	{
		final List<TestRule> testRules = getTestRules(target);
		Statement result = statement;
		result = withMethodRules(method, testRules, target, result);
		result = withTestRules(method, testRules, result);
		return result;
	}

	private Statement withMethodRules(FrameworkMethod method, List<TestRule> testRules, Object target, Statement result)
	{
		for (MethodRule each : rules(target))
		{
			if (! testRules.contains(each)) {
				result = each.apply(result, method, target);
			}
		}
		return result;
	}

	protected Statement createBefores(Object target)
	{
		List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(Before.class);
		return befores.isEmpty() ? new EmptyStatement() : new StatementList(befores, target);
	}

	protected Statement createAfters(Object target)
	{
		List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(After.class);
		return afters.isEmpty() ? new EmptyStatement() : new StatementList(afters, target);
	}

	private void evaluateStatement(Statement statement, List<EachTestNotifier> eachTestNotifierList)
	{
		try {
			statement.evaluate();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final Comparator<FrameworkMethod> READ_ONLY_FIRST = new Comparator<FrameworkMethod>()
	{
		public int compare(FrameworkMethod m1, FrameworkMethod m2) {
			if (m1.getAnnotation(ReadOnly.class) != null && m2.getAnnotation(ReadOnly.class) == null)
			{
				return -1;
			}
			else if (m1.getAnnotation(ReadOnly.class) == null && m2.getAnnotation(ReadOnly.class) != null)
			{
				return 1;
			}
			return 0;
		}
	};
}
