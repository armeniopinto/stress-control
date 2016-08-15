/**
 * Stop.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor.command;

import com.armeniopinto.stress.control.Request;

/**
 * @author armenio.pinto
 */
public class Stop extends Request {

	public Stop() {
		super(Stop.class.getSimpleName());
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof Stop) {
			result = ((Stop) obj).canEqual(this) && super.equals(obj);
		}

		return result;
	}

	@Override
	protected final boolean canEqual(final Object obj) {
		return obj instanceof Stop;
	}

}
