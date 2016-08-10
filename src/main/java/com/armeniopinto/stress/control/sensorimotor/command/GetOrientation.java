/**
 * GetOrientation.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor.command;

import com.armeniopinto.stress.control.Request;

/**
 * @author armenio.pinto
 */
public class GetOrientation extends Request {

	public GetOrientation() {
		super(GetOrientation.class.getSimpleName());
	}

}
