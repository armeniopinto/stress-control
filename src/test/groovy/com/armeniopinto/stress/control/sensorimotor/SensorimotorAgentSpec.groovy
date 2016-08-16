/**
 * SensorimotorAgentSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor

import spock.lang.*

import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

import org.springframework.core.task.SimpleAsyncTaskExecutor

import com.armeniopinto.stress.control.Request
import com.armeniopinto.stress.control.Response
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


	def "Sending a successful command returns a response"() {
		given: "the sending of a command with a successful response"
		def command = Mock(Request)
		def response = Mock(Response)
		agent.sender = Stub(CommandSender) {
			send(command) >> { agent.handleResponse(response) }
		}

		when: "we send the command"
		def returnedResponse = agent.sendCommand(command)

		then: "the returned response much match the received response"
		returnedResponse == response
	}


	def "Sending a command without a response throws a timeout exception"() {
		when: "we send a command for which a response isn't received"
		def command = Mock(Request)
		agent.sendCommand(command)

		then: "the command must be sent"
		agent.sender.send(command)

		and: "a timeout exception must be thrown"
		def exception = thrown(SensorimotorException)
		// TimeoutException > ExecutionException > SensorimotorException
		exception.cause instanceof ExecutionException
		exception.cause.cause instanceof TimeoutException
	}


	def "Keep-alive sends an Echo command"() {
		when: "the keep-alive method is invoked"
		agent.keepAlive()

		then: "an Echo command must be sent"
		1 * agent.sender.send(_ as Echo) >> {
			agent.handleResponse(Mock(Response))
		}
	}


	def "Successful keep-alive command flags sensorimotor as up"() {
		when: "the keep-alive method is invoked"
		agent.keepAlive()

		then: "an Echo command must be sent"
		1 * agent.sender.send(_ as Echo) >> {
			agent.handleResponse(Mock(Response))
		}

		and: "the sensorimotor component must be flagged as up"
		agent.alive
	}


	def "Failed keep-alive command flags sensorimotor as down"() {
		given: "a command sender invocation that throws an exception"
		agent.sender.send(_ as Echo) >> { throw new IOException() }

		when: "the keep-alive method is invoked"
		agent.keepAlive()

		then: "the sensorimotor component must be flagged as down"
		!agent.alive
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