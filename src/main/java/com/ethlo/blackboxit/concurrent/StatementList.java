package com.ethlo.blackboxit.concurrent;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class StatementList extends Statement
{
    private final Object target;
    private final List<FrameworkMethod> befores;

    public StatementList(List<FrameworkMethod> befores, Object target)
    {
        this.befores = befores;
        this.target = target;
    }

    @Override
    public void evaluate() throws Throwable
    {
        for (FrameworkMethod before : befores)
        {
            before.invokeExplosively(target);
        }
    }
}