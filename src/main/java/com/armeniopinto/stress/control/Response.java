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

}
