package com.ethlo.blackboxit;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;

import com.ethlo.blackboxit.concurrent.Concurrent;
import com.ethlo.blackboxit.concurrent.ConcurrentRunnable;
import com.ethlo.blackboxit.concurrent.ConcurrentStatement;
import com.ethlo.blackboxit.concurrent.EmptyStatement;
import com.ethlo.blackboxit.concurrent.StatementEvaluator;
import com.ethlo.blackboxit.concurrent.StatementList;
import com.ethlo.blackboxit.concurrent.TestResult;

public class BlackboxTestRunner extends SpringJUnit4ClassRunner
{
	private static final Logger logger = LoggerFactory.getLogger(BlackboxTestRunner.class);
	
	public BlackboxTestRunner(Class<?> clazz) throws InitializationError
	{
		super(clazz);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
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
			public void evaluate() {
				runChildrenConcurrently(notifier);
			}
		};
	}

	private void runChildrenConcurrently(final RunNotifier notifier)
	{
		final List<EachTestNotifier> notifiers = new LinkedList<EachTestNotifier>();
		
		try
		{
			final Object test = new ReflectiveCallable()
			{
				@Override
				protected Object runReflectiveCall() throws Throwable
				{
					return createTest();
				}
			}.run();
			
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
					notifier.fireTestStarted(description);
					executeInPool(concurrentStatements);
					for (ConcurrentStatement st : concurrentStatements)
					{
						st.addFailures();
					}
					evaluateStatement(createAfters(test), notifiers);
					logPerformanceReport(concurrentStatements);
					notifier.fireTestFinished(description);
				}
			}
		}
		catch (Throwable e)
		{
			for (FrameworkMethod method : getChildren())
			{
				final Description description = describeChild(method);
				notifier.fireTestFailure(new Failure(description, e));
			}
		}
	}

	private void executeInPool(final List<ConcurrentStatement> concurrentStatements) {
		final ExecutorService pool = Executors.newFixedThreadPool(concurrentStatements.size());
		for (ConcurrentStatement st : concurrentStatements)
		{
			final ConcurrentRunnable runnable = new ConcurrentRunnable(st);
			pool.submit(runnable);
		}
		pool.shutdown();
		try
		{
			pool.awaitTermination(100, TimeUnit.SECONDS);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	private List<ConcurrentStatement> createSingleConcurrentTest(FrameworkMethod method, final RunNotifier notifier, final List<EachTestNotifier> eachTestNotifierList, Object test)
	{
		final Description description = describeChild(method);
	
		final EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
		eachNotifier.fireTestStarted();
		eachTestNotifierList.add(eachNotifier);
		final Statement st = createMethodStatement(method, test);
		
		final Concurrent concurrentAnnotation = method.getAnnotation(Concurrent.class);
		if (concurrentAnnotation != null)
		{
			return doCreateConcurrent(eachNotifier, st, concurrentAnnotation);
		}
		else
		{
			return Collections.singletonList(new ConcurrentStatement(st, eachNotifier, 1, 0, 1));
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
			concurrentStatements.add(new ConcurrentStatement(st, eachNotifier, repeats, warmupRuns, threads));
		}
		return concurrentStatements;
	}

	private void logPerformanceReport(final List<ConcurrentStatement> concurrentStatements)
	{
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		long total = 0;
		int invocations = 0;
		final List<Long> median = new LinkedList<>();
		for (ConcurrentStatement st : concurrentStatements)
		{
			final StopWatch.TaskInfo[] tasks = st.getStopWatch().getTaskInfo();
			for (TaskInfo task : tasks)
			{
				if (task.getTaskName().startsWith(ConcurrentStatement.RUN_STAGE_NAME_PREFIX))
				{
					final long taskTime = task.getTimeMillis();
					
					min = Math.min(min, taskTime);
					max = Math.max(max, taskTime);
					median.add(taskTime);
					total += taskTime;
					invocations += 1;
				}
			}
		}
		if (! median.isEmpty())
		{
			Collections.sort(median);
			final long medianValue = median.get((median.size() - 1) / 2);
			final long averageValue = (long) (total / (double) median.size());
			
			final ConcurrentStatement st = concurrentStatements.iterator().next();
			
			logger.info("\n*** Total tests execution results across all threads ***"
				+ "\nWarmup: \t" + formatNum(st.getWarmupRuns()) 
				+ "\nRepeats: \t" + formatNum(st.getRepeats())
				+ "\nConcurrency: \t" + formatNum(st.getConcurrency()) 
				+ "\nInvocations: \t" + formatNum(invocations) 
				+ "\nMin: \t\t" + formatNum(min) + " ms" 
				+ "\nMax: \t\t" + formatNum(max) + " ms" 
				+ "\nMedian: \t" + formatNum(medianValue) + " ms" 
				+ "\nAverage: \t" + formatNum(averageValue) + " ms"
				+ "\nTotal: \t\t" + formatNum(total) + " ms");
		}
	}

	private String formatNum(Number num)
	{
		final DecimalFormat dec = new DecimalFormat();     
		dec.setGroupingUsed(true);
		return StringUtils.leftPad(dec.format(Long.parseLong(num.toString())), 6, ' ');
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
		final TestResult testResult = StatementEvaluator.evaluateStatement(statement);
		for (EachTestNotifier eachTestNotifier : eachTestNotifierList)
		{
			testResult.addTestNotifier(eachTestNotifier);
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
