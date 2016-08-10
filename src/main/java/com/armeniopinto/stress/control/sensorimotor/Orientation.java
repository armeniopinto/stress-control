/**
 * Orientation.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor;

import java.util.Map;

/**
 * The robot's orientation.
 *
 * @see https://en.wikipedia.org/wiki/Flight_dynamics_(fixed-wing_aircraft)
 * @author armenio.pinto
 */
public class Orientation {

	private final int yaw;

	private final int pitch;

	private final int roll;

	public Orientation(final Map<String, Object> data) {
		this(((Double) data.get("yaw")).intValue(), ((Double) data.get("pitch")).intValue(),
				((Double) data.get("roll")).intValue());
	}

	public Orientation(final int yaw, final int pitch, final int roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}

	public int getYaw() {
		return yaw;
	}

	public int getPitch() {
		return pitch;
	}

	public int getRoll() {
		return roll;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + yaw;
		hash = hash * 31 + pitch;
		return hash * 31 + roll;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj != null && obj instanceof Orientation) {
			final Orientation other = (Orientation) obj;
			return other.yaw == this.yaw && other.pitch == this.pitch && other.roll == this.roll;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("yaw=%d, pitch=%d, roll=%d", yaw, pitch, roll);
	}

}
