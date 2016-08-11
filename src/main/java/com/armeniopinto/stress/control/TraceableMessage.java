/**
 * TraceableMessage.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control;

import java.util.Map;

/**
 * A message part of a conversation between platform's components.
 * 
 * @author armenio.pinto
 */
public abstract class TraceableMessage extends Message {

	private final String id;

	protected TraceableMessage(final String type, final String id, final Map<String, Object> data) {
		super(type, data);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + super.hashCode();
		result = 31 * result + hashCode(id);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof TraceableMessage) {
			final TraceableMessage other = (TraceableMessage) obj;
			result = other.canEqual(this) && super.equals(other) && equalsOrNull(other.id, this.id);
		}
		return result;
	}

	@Override
	protected boolean canEqual(final Object obj) {
		return obj instanceof TraceableMessage;
	}

}
