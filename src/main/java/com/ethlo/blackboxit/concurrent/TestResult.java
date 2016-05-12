package com.ethlo.blackboxit.concurrent;

import org.junit.internal.runners.model.EachTestNotifier;

public abstract class TestResult
{
	public abstract void addTestNotifier(EachTestNotifier notifier);
}