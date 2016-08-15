/**
 * SensorimotorAgentSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor

import spock.lang.*

import org.springframework.core.task.SimpleAsyncTaskExecutor

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import com.armeniopinto.stress.control.Request;
import com.armeniopinto.stress.control.command.Echo
import com.armeniopinto.stress.control.command.Reset
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
		agent.period = 1000L
	}


	def "Keep-alive echo commands are sent periodically"() {
		setup:
		def executor = Executors.newSingleThreadExecutor()

		when: "we let the keep-alive thread running for 3 cycles"
		executor.execute({ agent.keepAlive() })
		TimeUnit.MILLISECONDS.sleep(agent.period * 3)

		then: "3 keep-alive commands must be sent"
		3 * agent.sender.send(_ as Request)

		cleanup:
		agent.stop()
		executor.shutdownNow()
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