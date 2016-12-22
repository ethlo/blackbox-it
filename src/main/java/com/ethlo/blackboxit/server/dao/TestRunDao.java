package com.ethlo.blackboxit.server.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ethlo.blackboxit.model.Test;
import com.ethlo.blackboxit.model.TestRun;

public interface TestRunDao extends PagingAndSortingRepository<TestRun, Integer>
{
	Page<TestRun> findAllByTestId(Integer testId, Pageable pageable);

	@Query("SELECT t FROM TestRun p, Test t WHERE p.test.id = t.id GROUP BY t.id ORDER BY AVG(p.testPerformance.max - p.testPerformance.min) DESC")
	Page<Test> getAnalysisVaryingPerformance(Pageable pageable);
}
