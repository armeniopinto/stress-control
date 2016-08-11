/**
 * Response.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control;

import java.util.Map;

/**
 * A response from a platform's component associated with a previous request.
 * 
 * @author armenio.pinto
 */
public class Response extends TraceableMessage {

	public static final String MESSAGE_TYPE = "Response";

	public Response(final String id, final Map<String, Object> data) {
		super(MESSAGE_TYPE, id, data);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof Response) {
			result = ((Response) obj).canEqual(this) && super.equals(obj);
		}

		return result;
	}

	@Override
	protected boolean canEqual(final Object obj) {
		return obj instanceof Response;
	}

}
