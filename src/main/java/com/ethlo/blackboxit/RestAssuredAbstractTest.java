package com.ethlo.blackboxit;

import static io.restassured.RestAssured.basic;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;

public abstract class RestAssuredAbstractTest extends TestFixtureAwareAbstractTest
{
	@Value("${tests.base-uri}")
	private String baseURI;
	
	@Value("${tests.auth.defaultusername}")
	private String defaultUsername;
	
	@Value("${tests.auth.defaultpassword}")
	private String defaultPassword;
		
	@Before
	public void resetRestAssured()
	{
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.baseURI = baseURI;
		RestAssured.authentication = basic(defaultUsername, defaultPassword);
		RestAssured.config = RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames().relaxedHTTPSValidation()); 
	}
}
