package com.ethlo.blackboxit.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define group for test. This can be used for application level or sub-application level grouping
 * 
 * @author Morten Haraldsen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Tag{
	String[] value();
}