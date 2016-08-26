/**
 * DanceSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor.command

import spock.lang.*

import nl.jqno.equalsverifier.EqualsVerifier

/**
 * Tests {@link Dance}.
 * 
 * @author armenio.pinto
 */
class DanceSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(Dance.class).withRedefinedSuperclass().verify()

		then:
		noExceptionThrown()
	}
}
