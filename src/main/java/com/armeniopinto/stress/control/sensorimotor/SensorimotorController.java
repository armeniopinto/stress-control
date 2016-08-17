/**
 * SensorimotorController.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */

package com.armeniopinto.stress.control.sensorimotor;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.armeniopinto.stress.control.Response;
import com.armeniopinto.stress.control.command.Reset;
import com.armeniopinto.stress.control.sensorimotor.command.Dance;
import com.armeniopinto.stress.control.sensorimotor.command.GetOrientation;
import com.armeniopinto.stress.control.sensorimotor.command.Go;
import com.armeniopinto.stress.control.sensorimotor.command.Stop;

@RestController
@RequestMapping("/sensorimotor")
public class SensorimotorController implements HealthIndicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SensorimotorController.class);

	@Autowired
	private CommandSender sender;

	@Autowired
	private SensorimotorAgent agent;

	@Override
	public Health health() {
		return agent.isAlive() ? Health.up().build() : Health.down().build();
	}

	@GetMapping("/orientation")
	@SuppressWarnings("unchecked")
	public Orientation getOrientation() throws SensorimotorException {
		final Response response;
		try {
			response = agent.sendCommand(new GetOrientation());
		} catch (final Exception ioe) {
			throw new SensorimotorException("Failed to retrieve sensorimotor orientation.", ioe);
		}
		return new Orientation((Map<String, Object>) response.getData().get("orientation"));
	}

	@PostMapping("/go")
	public void robotGo() throws SensorimotorException {
		try {
			agent.sendCommand(new Go());
		} catch (final Exception ioe) {
			throw new SensorimotorException("Failed to make the robot go.", ioe);
		}
	}

	@PostMapping("/stop")
	public void robotStop() throws SensorimotorException {
		try {
			agent.sendCommand(new Stop());
		} catch (final Exception ioe) {
			throw new SensorimotorException("Failed to make the robot stop.", ioe);
		}
	}

	@PostMapping("/dance")
	public void robotDance() throws SensorimotorException {
		try {
			agent.sendCommand(new Dance());
		} catch (final Exception ioe) {
			throw new SensorimotorException("Failed to make the robot dance!", ioe);
		}
	}

	@PostMapping("/reset")
	public synchronized void reset() throws SensorimotorException {
		try {
			LOGGER.info("Resetting the sensorimotor component...");
			sender.send(new Reset());
		} catch (final IOException ioe) {
			throw new SensorimotorException("Failed to reset the sensorimotor component.", ioe);
		}
	}

}
