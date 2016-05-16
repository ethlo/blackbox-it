package com.ethlo.blackboxit.server.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ethlo.blackboxit.model.Test;

public interface TestDao extends PagingAndSortingRepository<Test, Integer>
{
	Test findByName(String testName);
}
