package com.ethlo.blackboxit.reporting;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	public void fireTestFinished(TestResult testResult)
	{
		final Path path = Paths.get(targetPath, testResult.getDescription().getDisplayName() + ".json");
		path.getParent().toFile().mkdirs();
		try
		{
			mapper.writeValue(path.toFile(), testResult);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
