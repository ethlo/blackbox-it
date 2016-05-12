package com.ethlo.blackboxit.concurrent;

import org.junit.runners.model.Statement;

public class EmptyStatement extends Statement
{
	@Override
	public void evaluate() throws Throwable{}
}