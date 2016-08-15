/**
 * AppStarter.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author armenio.pinto
 */
@SpringBootApplication
@EnableScheduling
public class AppStarter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppStarter.class);

	public static void main(final String[] args) {
		SpringApplication.run(AppStarter.class, args);
	}

	@PreDestroy
	public void mensagem() {
		LOGGER.info("\nE outra vez conquistaremos a Distância -\n"
				+ "Do mar ou outra, mas que seja nossa!");
	}

}
