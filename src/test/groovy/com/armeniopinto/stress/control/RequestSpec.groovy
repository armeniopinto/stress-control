/**
 * RequestSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control

import spock.lang.*

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning

import com.armeniopinto.stress.control.command.Echo

/**
 * Tests {@link Request}.
 * 
 * @author armenio.pinto
 */
class RequestSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(Request.class)
				.withRedefinedSuperclass()
				.withRedefinedSubclass(Echo.class)
				.verify()

		then:
		noExceptionThrown()
	}
}
