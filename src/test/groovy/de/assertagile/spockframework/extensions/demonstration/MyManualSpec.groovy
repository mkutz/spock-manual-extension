package de.assertagile.spockframework.extensions.demonstration

import de.assertagile.spockframework.extensions.Manual
import spock.lang.Specification


/**
 * Created by mkutz on 02.07.14.
 */
@Manual("A manual specification")
class MyManualSpec extends Specification {

    def "this is a manual tested feature description"() {
        given: "the user is logged in"
        when: "the user clicks the logout button"
        then: "the user is at the start page"
        and: "the login button is visible"
    }

    @Manual(story = "STY-4711", knownBugs = ["BUG-666", "BUG-23"])
    def "this is a manual tested feature description with additional information"() {
        given: "the user is not logged in"
        when: "the user enters unknown credentials"
        then: "the user stayed at the start page"
        and: "the login button is visible"
    }
}