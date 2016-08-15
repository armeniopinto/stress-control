/**
 * Dance.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor.command;

import com.armeniopinto.stress.control.Request;

/**
 * @author armenio.pinto
 */
public class Dance extends Request {

	public Dance() {
		super(Dance.class.getSimpleName());
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof Dance) {
			result = ((Dance) obj).canEqual(this) && super.equals(obj);
		}

		return result;
	}

	@Override
	protected final boolean canEqual(final Object obj) {
		return obj instanceof Dance;
	}

}
