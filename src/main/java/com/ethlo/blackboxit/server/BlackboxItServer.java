package com.ethlo.blackboxit.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ethlo.blackboxit.model.TestPerformance;
import com.ethlo.blackboxit.server.cfg.WebSecurityCfg;

@SpringBootApplication
@EntityScan(basePackageClasses=TestPerformance.class)
@EnableTransactionManagement
@Import(WebSecurityCfg.class)
public class BlackboxItServer
{
	public static void main(String[] args)
	{
		SpringApplication.run(BlackboxItServer.class);
	}
}
