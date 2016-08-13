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
		super(MESSAGE_TYPE, id, data);
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof TchauAck) {
			result = ((TchauAck) obj).canEqual(this) && super.equals(obj);
		}

		return result;
	}

	@Override
	protected final boolean canEqual(final Object obj) {
		return obj instanceof TchauAck;
	}

}
