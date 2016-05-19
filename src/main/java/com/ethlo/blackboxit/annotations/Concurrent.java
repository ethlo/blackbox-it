package com.ethlo.blackboxit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes it possible to run any test as a multi-threaded/concurrent test to possibly reveal race-conditions and dead-locks. 
 * 
 * @author Morten Haraldsen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Concurrent
{
	/**
	 * The number of parallel threads to fork. Default is 4
	 * @return
	 */
	int threads() default 4;

	/**
	 * The number of times that each thread should repeat the test
	 * @return
	 */
	int repeats() default 1;

	/**
	 * Number of times that each thread should repeat the test before starting the actual testing
	 * @return
	 */
	int warmupRuns() default 0;
}
