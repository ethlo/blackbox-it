package com.ethlo.blackboxit;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import com.ethlo.blackboxit.AbstractTest.Cfg;
import com.ethlo.blackboxit.reporting.JsonDumpReportingListener;
import com.ethlo.blackboxit.reporting.LogbackReportingListener;
import com.ethlo.blackboxit.reporting.ReportingListener;

import groovy.util.GroovyTestCase;

@RunWith(BlackboxTestRunner.class)
@ContextConfiguration(classes=Cfg.class)
@PropertySource(value="classpath:application.properties")
public abstract class AbstractTest extends GroovyTestCase
{
	protected static final Logger logger = LoggerFactory.getLogger(AbstractTest.class);
	
	@EnableAutoConfiguration
	@PropertySource("classpath:application.properties")
	public static class Cfg
	{
		@Bean
		@ConditionalOnProperty(name="blackbox-it.log", matchIfMissing=true)
		public ReportingListener logbackReportingListener()
		{
			return new LogbackReportingListener();
		}
		
		@Bean
		@ConditionalOnProperty(name="blackbox-it.log.json")
		public JsonDumpReportingListener jsonReportingListener(@Value(value="${blackbox-it.log.json.directory}") String path)
		{
			return new JsonDumpReportingListener(path);
		}
	}
}