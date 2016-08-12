/**
 * TchauSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.command

import spock.lang.*

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning

/**
 * Tests {@link Tchau}.
 * 
 * @author armenio.pinto
 */
class TchauSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(Tchau.class).withRedefinedSuperclass().verify()

		then:
		noExceptionThrown()
	}
}
