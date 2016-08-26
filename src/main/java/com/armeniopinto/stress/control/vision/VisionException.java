/**
 * VisionException.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.vision;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author armenio.pinto
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class VisionException extends Exception {

	private static final long serialVersionUID = 1L;

	public VisionException(final String message) {
		super(message);
	}

	public VisionException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
