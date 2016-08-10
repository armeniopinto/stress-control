/**
 * Message.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A message exchanged between the platform's components.
 * 
 * @author armenio.pinto
 */
public abstract class Message {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final String type;

	private final Map<String, Object> data;

	protected Message(final String type, final Map<String, Object> data) {
		this.type = type;
		this.data = data;
	}

	protected Message(final String type) {
		this(type, new HashMap<>());
	}

	public String getType() {
		return type;
	}

	public Map<String, Object> getData() {
		return Collections.unmodifiableMap(data);
	}

	@Override
	public int hashCode() {
		return 17 + type.hashCode() * 31 + data.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj != null && obj instanceof Message) {
			final Message other = (Message) obj;
			return other.type.equals(this.type) && other.data.equals(this.data);
		}
		return false;
	}

	@Override
	public String toString() {
		try {
			return MAPPER.writeValueAsString(this);
		} catch (final JsonProcessingException jpe) {
			throw new RuntimeException("Error marshalling the message to JSON.", jpe);
		}
	}

	public static <M> M fromString(final String json, final Class<M> messageClass)
			throws IOException {
		return MAPPER.readValue(json, messageClass);
	}

}
