/**
 * GoSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor.command

import spock.lang.*

import nl.jqno.equalsverifier.EqualsVerifier

/**
 * Tests {@link Go}.
 * 
 * @author armenio.pinto
 */
class GoSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(Go.class).withRedefinedSuperclass().verify()

		then:
		noExceptionThrown()
	}
}
