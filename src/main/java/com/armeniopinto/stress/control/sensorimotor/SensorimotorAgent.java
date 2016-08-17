/**
 * SensorimotorAgent.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.Lifecycle;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.mutable.MutableObject;

import com.armeniopinto.stress.control.Request;
import com.armeniopinto.stress.control.Response;
import com.armeniopinto.stress.control.command.Echo;
import com.armeniopinto.stress.control.command.Reset;
import com.armeniopinto.stress.control.command.Tchau;

/**
 * Implements communication with the sensorimotor component and maintains its status.
 * 
 * @author armenio.pinto
 */
@Component
public class SensorimotorAgent implements Lifecycle {

	private static final Logger LOGGER = LoggerFactory.getLogger(SensorimotorAgent.class);

	@Autowired
	@Qualifier("stressExecutor")
	private AsyncTaskExecutor executor;

	@Autowired
	private CommandSender sender;

	@Value("${stress.request.timeout:5000}")
	private long timeout;

	private final MutableObject<Response> responseHolder = new MutableObject<>();

	private boolean running = false, alive = false;

	@PostConstruct
	@Override
	public void start() {
		running = true;

		try {
			sender.send(new Reset());
			LOGGER.debug("Sensorimotor component reset.");
		} catch (final IOException ioe) {
			throw new RuntimeException("Failed to reset the sensorimotor component.", ioe);
		}

		LOGGER.info("Sensorimotor agent started.");
	}

	@Override
	public void stop() {
		running = false;

		// Shutdown handshake. This should gracefully stop the listener.
		try {
			sender.send(new Tchau());
		} catch (final IOException ioe) {
			LOGGER.warn("Failed to send the shutdown command.", ioe);
		}

		LOGGER.info("Sensorimotor agent stopped.");
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	public synchronized Response sendCommand(final Request command) throws SensorimotorException {
		try {
			return executor.submit(() -> {
				synchronized (responseHolder) {
					sender.send(command);
					responseHolder.wait(timeout);
					final Response response = responseHolder.getValue();
					if (response != null) {
						responseHolder.setValue(null);
						return response;
					} else {
						throw new TimeoutException();
					}
				}
			}).get();

		} catch (final InterruptedException | ExecutionException e) {
			throw new SensorimotorException(
					String.format("Failed to run sensorimotor command: %s", command.toString()), e);
		}
	}

	void handleResponse(final Response response) {
		synchronized (responseHolder) {
			responseHolder.setValue(response);
			responseHolder.notify();
		}
	}

	public boolean isAlive() {
		return alive;
	}

	@Scheduled(fixedDelayString = "${stress.sensorimotor.keep_alive_period:5000}")
	public void keepAlive() {
		try {
			sendCommand(new Echo());
			alive = true;
			LOGGER.debug("Sensorimotor component alive.");
		} catch (final SensorimotorException se) {
			alive = false;
			LOGGER.warn("Sensorimotor component down.", se);
		}
	}

}
