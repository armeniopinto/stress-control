/**
 * SensorimotorAgentSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor

import spock.lang.*

import com.armeniopinto.stress.control.MessageBroker
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
		agent.sender = Mock(CommandSender)
		agent.broker = Mock(MessageBroker)
		agent.period = 1000L
	}

	def "Stopping the agent sends a shutdown command"() {
		given: "a running agent"
		agent.running = true

		when: "we stop the agent"
		agent.stop()

		then: "a shutdown command must be sent"
		1 * agent.sender.send(_ as Tchau)
	}

	def "Stopping a stopped agent throws an IllegalStateException"() {
		given: "a stopped agent"
		agent.running = false

		when: "we try to stop it again"
		agent.stop()
		
		then: "an IllegalStateException must be thrown"
		thrown(IllegalStateException)
		
		and: "no commands should be sent"
		0 * agent.sender.send(_)
	}
}