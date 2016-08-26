/**
 * VisionAgentSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.vision

import spock.lang.*

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.videoio.VideoCapture

/**
 * Tests {@link VisionAgent}.
 * 
 * @author armenio.pinto
 */
class VisionAgentSpec extends Specification {

	VisionAgent agent

	def setup() {
		// Groovy uses a different class loader in fork mode.
		Runtime.getRuntime().load0(groovy.lang.GroovyClassLoader.class,
				new File("target/natives/x86-64/opencv_java310.dll").getAbsolutePath())

		agent = new VisionAgent()
		agent.device = Mock(VideoCapture)
		agent.start()
	}


	def "Captured frame is the actually captured one"() {
		given: "very respectfully, a photo of Chuck Norris"
		Mat image = Imgcodecs.imread("src/test/resources/images/chuck.jpg")
		final MatOfByte imageBytes = new MatOfByte()
		Imgcodecs.imencode(".jpg", image, imageBytes)
		final byte[] expectedBytes = imageBytes.toArray()
		imageBytes.release()

		and: "a device that always captures the photo of Chuck Norris, again, very respectfully"
		agent.device.read(_ as Mat) >> { Mat frame ->
			image.copyTo(frame)
		}

		when: "we capture a frame"
		agent.refresh()

		then: "the captured frame must be the actually captured one"
		Arrays.equals(agent.capturedFrame, expectedBytes)
	}
}