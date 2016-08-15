/**
 * MessageListener.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import com.armeniopinto.stress.control.Event;
import com.armeniopinto.stress.control.EventHandler;
import com.armeniopinto.stress.control.Message;
import com.armeniopinto.stress.control.Response;
import com.armeniopinto.stress.control.command.TchauAck;

/**
 * Listens from messages sent by the sensorimotor component and notifies the associated components.
 * 
 * @author armenio.pinto
 */
@Component
public class MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

	@Autowired
	@Qualifier("stressExecutor")
	private AsyncTaskExecutor executor;

	@Autowired
	@Qualifier("sensorimotorReader")
	private BufferedReader reader;

	@Autowired
	private EventHandler events;

	@Autowired
	private SensorimotorAgent agent;

	private boolean running = false;

	@PostConstruct
	public void start() {
		running = true;

		executor.execute(() -> {
			while (running) {
				try {
					listen();
				} catch (final SensorimotorException se) {
					LOGGER.warn("Error listening for sensorimotor messages.", se);
				}
			}
			LOGGER.info("Sensorimotor message listener stopped.");
		});

		LOGGER.info("Sensorimotor message listener started.");
	}

	private void listen() throws SensorimotorException {
		final String message;
		try {
			message = reader.readLine();
		} catch (final IOException ioe) {
			throw new SensorimotorException("Error reading sensorimotor stream.", ioe);
		}
		LOGGER.trace("<-- " + message);

		final Message received = buildMessage(message);
		if (received instanceof TchauAck) {
			LOGGER.debug("Sensorimotor acknowledged shutdown.");
			running = false;
		} else if (received instanceof Response) {
			agent.handleResponse((Response) received);
		} else {
			events.handle((Event) received);
		}
	}

	@SuppressWarnings("unchecked")
	private static Message buildMessage(final String message) throws SensorimotorException {
		final Map<String, Object> raw;
		try {
			raw = Message.fromString(message, Map.class);
		} catch (IOException e) {
			throw new SensorimotorException(
					String.format("Failed to parse the message: %s", message));
		}

		switch ((String) raw.get("type")) {
		case Event.MESSAGE_TYPE:
			return new Event((Map<String, Object>) raw.get("data"));
		case Response.MESSAGE_TYPE:
			return new Response((String) raw.get("id"), (Map<String, Object>) raw.get("data"));
		case TchauAck.MESSAGE_TYPE:
			return new TchauAck((String) raw.get("id"), (Map<String, Object>) raw.get("data"));
		default:
			throw new SensorimotorException(
					String.format("Unknown message type '%s'.", raw.get("type")));
		}
	}

}
