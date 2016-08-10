/**
 * TchauAck.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.command;

import java.util.Map;

import com.armeniopinto.stress.control.Response;

/**
 * The acknowledgement of a shutdown request.
 * 
 * @author armenio.pinto
 */
public class TchauAck extends Response {

	public static final String MESSAGE_TYPE = "TchauAck";

	public TchauAck(final String id, final Map<String, Object> data) {
		super(id, data);
	}

}
