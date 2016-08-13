/**
 * ResponseSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control

import spock.lang.*

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning

import com.armeniopinto.stress.control.command.TchauAck

/**
 * Tests {@link Response}.
 * 
 * @author armenio.pinto
 */
class ResponseSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(Response.class)
				.withRedefinedSuperclass()
				.withRedefinedSubclass(TchauAck.class)
				.verify()

		then:
		noExceptionThrown()
	}
}
