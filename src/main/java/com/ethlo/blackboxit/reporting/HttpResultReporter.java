package com.ethlo.blackboxit.reporting;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.ethlo.blackboxit.server.TestResultDto;

public class HttpResultReporter extends ReportingAdapter
{
	private final String url;
	private final String username;
	private final String password;
	
	private RestTemplate restTemplate;
	
	public HttpResultReporter(String url, String username, String password)
	{
		this.url = url;
		this.username = username;
		this.password = password;
		this.restTemplate = new RestTemplate();
	}
	
	@Override
	public void fireTestFinished(TestResult testResult)
	{
		final TestResultDto t = new TestResultDto();
		t.setMethodName(getTestName(testResult));
		
		final Optional<PerformanceReport> perf = testResult.getPerformanceReport();
		t.setPerformance(perf);
		t.setTimestamp(testResult.getTimestamp());
		t.setSuccess(testResult.getError() == null);
		t.setTags(testResult.getTags());
		t.setName(testResult.getName());
		t.setTestClass(testResult.getDescription().getClassName());
		t.setMethodName(testResult.getDescription().getMethodName());
		t.setConcurrency(perf.isPresent() ? perf.get().getConcurrency() : 1);
		t.setRepeats(perf.isPresent() ? perf.get().getRepeats() : 0);
		t.setWarmupRuns(perf.isPresent() ? perf.get().getWarmupRuns() : 0);
		
		final HttpHeaders headers = new HttpHeaders();
		
		if (StringUtils.hasLength(username))
		{
			headers.set("Authorization", "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes(StandardCharsets.UTF_8)));
		}
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		final HttpEntity<TestResultDto> entity = new HttpEntity<>(t, headers);
		restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
	}

	private String getTestName(TestResult testResult)
	{
		return FilenameUtils.getName(StringUtils.replace(testResult.getDescription().getClassName(), ".", "/")) + "." + testResult.getDescription().getMethodName();
	}
}
