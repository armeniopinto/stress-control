/**
 * VisionAgent.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.vision;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

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

	private Mat frame;

	private boolean running = false;

	@PostConstruct
	@Override
	public void start() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		LOGGER.debug("OpenCV native library loaded.");

		camera = new VideoCapture(0);
		camera.set(CAP_PROP_FRAME_WIDTH, 320);
		camera.set(CAP_PROP_FRAME_HEIGHT, 240);
		try {
			TimeUnit.MICROSECONDS.sleep(2000L);
		} catch (final InterruptedException ie) {
			LOGGER.warn("Failed to sleep!", ie);
		}
		if (!camera.isOpened()) {
			throw new RuntimeException("Unable to open the camera.");
		}
		frame = new Mat();
		LOGGER.debug(String.format("Camera service started at %dx%d.",
				(int) camera.get(CAP_PROP_FRAME_WIDTH), (int) camera.get(CAP_PROP_FRAME_HEIGHT)));

		running = true;
		LOGGER.info("Vision agent started.");
	}

	@Override
	public synchronized void stop() {
		frame.release();
		camera.release();
		LOGGER.debug("Camera service stopped.");
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
			camera.read(frame);
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
