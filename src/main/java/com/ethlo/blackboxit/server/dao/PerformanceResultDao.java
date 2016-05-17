package com.ethlo.blackboxit.server.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ethlo.blackboxit.model.TestPerformance;

public interface PerformanceResultDao extends PagingAndSortingRepository<TestPerformance, Integer>
{
	@Query("SELECT p FROM TestPerformance p, TestRun t WHERE t.testPerformance.id = p.id AND t.test.id = :id")
	Page<TestPerformance> findAllByTestId(@Param("id") Integer id, Pageable pageable);
}
