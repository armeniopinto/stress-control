/**
 * TchauAckSpec.java
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
 * Tests {@link TchauAck}.
 * 
 * @author armenio.pinto
 */
class TchauAckSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(TchauAck.class).withRedefinedSuperclass().verify()

		then:
		noExceptionThrown()
	}
}
