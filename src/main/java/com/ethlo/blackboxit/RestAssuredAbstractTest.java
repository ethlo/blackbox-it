package com.ethlo.blackboxit;

import static com.jayway.restassured.RestAssured.basic;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;

import com.jayway.restassured.RestAssured;

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
	}
}
