/**
 * SensorimotorConfig.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

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
		final SerialPort port;
		try {
			final CommPortIdentifier id = findSerialPort();
			LOGGER.debug(String.format("Using serial port '%s'.", id.getName()));

			port = openSerialPort(id);
			LOGGER.info("Connected to the sensorimotor component.");
		} catch (final IOException ioe) {
			throw new IOException("Error connecting to the sensorimotor component.", ioe);
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

	@Bean(name = "sensorimotorInputStream")
	public InputStream serialPortInputStream(@Autowired final SerialPort port) throws IOException {
		return port.getInputStream();
	}

	@Bean(name = "sensorimotorOutputStream")
	public OutputStream serialPortOutputStream(@Autowired final SerialPort port)
			throws IOException {
		return port.getOutputStream();
	}

}