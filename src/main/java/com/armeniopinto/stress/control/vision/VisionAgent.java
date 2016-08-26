/**
 * VisionAgent.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.vision;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.Lifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

/**
 * Implements communication with the sensorimotor component and maintains its status.
 * 
 * @author armenio.pinto
 */
@Component
public class VisionAgent implements Lifecycle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisionAgent.class);

	@Autowired
	@Qualifier("visionDevice")
	private VideoCapture device;

	private Mat frame;

	private boolean running = false;

	@PostConstruct
	@Override
	public void start() {
		frame = new Mat();
		running = true;
		LOGGER.info("Vision agent started.");
	}

	@Override
	public synchronized void stop() {
		frame.release();
		running = false;
		LOGGER.info("Vision agent stopped.");
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	private byte[] frameBytes;

	public byte[] scanFrame() {
		return frameBytes;
	}

	@Scheduled(fixedDelayString = "${stress.vision.refresh_period:33}")
	public synchronized void refresh() {
		if (running) {
			device.read(frame);
			final MatOfByte readBytes = new MatOfByte();
			Imgcodecs.imencode(".jpg", frame, readBytes);
			frameBytes = readBytes.toArray();
			readBytes.release();

			reportRefreshRate();
		}
	}

	private long time = 0, refreshes = 0;

	private void reportRefreshRate() {
		refreshes++;
		if (time == 0) {
			time = System.currentTimeMillis();
		} else {
			if (refreshes % 100 == 0) {
				LOGGER.debug(String.format("Refresh rate %d frames/s.",
						(refreshes * 1000) / (System.currentTimeMillis() - time)));
				time = refreshes = 0;
			}
		}
	}

}
