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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.armeniopinto.stress.control.sensorimotor.MessageListener;
import com.armeniopinto.stress.control.sensorimotor.SensorimotorAgent;

/**
 * @author armenio.pinto
 */
@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class AppStarter implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppStarter.class);

	@Autowired
	private MessageListener listener;

	@Autowired
	private SensorimotorAgent agent;

	public static void main(final String[] args) {
		SpringApplication.run(AppStarter.class, args);
	}

	@Override
	public void run(final String... args) throws Exception {
		listener.listen();
		agent.keepAlive();
	}

	@PreDestroy
	public void mensagem() {
		LOGGER.info("\nE outra vez conquistaremos a Distância -\n"
				+ "Do mar ou outra, mas que seja nossa!");
	}

}
