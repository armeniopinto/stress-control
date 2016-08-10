/**
 * RequestSender.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.armeniopinto.stress.control.Request;

/**
 * @author armenio.pinto
 */
@Component
public interface RequestSender {

	public void send(final Request request) throws IOException;

}