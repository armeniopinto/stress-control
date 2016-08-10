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
	public boolean equals(final Object obj) {
		if (obj != null && obj instanceof Request) {
			final Request other = (Request) obj;
			return super.equals(other) && other.what.equals(this.what);
		}
		return false;
	}

}
