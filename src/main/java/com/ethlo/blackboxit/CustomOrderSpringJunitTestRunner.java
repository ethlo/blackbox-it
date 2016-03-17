package com.ethlo.blackboxit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class CustomOrderSpringJunitTestRunner extends SpringJUnit4ClassRunner
{
	public static final Comparator<FrameworkMethod> READ_ONLY_FIRST = new Comparator<FrameworkMethod>()
	{
        public int compare(FrameworkMethod m1, FrameworkMethod m2)
        {
        	if (m1.getAnnotation(ReadOnly.class) != null && m2.getAnnotation(ReadOnly.class) == null)
        	{
        		return -1;
        	} else if (m1.getAnnotation(ReadOnly.class) == null && m2.getAnnotation(ReadOnly.class) != null)
        	{
        		return 1;
        	}
        	return 0;
        }
    };
	
	public CustomOrderSpringJunitTestRunner(Class<?> clazz) throws InitializationError
	{
		super(clazz);
	}
	
	@Override
    protected List<FrameworkMethod> computeTestMethods()
    {
        final List<FrameworkMethod> list = new ArrayList<>(super.computeTestMethods());
        Collections.sort(list, READ_ONLY_FIRST);
        return list;
    }
}
