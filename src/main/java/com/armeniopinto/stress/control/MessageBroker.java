/**
 * MessageHandler.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Implements the request-response pattern.
 * 
 * @author armenio.pinto
 */
@Component
public class MessageBroker {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroker.class);

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Value("${stress.request.timeout:5000}")
	private long timeout;

	private final Map<String, ReceivedResponse> responses;

	@Autowired
	private EventHandler events;

	public MessageBroker() {
		responses = new HashMap<>();
	}

	public void handle(final Message message) {
		if (message instanceof Response) {
			handleResponse((Response) message);
		} else if (message instanceof Event) {
			events.handle((Event) message);
		}
	}

	private void handleResponse(final Response response) {
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

	public Future<Response> sendRequest(final RequestSender sender, final Request request)
			throws IOException {
		return executor.submit(() -> {
			final long start = System.currentTimeMillis();
			sender.send(request);

			final String id = request.getId();
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
	}

	private class ReceivedResponse {

		public final Response response;

		public final long timestamp;

		public ReceivedResponse(final Response response) {
			this.response = response;
			timestamp = System.currentTimeMillis();
		}
	}

}
