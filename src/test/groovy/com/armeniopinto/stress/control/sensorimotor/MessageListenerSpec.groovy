/**
 * MessageListenerSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor

import spock.lang.*

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import com.armeniopinto.stress.control.Event
import com.armeniopinto.stress.control.MessageBroker
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
		listener.broker = Mock(MessageBroker)
	}


	def "Receiving messages to be handled by the message broker"(json, expectedClass) {
		given: "the reception of a message"
		listener.reader.readLine() >>> [json, TCHAU_ACK_MESSAGE]
		def capturedClass

		when: "we listen for messages"
		listener.listen()

		then: "the broker must be asked to handle the expected deserialised message"
		1 * listener.broker.handle(_) >> { args ->
			capturedClass = args[0].class
		}

		and: "the deserialised message must be of the expected type"
		capturedClass == expectedClass

		where:
		json || expectedClass
		'{"type":"Event","data":{"some":"Data"}}' | Event.class
		'{"type":"Response","id":"1234567","data":{"some":"Data"}}' | Response.class
	}


	def "Receiving a shutdown acknowledgement stops the listener"() {
		given: "the reception of a mock shutdown aknowledgement"
		listener.reader.readLine() >> TCHAU_ACK_MESSAGE

		when: "we listen for messages"
		listener.listen()

		then: "the message listener shouldn't be listening"
		!listener.listening
	}


	def "Building an unknown message type from JSON"() {
		given: "the JSON representation of an unknown message type"
		def json = '{"type":"Unkwnown"}'

		when: "we try to deserialise a message from it"
		listener.buildMessage(json)

		then: "an IOException must be thrown"
		thrown(IOException)
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