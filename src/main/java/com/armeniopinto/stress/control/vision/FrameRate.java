/**
 * FrameRate.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.vision;

import java.util.LinkedList;

/**
 * A frame rate calculation helper.
 * 
 * @author armenio.pinto
 */
public class FrameRate {

	private static final int DEFAULT_EXPECTED_FPS = 30;

	/** The sampling window, in seconds. */
	private static final int DEFAULT_SAMPLING_WINDOW = 10;

	private final int maxRefreshes;

	private final LinkedList<Long> refreshes;

	public FrameRate() {
		this(DEFAULT_EXPECTED_FPS, DEFAULT_SAMPLING_WINDOW);
	}

	/**
	 * Creates a frame rate calculator that is optimised for a given FPS value and sampling window. 
	 * @param expectedFPS the expected FPS.
	 * @param samplingWindow the sampling window, in seconds.
	 */
	public FrameRate(final int expectedFPS, final int samplingWindow) {
		maxRefreshes = expectedFPS * samplingWindow;
		if (maxRefreshes < 2) {
			throw new IllegalArgumentException(
					"FPS and sampling window combination produces a buffer size of less than 2.");
		}
		refreshes = new LinkedList<>();
	}

	public synchronized void refresh() {
		refreshes.push(System.currentTimeMillis());
		if (refreshes.size() > maxRefreshes) {
			refreshes.pop();
		}
	}

	public synchronized double getFPS() {
		final int size = refreshes.size();
		return size > 1 ? calculateFPS(refreshes.peekFirst(), refreshes.peekLast(), size) : 0;
	}

	private double calculateFPS(final long start, final long stop, final int nframes) {
		return nframes / ((stop - start) / 1000D);
	}

}
