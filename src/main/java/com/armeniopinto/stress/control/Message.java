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
		if (type == null) {
			throw new IllegalArgumentException("type=null");
		}
		if (data == null) {
			throw new IllegalArgumentException("data=null");
		}
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
		int result = 17;
		result = 31 * result + hashCode(type);
		result = 31 * result + hashCode(data);
		return result;
	}

	protected static int hashCode(final Object obj) {
		return obj != null ? obj.hashCode() : 0;
	}

	@Override
	public boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof Message) {
			final Message other = (Message) obj;
			result = other.canEqual(this) && equalsOrNull(other.type, this.type)
					&& equalsOrNull(other.data, this.data);
		}
		return result;
	}

	public boolean canEqual(final Object obj) {
		return obj instanceof Message;
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

	protected static boolean equalsOrNull(final Object obj1, final Object obj2) {
		return (obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2));
	}

}
