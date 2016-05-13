package com.ethlo.blackboxit;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.ethlo.blackboxit.fixture.DataHandler;

public abstract class TestFixtureAwareAbstractTest extends AbstractTest
{
	private static volatile boolean lastWasReadOnly = false;
	
	@Autowired
	private ApplicationContext ctx;
	
    @Rule
    public Stopwatch stopwatch = new Stopwatch()
    {
        @Override
        protected void finished(long nanos, Description description)
        {
        	lastWasReadOnly = description.getAnnotation(ReadOnly.class) != null;
        }
    };
	
	@Before
	public final void reset()
	{	
		final Map<String, DataHandler> handlers = ctx.getBeansOfType(DataHandler.class);
		
		if (! lastWasReadOnly)
		{
			handlers.values().forEach(h->{h.reset();});
			doReset();
		}
	}
	
	protected void doReset()
	{
		
	}
}
