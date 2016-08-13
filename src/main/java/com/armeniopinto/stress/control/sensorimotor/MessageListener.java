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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.armeniopinto.stress.control.Event;
import com.armeniopinto.stress.control.Message;
import com.armeniopinto.stress.control.MessageBroker;
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
	@Qualifier("sensorimotorReader")
	private BufferedReader reader;

	@Autowired
	private MessageBroker broker;

	private boolean listening = false;

	public boolean isListening() {
		return listening;
	}

	@Async
	public void listen() throws IOException {
		LOGGER.info("Sensorimotor message listener started.");
		listening = true;
		String message;
		while ((message = reader.readLine()) != null) {
			System.out.println(message);
			LOGGER.trace("<-- " + message);
			try {
				final Message received = buildMessage(message);
				if (received instanceof TchauAck) {
					LOGGER.debug("Sensorimotor acknowledged shutdown.");
					listening = false;
					break;
				} else {
					broker.handle(received);
				}
			} catch (final Throwable t) {
				LOGGER.warn("Error parsing sensorimotor message.", t);
			}
		}
		LOGGER.info("Sensorimotor message listener stopped.");
	}

	@SuppressWarnings("unchecked")
	private static Message buildMessage(final String message) throws IOException {
		final Map<String, Object> raw = Message.fromString(message, Map.class);

		switch ((String) raw.get("type")) {
		case Event.MESSAGE_TYPE:
			return new Event((Map<String, Object>) raw.get("data"));
		case Response.MESSAGE_TYPE:
			return new Response((String) raw.get("id"), (Map<String, Object>) raw.get("data"));
		case TchauAck.MESSAGE_TYPE:
			return new TchauAck((String) raw.get("id"), (Map<String, Object>) raw.get("data"));
		default:
			throw new IOException(String.format("Unknown message type '%s'.", raw.get("type")));
		}
	}

}
