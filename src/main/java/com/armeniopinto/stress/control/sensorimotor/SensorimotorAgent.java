/**
 * SensorimotorAgent.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.armeniopinto.stress.control.MessageBroker;
import com.armeniopinto.stress.control.Response;
import com.armeniopinto.stress.control.command.Echo;
import com.armeniopinto.stress.control.command.Reset;
import com.armeniopinto.stress.control.command.Tchau;
import com.armeniopinto.stress.control.sensorimotor.command.GetOrientation;

/**
 * Maintains the status of the sensorimotor component and exposes a rest API to use it.
 * 
 * @author armenio.pinto
 */
@RestController("sensorimotor")
@RequestMapping("/sensorimotor")
public class SensorimotorAgent implements HealthIndicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SensorimotorAgent.class);

	@Autowired
	private CommandSender sender;

	@Autowired
	private MessageBroker broker;

	@Value("${stress.sensorimotor.keep_alive_period}")
	private long period;

	private boolean running = false;

	private boolean alive = false;

	@Async
	public void keepAlive() {
		running = true;
		alive = false;

		LOGGER.info("Sensorimotor agent started.");
		while (running) {
			try {
				broker.sendRequest(sender, new Echo()).get();
				alive = true;
				LOGGER.debug("Sensorimotor component alive.");
				TimeUnit.MILLISECONDS.sleep(period);
			} catch (final Throwable t) {
				alive = false;
				if (running) {
					LOGGER.warn("Sensorimotor component down.", t);
				}
			}
		}
		LOGGER.info("Sensorimotor agent stopped.");
	}

	@Override
	public Health health() {
		return alive ? Health.up().build() : Health.down().build();
	}

	@GetMapping("/orientation")
	@SuppressWarnings("unchecked")
	public ResponseEntity<Orientation> getOrientation() {
		try {
			final Future<Response> response = broker.sendRequest(sender, new GetOrientation());
			return new ResponseEntity<Orientation>(
					new Orientation(
							(Map<String, Object>) response.get().getData().get("orientation")),
					HttpStatus.OK);
		} catch (final Exception e) {
			LOGGER.warn("Failed to retrieve sensorimotor orientation.", e);
			return new ResponseEntity<Orientation>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/reset")
	public ResponseEntity<HttpStatus> reset() {
		try {
			LOGGER.info("Resetting the sensorimotor component...");
			sender.send(new Reset());
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		} catch (final IOException ioe) {
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreDestroy
	public void stop() throws IOException {
		LOGGER.debug("Preparing to stop sensorimotor agent...");
		running = false;

		// Shutdown handshake:
		sender.send(new Tchau());
	}

}
