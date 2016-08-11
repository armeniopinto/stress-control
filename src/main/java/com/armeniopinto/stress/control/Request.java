/**
 * Request.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control;

import java.util.HashMap;
import java.util.UUID;

/**
 * A request sent to a platform's component.
 * 
 * @author armenio.pinto
 */
public abstract class Request extends TraceableMessage {

	public static final String MESSAGE_TYPE = "Request";

	private final String what;

	protected Request(final String what) {
		super(MESSAGE_TYPE, UUID.randomUUID().toString(), new HashMap<>());
		this.what = what;
	}

	/** Specifies what's being requested. */
	public String getWhat() {
		return what;
	}

	@Override
	public final int hashCode() {
		return 17 + super.hashCode() * 31 + hashCode(what);
	}

	@Override
	public final boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof Request) {
			final Request other = (Request) obj;
			result = other.canEqual(this) && super.equals(other)
					&& equalsOrNull(other.what, this.what);
		}
		return result;
	}

	@Override
	public final boolean canEqual(final Object obj) {
		return obj instanceof Request;
	}

}
