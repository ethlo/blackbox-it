package com.ethlo.blackboxit.server.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ethlo.blackboxit.model.TestPerformance;

public interface PerformanceResultDao extends PagingAndSortingRepository<TestPerformance, Integer>
{
}
