package com.armeniopinto.stress.control.sensorimotor

import spock.lang.*

import com.armeniopinto.stress.control.Request

/**
 * Tests {@link CommandSender}.
 * 
 * @author armenio.pinto
 */
class CommandSenderSpec extends Specification {

	CommandSender sender

	def setup() {
		sender = new CommandSender()
		sender.bufferSize = 64
		sender.out = Mock(OutputStream)
	}


	def "Sending a null command throws a NullPointerException"() {
		when: "we try to send a null command"
		sender.send(null)

		then:"a NullPointerException must be thrown"
		thrown(NullPointerException)
	}


	def "Sending a command that serialises to less than or 64 bytes"(String json, int length) {
		setup:
		def Request command = Mock() {
			2 * toString() >> json
		}

		byte[] buffer = new byte[64]
		System.arraycopy((json + "\n").getBytes(), 0, buffer, 0, json.length() + 1)

		when: "we send the command"
		sender.send(command)

		then: "the correct bytes should be written to the serial port"
		1 * sender.out.write(buffer, 0, length)
		1 * sender.out.flush()

		where:
		json																||	length
		'{}'																|	3
		'{"type":"Request"}'												|	19
		'{"type":"Request","id":"038f1cb4-5dc0-11e6-8b77-86f30ca893d3"}'	|	63
		'{"type":"Requesto","id":"038f1cb4-5dc0-11e6-8b77-86f30ca893d3"}'	|	64
	}


	def "Sending a command that serialises to more than 64 bytes"(String json, int length1, int length2) {
		setup:
		def Request command = Mock() {
			2 * toString() >> json
		}

		byte[] bytes = (json + "\n").getBytes()
		byte[] buffer1 = new byte[64]
		Arrays.fill(buffer1, (byte)0)
		System.arraycopy(bytes, 0, buffer1, 0, 64)

		byte[] buffer2 = new byte[64]
		Arrays.fill(buffer2, (byte)0)
		System.arraycopy(bytes, 64, buffer2, 0, bytes.size() - 64)

		when: "we send the command"
		sender.send(command)

		then: "the correct bytes should be written to the serial port"
		1 * sender.out.write(buffer1, 0, length1)
		1 * sender.out.flush()
		1 * sender.out.write(buffer2, 0, length2)
		1 * sender.out.flush()

		where:
		json																		||	length1	||	length2
		'{"type":"Requestio","id":"038f1cb4-5dc0-11e6-8b77-86f30ca893d3"}'			|	64		||	1
		'{"type":"Request","id":"038f1cb4-5dc0-11e6-8b77-86f30ca893d3","a":"1"}'	|	64		||	7
	}
}