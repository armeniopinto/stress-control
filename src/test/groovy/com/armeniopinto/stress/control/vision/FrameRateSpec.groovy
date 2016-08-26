/**
 * FrameRateSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.vision

import spock.lang.*

/**
 * Tests {@link FrameRate}.
 * 
 * @author armenio.pinto
 */
class FrameRateSpec extends Specification {

	@Unroll("A combination of #expectedFPS FPS and a sampling window of #samplingWindow throws an exception.")
	def "Invalid combinations of FPS and sampling window throws exception"(
			int expectedFPS, int samplingWindow) {

		when: "we create a frame rate calculator with an invalid combination of FPS and sampling window"
		def FrameRate fps = new FrameRate(expectedFPS, samplingWindow)

		then: "an exception must be thrown"
		thrown(IllegalArgumentException)

		where:
		expectedFPS	||	samplingWindow
		0			|	0
		0			|	10
		30			|	0
		1			|	1
		0			|	1
		1			|	0
	}


	def "No refreshes produces 0 FPS"() {
		when: "we create a frame rate calculator without any refreshes"
		def fps = new FrameRate()

		then: "the FPS must be 0"
		fps.getFPS() == 0D
	}


	def "Single refresh produces 0 FPS"() {
		given: "a test frame rate calculator"
		def fps = new FrameRate()

		when: "we refresh it a single time"
		fps.refresh()

		then: "the FPS must be 0"
		fps.getFPS() == 0D
	}
}