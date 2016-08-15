/**
 * SensorimotorAgentSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor

import spock.lang.*

import org.springframework.core.task.SimpleAsyncTaskExecutor

import com.armeniopinto.stress.control.command.Echo
import com.armeniopinto.stress.control.command.Tchau

/**
 * Tests {@link SensorimotorAgent}.
 * 
 * @author armenio.pinto
 */
class SensorimotorAgentSpec extends Specification {

	SensorimotorAgent agent

	def setup() {
		agent = new SensorimotorAgent()
		agent.executor = new SimpleAsyncTaskExecutor()
		agent.sender = Mock(CommandSender)
		agent.timeout = 1000L
	}


	def "Keep-alive sends Echo commands"() {
		when: "the keep-alive method is invoked"
		agent.keepAlive()

		then: "an Echo command must be sent"
		1 * agent.sender.send(_ as Echo)
	}


	def "Stopping the agent sends a shutdown command"() {
		given: "a running agent"
		agent.running = true

		when: "we stop the agent"
		agent.stop()

		then: "a shutdown command must be sent"
		1 * agent.sender.send(_ as Tchau)
	}
}