/**
 * MessageListenerSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor

import spock.lang.*

import com.armeniopinto.stress.control.Event
import com.armeniopinto.stress.control.EventHandler
import com.armeniopinto.stress.control.Response
import com.armeniopinto.stress.control.command.TchauAck

/**
 * Tests {@link MessageListener}.
 * 
 * @author armenio.pinto
 */
class MessageListenerSpec extends Specification {

	def TCHAU_ACK_MESSAGE = '{"type":"TchauAck","id":"1234567","data":{}}'

	MessageListener listener

	def setup() {
		listener = new MessageListener()
		listener.reader = Stub(BufferedReader)
		listener.agent = Mock(SensorimotorAgent)
		listener.events = Mock(EventHandler)
	}


	def "Received responses are handled by the agent"() {
		given: "the reception of a response"
		def json = '{"type":"Response","id":"1234567","data":{"some":"Data"}}'
		listener.reader.readLine() >>> [json, TCHAU_ACK_MESSAGE]

		when: "we listen for messages"
		listener.listen()

		then: "the agent must be asked to handle the expected deserialised response"
		1 * listener.agent.handleResponse(_ as Response)
	}


	def "Received events are handled by the event handler"() {
		given: "the reception of an event"
		def json = '{"type":"Event","data":{"some":"Data"}}'
		listener.reader.readLine() >>> [json, TCHAU_ACK_MESSAGE]

		when: "we listen for messages"
		listener.listen()

		then: "the event handler must be asked to handle the expected deserialised event"
		1 * listener.events.handle(_ as Event)
	}


	def "Receiving a shutdown acknowledgement stops the listener"() {
		given: "the reception of a mock shutdown aknowledgement"
		listener.reader.readLine() >> TCHAU_ACK_MESSAGE

		when: "we listen for messages"
		listener.listen()

		then: "the message listener shouldn't be listening"
		!listener.running
	}


	def "Building an unknown message type from JSON"() {
		given: "the JSON representation of an unknown message type"
		def json = '{"type":"Unkwnown"}'

		when: "we try to deserialise a message from it"
		listener.buildMessage(json)

		then: "an IOException must be thrown"
		thrown(SensorimotorException)
	}


	def "Building an event from JSON"() {
		given: "the JSON representation of an event"
		def json = '{"type":"Event","data":{"some":"Data"}}'

		when: "we try to deserialise an event from it"
		def event = listener.buildMessage(json)

		then: "the built message must be an event"
		event instanceof Event

		and: "the event attributes must match"
		event.type == "Event"
		event.data.some == "Data"
	}


	def "Building a response from JSON"() {
		given: "the JSON representation of a response"
		def json = '{"type":"Response","id":"1234567","data":{"some":"Data"}}'

		when: "we try to deserialise a response from it"
		def response = listener.buildMessage(json)

		then: "the built message must be a response"
		response instanceof Response

		and: "the event attributes must match"
		response.type == "Response"
		response.id == "1234567"
		response.data.some == "Data"
	}


	def "Building a shutdown acknowledgement from JSON"() {
		given: "the JSON representation of a shutdown acknowledgement"
		def json = '{"type":"TchauAck","id":"1234567","data":{}}'

		when: "we try to deserialise a response from it"
		def ack = listener.buildMessage(json)

		then: "the built message must be a shutdown acknowledgement"
		ack instanceof TchauAck

		and: "the shutdown acknowledgement attributes must match"
		ack.type == "TchauAck"
		ack.id == "1234567"
	}
}