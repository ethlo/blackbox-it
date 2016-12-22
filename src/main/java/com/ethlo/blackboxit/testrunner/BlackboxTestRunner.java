package com.ethlo.blackboxit.testrunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DefaultTestContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.ethlo.blackboxit.annotations.Concurrent;
import com.ethlo.blackboxit.annotations.Name;
import com.ethlo.blackboxit.annotations.ReadOnly;
import com.ethlo.blackboxit.annotations.Tag;
import com.ethlo.blackboxit.concurrent.ConcurrentCallable;
import com.ethlo.blackboxit.concurrent.ConcurrentStatement;
import com.ethlo.blackboxit.concurrent.EmptyStatement;
import com.ethlo.blackboxit.concurrent.StatementList;
import com.ethlo.blackboxit.reporting.PerformanceReport;
import com.ethlo.blackboxit.reporting.ReportGenerator;
import com.ethlo.blackboxit.reporting.ReportingListener;
import com.ethlo.blackboxit.reporting.TestResult;

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
		final ApplicationContext appCtx = dtc.getApplicationContext();
		final String defaultTags = appCtx.getEnvironment().getProperty("blackbox-it.tags.default", "");
		final Map<String, ReportingListener> reportingListeners = appCtx.getBeansOfType(ReportingListener.class);

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
				
				Throwable testError = null;
				try
				{
					executeInPool(concurrentStatements);
				}
				catch (Throwable exc)
				{
					testError = getCause(exc);
					notifier.fireTestFailure(new Failure(description, testError));
					notifiers.forEach(n->{n.fireTestFinished();});
					final Throwable e = testError;
					reportingListeners.values().forEach(v ->{v.fireTestFailure(description, e);});
				}
				
				evaluateStatement(createAfters(test), notifiers);
				
				// Mark test finished
				notifier.fireTestFinished(description);
				
				final String testName = method.getAnnotation(Name.class) != null ? method.getAnnotation(Name.class).value() : null;
				final String[] tags = method.getAnnotation(Tag.class) != null ? method.getAnnotation(Tag.class).value() : new String[0];
				final Set<String> allTags = new TreeSet<>();
				allTags.addAll(Arrays.asList(tags));
				allTags.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(defaultTags)));
				
				final PerformanceReport performanceReport = testError == null ? ReportGenerator.createPerformanceReport(method, concurrentStatements) : null;
				final TestResult result = testError == null ? TestResult.success(testName, allTags, description, performanceReport) : TestResult.error(testName, allTags, description, testError);
				reportingListeners.values().forEach(v ->{v.fireTestFinished(result);});
			}
		}
	}

	private Throwable getCause(Throwable exc)
	{
		final Throwable t = ExceptionUtils.getRootCause(exc);
		return t != null ? t : exc;
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
	
	@Override
	public void filter(Filter filter) throws NoTestsRemainException
	{
		System.out.println(filter);
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
