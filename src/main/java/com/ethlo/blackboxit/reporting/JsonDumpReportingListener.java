package com.ethlo.blackboxit.reporting;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.junit.runners.model.FrameworkMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDumpReportingListener extends ReportingAdapter
{
	private final String targetPath;
	private final ObjectMapper mapper = new ObjectMapper();
	
	public JsonDumpReportingListener(String targetPath)
	{
		this.targetPath = targetPath;
	}
	
	@Override
	public void fireConcurrentTestFinished(Object test, FrameworkMethod method, boolean success, Date time, PerformanceReport report)
	{
		final Path path = Paths.get(targetPath, method.getName() + ".json");
		path.getParent().toFile().mkdirs();
		try
		{
			mapper.writeValue(path.toFile(), report);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
