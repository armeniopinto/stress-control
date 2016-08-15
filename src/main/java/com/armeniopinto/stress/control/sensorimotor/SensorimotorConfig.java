/**
 * SensorimotorConfig.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * @author armenio.pinto
 */
@Configuration
public class SensorimotorConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SensorimotorConfig.class);

	private static final int BAUDRATE = 38400;

	@Value("${stress.control.sensorimotor.port:#{null}}")
	private String configPort;

	@Bean(name = "sensorimotorSerialPort", destroyMethod = "close")
	public SerialPort serialPort() throws IOException {
		final CommPortIdentifier id = findSerialPort();
		LOGGER.debug(String.format("Using serial port '%s'.", id.getName()));

		final SerialPort port = openSerialPort(id);
		try {
			TimeUnit.MILLISECONDS.sleep(2000L);
		} catch (final InterruptedException ie) {
			LOGGER.warn("Failed to sleep!", ie);
		}
		LOGGER.info("Connected to the sensorimotor component.");

		// Drains any initialisation messages:
		final InputStream in = port.getInputStream();
		final int available = in.available();
		if (available > 0) {
			in.skip(available);
			LOGGER.debug(String.format("%d bytes discarded from sensorimotor stream.", available));
		}

		return port;
	}

	private CommPortIdentifier findSerialPort() throws IOException {
		// XXX: RXTX doesn't behave very well with the Raspberry Pi's serial port. 
		if (System.getProperty("os.name").equals("Linux")
				&& System.getProperty("os.arch").equals("arm")) {
			System.setProperty("gnu.io.rxtx.SerialPorts", configPort);
			LOGGER.info(String.format("Forcing serial port '%s'.", configPort));
		}

		@SuppressWarnings("unchecked")
		final Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier id = null;
		while (ports.hasMoreElements()) {
			final CommPortIdentifier candidate = ports.nextElement();
			if (candidate.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (!candidate.isCurrentlyOwned()) {
					id = candidate;
					break;
				} else {
					LOGGER.debug(String.format("Discarding '%s', currently owned.", candidate));
				}
			} else {
				LOGGER.debug(String.format("Discarding '%s', not a serial port.", candidate));
			}
		}
		if (id == null) {
			throw new IOException("Unable to find a suitable serial port.");
		}

		return id;
	}

	private static SerialPort openSerialPort(final CommPortIdentifier id) throws IOException {
		final SerialPort port;
		try {
			port = (SerialPort) id.open(SensorimotorConfig.class.getName(), 2000);
			port.setSerialPortParams(BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			port.disableReceiveTimeout();
			port.enableReceiveThreshold(1);

			return port;
		} catch (final PortInUseException | UnsupportedCommOperationException e) {
			throw new IOException(String.format("Failed to open port '%s'.", id.getName()), e);
		}
	}

	@Bean(name = "sensorimotorReader", destroyMethod = "close")
	public BufferedReader serialPortInputStream(@Autowired final SerialPort port)
			throws IOException {
		return new BufferedReader(new InputStreamReader(port.getInputStream()));
	}

	@Bean(name = "sensorimotorOutputStream")
	public OutputStream serialPortOutputStream(@Autowired final SerialPort port)
			throws IOException {
		return port.getOutputStream();
	}

}