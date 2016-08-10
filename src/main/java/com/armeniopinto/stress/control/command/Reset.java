/**
 * Reset.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.command;

import com.armeniopinto.stress.control.Request;

/**
 * Requests a platform's component to reset.
 * 
 * @author armenio.pinto
 */
public class Reset extends Request {

	public Reset() {
		super(Reset.class.getSimpleName());
	}

}
