/**
 * VisionAgent.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.vision;

import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import org.springframework.context.Lifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Implements communication with the sensorimotor component and maintains its status.
 * 
 * @author armenio.pinto
 */
@Component
public class VisionAgent implements Lifecycle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisionAgent.class);

	private VideoCapture camera;

	private boolean running = false;

	private Mat frame;

	private byte[] frameBytes;

	@PostConstruct
	@Override
	public void start() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		LOGGER.debug("OpenCV native library loaded.");

		camera = new VideoCapture(0);
		try {
			TimeUnit.MICROSECONDS.sleep(2000L);
		} catch (final InterruptedException ie) {
			LOGGER.warn("Failed to sleep!", ie);
		}
		if (!camera.isOpened()) {
			throw new RuntimeException("Unable to open the camera.");
		}
		LOGGER.debug(String.format("Camera service started with resolution %dx%d.",
				(int) camera.get(CAP_PROP_FRAME_WIDTH), (int) camera.get(CAP_PROP_FRAME_HEIGHT)));

		frame = new Mat();

		running = true;

		LOGGER.info("Vision agent started.");
	}

	@Override
	public synchronized void stop() {
		running = false;
		frame.release();
		camera.release();
		LOGGER.debug("Camera service stopped.");
		LOGGER.info("Vision agent stopped.");
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Scheduled(fixedDelay = 143)
	public synchronized void refresh() {
		if (running) {
			camera.read(frame);
			final MatOfByte readBytes = new MatOfByte();
			Imgcodecs.imencode(".jpg", frame, readBytes);
			frameBytes = readBytes.toArray();
			readBytes.release();
		}
	}

	public byte[] scanFrame() {
		return frameBytes;
	}

}
