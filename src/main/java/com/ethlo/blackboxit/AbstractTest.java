package com.ethlo.blackboxit;

import static com.jayway.restassured.RestAssured.basic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Stopwatch;
import org.junit.rules.TestName;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

import com.ethlo.blackboxit.AbstractTest.Cfg;
import com.jayway.restassured.RestAssured;

import groovy.util.GroovyTestCase;

@RunWith(BlackboxTestRunner.class)
@ContextConfiguration(classes=Cfg.class)
@PropertySource(value="classpath:application.properties")
public abstract class AbstractTest extends GroovyTestCase
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractTest.class);
	
	private static volatile boolean lastWasReadOnly = false;
	
	@Autowired
	private DataSource dataSource;
	
	@Value("${tests.base-uri}")
	private String baseURI;
	
	@Value("${tests.auth.defaultusername}")
	private String defaultUsername;
	
	@Value("${tests.auth.defaultpassword}")
	private String defaultPassword;
	
	@Rule public TestName name = new TestName();
	
    @Rule
    public Stopwatch stopwatch = new Stopwatch()
    {
        @Override
        protected void finished(long nanos, Description description)
        {
        	lastWasReadOnly = description.getAnnotation(ReadOnly.class) != null;
            //logger.info("Finished test {} - {} in {} ms", getClass().getSimpleName(), name.getMethodName(), TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS));
        }
    };
	
	private Resource[] scripts;
	private String baseUrl;

	protected AbstractTest()
	{
		this(new ClassPathResource("testdata.sql"));
	}
	
	protected AbstractTest(Resource... scripts)
	{
		this.scripts = scripts;
	}
	
	@Before
	public void resetDb()
	{
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.baseURI = baseURI;
		RestAssured.authentication = basic(defaultUsername, defaultPassword);
	
		if (! lastWasReadOnly)
		{
			logger.debug("Last was not read-only, resetting test-data");
			new ResourceDatabasePopulator(scripts).execute(dataSource);
		}
		
		//logger.info("Starting test " + getClass().getSimpleName() + " - " + name.getMethodName());
	}
	
	protected String requestBody()
	{
		final Path path = Paths.get(getClass().getSimpleName().toLowerCase() + "/" + name.getMethodName() + "-req.json");
		final ClassPathResource res = new ClassPathResource(path.toString());
		try
		{
			return IOUtils.toString(res.getInputStream(), StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Unable to load body for method " + name.getMethodName(), e);
		}
	}
	
	protected String fullUrl(String relUrl)
	{
		return baseUrl + relUrl;
	}
	
	@EnableAutoConfiguration
	@PropertySource("classpath:application.properties")
	public static class Cfg
	{
		
	}
}
