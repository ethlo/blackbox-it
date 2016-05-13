package com.ethlo.blackboxit.fixture;

import javax.sql.DataSource;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;

public class DataBaseScriptDataHandler implements DataHandler
{
	private DataSource dataSource;
	private Resource[] scripts;

	public DataBaseScriptDataHandler(DataSource dataSource, Resource... scripts)
	{
		this.dataSource = dataSource;
		Assert.notEmpty(scripts, "scripts cannot be empty");
		this.scripts = scripts;
	}
	
	
	@Override
	public void loadInitial()
	{
		
	}

	@Override
	public void reset()
	{
		new ResourceDatabasePopulator(scripts).execute(dataSource);
	}
}
