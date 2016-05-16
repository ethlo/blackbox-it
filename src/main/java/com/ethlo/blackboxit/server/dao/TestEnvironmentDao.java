package com.ethlo.blackboxit.server.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ethlo.blackboxit.model.TestEnvironment;

public interface TestEnvironmentDao extends PagingAndSortingRepository<TestEnvironment, Integer>
{
}
