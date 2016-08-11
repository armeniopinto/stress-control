/**
 * GetOrientationSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control.sensorimotor.command

import spock.lang.*

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning

import com.armeniopinto.stress.control.command.TchauAck

/**
 * Tests {@link GetOrientation}.
 * 
 * @author armenio.pinto
 */
class GetOrientationSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(GetOrientation.class).withRedefinedSuperclass().verify()

		then:
		noExceptionThrown()
	}
}
