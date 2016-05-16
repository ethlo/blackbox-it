package com.ethlo.blackboxit.client;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.runners.model.FrameworkMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.ethlo.blackboxit.reporting.PerformanceReport;
import com.ethlo.blackboxit.reporting.ReportingAdapter;
import com.ethlo.blackboxit.server.TestResultDto;

public class HttpResultReporter extends ReportingAdapter
{
	private final String url;
	private RestTemplate restTemplate;
	
	public HttpResultReporter(String url)
	{
		this.url = url;
		this.restTemplate = new RestTemplate();
	}
	
	@Override
	public void fireConcurrentTestFinished(Object test, FrameworkMethod method, boolean success, Date time, PerformanceReport performanceReport)
	{
		final TestResultDto testResult = new TestResultDto();
		testResult.setTestName(method.getName());
		testResult.setPerformance(performanceReport);
		testResult.setTimestamp(time);
		testResult.setSuccess(success);
		
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + Base64.encodeBase64String("user:password".getBytes(StandardCharsets.UTF_8)));
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		final HttpEntity<TestResultDto> entity = new HttpEntity<>(testResult, headers);
		restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
	}
}
