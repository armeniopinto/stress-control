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

}
