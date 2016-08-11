/**
 * EchoSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.command

import spock.lang.*

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning

import com.armeniopinto.stress.control.command.TchauAck

/**
 * Tests {@link Echo}.
 * 
 * @author armenio.pinto
 */
class EchoSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(Echo.class).withRedefinedSuperclass().verify()

		then:
		noExceptionThrown()
	}
}
