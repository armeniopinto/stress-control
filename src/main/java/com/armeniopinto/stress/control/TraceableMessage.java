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
		result = 37 * result + super.hashCode();
		result = 37 * result + id.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj != null && obj instanceof TraceableMessage) {
			final TraceableMessage other = (TraceableMessage) obj;
			return super.equals(other) && other.id.equals(this.id);
		}
		return false;
	}

}
