/**
 * CommandSender.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.armeniopinto.stress.control.Request;
import com.armeniopinto.stress.control.RequestSender;

/**
 * Sends commands to the sensorimotor component.
 * 
 * @author armenio.pinto
 */
@Component
public class CommandSender implements RequestSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandSender.class);

	@Value("${stress.sensorimotor.rx_buffer_size}")
	private int bufferSize;

	@Autowired
	@Qualifier("sensorimotorOutputStream")
	private OutputStream out;

	@Override
	public synchronized void send(final Request request) throws IOException {
		// By default the Arduino's serial read buffer is only 64 bytes long.
		LOGGER.trace("--> " + request.toString());
		final byte[] data = (request.toString() + "\n").getBytes();
		final byte[] buffer = new byte[bufferSize];
		int i = 0;
		for (final byte b : data) {
			buffer[(i++) % bufferSize] = b;
			if (i % bufferSize == 0 || i == data.length) {
				out.write(buffer, 0, (i - 1) % bufferSize + 1);
				out.flush();
				Arrays.fill(buffer, (byte) 0);
			}
		}
	}

}
