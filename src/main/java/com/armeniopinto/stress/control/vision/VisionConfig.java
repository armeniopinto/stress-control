/**
 * VisionConfig.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.vision;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

/**
 * @author armenio.pinto
 */
@Configuration
public class VisionConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisionConfig.class);

	@Value("${stress.vision.device_id:0}")
	private int deviceId;

	@Value("${stress.vision.frame.width:320}")
	private int width;

	@Value("${stress.vision.frame.height:240}")
	private int height;

	@Bean(name = "visionDevice", destroyMethod = "release")
	public VideoCapture videoCapture() throws VisionException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		LOGGER.debug("OpenCV native library loaded.");

		final VideoCapture device = new VideoCapture(deviceId) {
			@Override
			public void release() {
				super.release();
				LOGGER.debug("Vision device stopped.");
			}
		};
		device.set(CAP_PROP_FRAME_WIDTH, width);
		device.set(CAP_PROP_FRAME_HEIGHT, height);
		try {
			TimeUnit.MICROSECONDS.sleep(2000L);
		} catch (final InterruptedException ie) {
			LOGGER.warn("Failed to sleep!", ie);
		}
		if (!device.isOpened()) {
			throw new VisionException("Unable to open the camera.");
		}

		final int actualWidth = (int) device.get(CAP_PROP_FRAME_WIDTH);
		final int actualHeight = (int) device.get(CAP_PROP_FRAME_HEIGHT);
		if (actualWidth != width) {
			LOGGER.warn(String.format("Requested frame width %d but got %d instead.", width,
					actualWidth));
		}
		if (actualHeight != height) {
			LOGGER.warn(String.format("Requested frame height %d but got %d instead.", height,
					actualHeight));
		}

		LOGGER.debug(String.format("Vision device %d started at %dx%d.", deviceId, actualWidth,
				actualHeight));

		return device;
	}

}