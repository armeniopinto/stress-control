/**
 * Tchau.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.command;

import com.armeniopinto.stress.control.Request;

/**
 * A request to shutdown a platform's component.
 * 
 * @author armenio.pinto
 */
public class Tchau extends Request {

	public Tchau() {
		super(Tchau.class.getSimpleName());
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof Tchau) {
			result = ((Tchau) obj).canEqual(this) && super.equals(obj);
		}

		return result;
	}

	@Override
	protected final boolean canEqual(final Object obj) {
		return obj instanceof Tchau;
	}

}
