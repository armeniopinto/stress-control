/**
 * SensorimotorAgent.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.Lifecycle;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.armeniopinto.stress.control.Request;
import com.armeniopinto.stress.control.Response;
import com.armeniopinto.stress.control.command.Echo;
import com.armeniopinto.stress.control.command.Tchau;

/**
 * Maintains the status of the sensorimotor component and exposes a rest API to use it.
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

	private final Map<String, ReceivedResponse> responses;

	@Value("${stress.sensorimotor.keep_alive_period:5000}")
	private long period;

	private boolean running, alive;

	public SensorimotorAgent() {
		responses = new HashMap<>();
	}

	@Override
	public void start() {
		running = alive = false;
	}

	@Override
	public void stop() {
		if (!running) {
			throw new IllegalStateException("Already stopped.");
		}

		LOGGER.debug("Preparing to stop sensorimotor agent...");
		running = false;

		// Shutdown handshake:
		try {
			sender.send(new Tchau());
		} catch (final IOException ioe) {
			LOGGER.warn("Failed to send the shutdown command.", ioe);
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	public synchronized Response sendCommand(final Request command) throws IOException {
		final Future<Response> response = executor.submit(() -> {
			final long start = System.currentTimeMillis();
			sender.send(command);

			final String id = command.getId();
			while (System.currentTimeMillis() - start < timeout) {
				synchronized (responses) {
					if (responses.containsKey(id)) {
						return responses.remove(id).response;
					} else {
						responses.wait(timeout - (System.currentTimeMillis() - start));
					}
				}
			}
			throw new TimeoutException(
					String.format("Response didn't arrive within %d milliseconds.", timeout));
		});

		try {
			return response.get();
		} catch (final InterruptedException | ExecutionException e) {
			throw new IOException(e); // XXX: refactor.
		}
	}

	void handleResponse(final Response response) {
		synchronized (responses) {
			responses.put(response.getId(), new ReceivedResponse(response));
			responses.notifyAll();
		}
		executor.execute(() -> {
			cleanResponses();
		});
	}

	/** Goes through the received responses buffer and removes the ones left there for too long. */
	private void cleanResponses() {
		synchronized (responses) {
			responses.entrySet().removeIf((entry) -> {
				if (System.currentTimeMillis() - entry.getValue().timestamp > timeout * 1.5) {
					LOGGER.info(String.format("Response %s timed-out.", entry.getKey()));
					return true;
				}
				return false;
			});
		}
	}

	private class ReceivedResponse {

		public final Response response;

		public final long timestamp;

		public ReceivedResponse(final Response response) {
			this.response = response;
			timestamp = System.currentTimeMillis();
		}
	}

	public boolean isAlive() {
		return alive;
	}

	@Async
	public void keepAlive() {
		running = true;
		alive = false;

		LOGGER.info("Sensorimotor agent started.");
		while (running) {
			try {
				sendCommand(new Echo());
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

}
