package com.ethlo.blackboxit.server.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ethlo.blackboxit.model.TestRun;

public interface TestRunDao extends PagingAndSortingRepository<TestRun, Integer>
{
	Page<TestRun> findAllByTestId(Integer testId, Pageable pageable);
}
