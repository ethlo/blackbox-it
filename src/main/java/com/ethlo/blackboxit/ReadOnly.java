package com.ethlo.blackboxit;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker to indicate that the test method does not modify the data, so reloading the test data is unnecessary.
 * 
 * @author Morten Haraldsen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReadOnly{}