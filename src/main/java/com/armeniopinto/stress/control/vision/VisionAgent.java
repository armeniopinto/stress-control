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
 * Sees.
 * 
 * @author armenio.pinto
 */
@Component
public class VisionAgent implements Lifecycle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisionAgent.class);

	@Autowired
	@Qualifier("visionDevice")
	private VideoCapture device;

	private final Mat capturedFrame = new Mat(), processedFrame = new Mat();

	private final FrameRate fps = new FrameRate();

	private boolean running = false;

	@PostConstruct
	@Override
	public void start() {
		running = true;
		LOGGER.info("Vision agent started.");
	}

	@Override
	public synchronized void stop() {
		processedFrame.release();
		capturedFrame.release();
		running = false;
		LOGGER.info("Vision agent stopped.");
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	public byte[] getCapturedFrame() {
		final MatOfByte frameBytes = new MatOfByte();
		synchronized (capturedFrame) {
			Imgcodecs.imencode(".jpg", capturedFrame, frameBytes);
		}
		final byte[] bytes = frameBytes.toArray();
		frameBytes.release();

		return bytes;
	}

	@Scheduled(fixedDelayString = "${stress.vision.refresh_period:33}")
	public void refresh() {
		if (running) {
			synchronized (capturedFrame) {
				device.read(capturedFrame);
				capturedFrame.copyTo(processedFrame);
			}
			fps.refresh();
		}
	}

}
