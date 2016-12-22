package com.ethlo.blackboxit.server.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ethlo.blackboxit.model.Test;

public interface TestDao extends JpaRepository<Test, Integer>, JpaSpecificationExecutor<Test>
{
	Test findByTestClassAndMethodName(String testClass, String methodName);
}
