/**
 * VisionController.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */

package com.armeniopinto.stress.control.vision;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vision")
public class VisionController implements HealthIndicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisionController.class);

	@Autowired
	private VisionAgent agent;

	@Override
	public Health health() {
		return agent.isRunning() ? Health.up().build() : Health.down().build();
	}

	@RequestMapping(value = "/frame", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getFrame() throws IOException {
		return agent.scanFrame();
	}

}
