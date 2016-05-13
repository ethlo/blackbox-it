package com.ethlo.blackboxit.reporting;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;

import org.junit.runners.model.FrameworkMethod;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;

import com.ethlo.blackboxit.concurrent.Concurrent;
import com.ethlo.blackboxit.concurrent.ConcurrentStatement;

public class ReportGenerator
{
	public static PerformanceReport createPerformanceReport(FrameworkMethod method, final List<ConcurrentStatement> concurrentStatements)
	{
		if (method.getAnnotation(Concurrent.class) == null)
		{
			return null;
		}
		
		long total = 0;
		int invocations = 0;
		final List<Long> taskTimings = new LinkedList<>();
		for (ConcurrentStatement st : concurrentStatements)
		{
			final StopWatch.TaskInfo[] tasks = st.getStopWatch().getTaskInfo();
			for (TaskInfo task : tasks)
			{
				if (task.getTaskName().startsWith(ConcurrentStatement.RUN_STAGE_NAME_PREFIX))
				{
					final long taskTime = task.getTimeMillis();
					taskTimings.add(taskTime);
					total += taskTime;
					invocations += 1;
				}
			}
		}

		Collections.sort(taskTimings);
		final long median = taskTimings.get((taskTimings.size() - 1) / 2);
		
        final LongSummaryStatistics stats = taskTimings.stream().mapToLong((x) -> x).summaryStatistics();
        final long min = stats.getMin();
        final long max = stats.getMax();
		
        final List<Long> squared = taskTimings.stream().map(i -> ((long)Math.pow(i - stats.getAverage(), 2))).collect(Collectors.toList());
        final double stdDeviation = Math.sqrt(squared.stream().mapToLong((x) -> x).summaryStatistics().getAverage());
        
		final ConcurrentStatement st = concurrentStatements.iterator().next();
		
		final PerformanceReport report = new PerformanceReport();
		report.setAverage(stats.getAverage());
		report.setMedian(median);
		report.setStandardDeviation(stdDeviation);
		report.setConcurrency(concurrentStatements.size());
		report.setInvocations(invocations);
		report.setMax(max);
		report.setMedian(median);
		report.setMin(min);
		report.setRepeats(st.getRepeats());
		report.setWarmupRuns(st.getWarmupRuns());
		report.setTotal(total);
		report.setTestName(method.toString());
		
		return report;
	}
}
