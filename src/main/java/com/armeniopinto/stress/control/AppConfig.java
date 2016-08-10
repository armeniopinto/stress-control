/**
 * AppConfig.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author armenio.pinto
 */
@Configuration
public class AppConfig {

	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor taskExecutor() {
		final ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(4);
		pool.setMaxPoolSize(4);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		pool.initialize();

		return pool;
	}
}