/**
 * TraceableMessageSpec.java
 * 
 * Copyright (C) 2016 by Arménio Pinto
 * Please read the file LICENSE for the license details.
 */
package com.armeniopinto.stress.control

import spock.lang.*

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning

/**
 * Tests {@link TraceableMessage}.
 * 
 * @author armenio.pinto
 */
class TraceableMessageSpec extends Specification {

	def "equals() and hashCode() methods"() {
		when:
		EqualsVerifier.forClass(TraceableMessage.class)
				.withRedefinedSuperclass()
				.withRedefinedSubclass(Request.class)
				.verify()

		then:
		noExceptionThrown()
	}
}
