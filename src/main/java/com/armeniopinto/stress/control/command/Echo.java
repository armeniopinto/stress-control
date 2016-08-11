/**
 * Echo.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.command;

import com.armeniopinto.stress.control.Request;

/**
 * When an Echo command is sent to a platform's component it must reply with another Echo.
 * 
 * @author armenio.pinto
 */
public class Echo extends Request {

	public Echo() {
		super(Echo.class.getSimpleName());
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof Echo) {
			result = ((Echo) obj).canEqual(this) && super.equals(obj);
		}

		return result;
	}

	@Override
	protected final boolean canEqual(final Object obj) {
		return obj instanceof Echo;
	}

}
