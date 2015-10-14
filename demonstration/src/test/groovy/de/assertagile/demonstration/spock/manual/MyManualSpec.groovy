package de.assertagile.demonstration.spock.manual

import de.assertagile.spockframework.extensions.Manual
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Title


/**
 * Created by mkutz on 02.07.14.
 */
@Manual
@Title("My manual spec is manual")
@Issue("STY-4711")
class MyManualSpec extends Specification {

    @Issue("http://assertagile.de/issues/BUG-42")
    def "this is a manual tested feature description"() {
        given: "the user is logged in"
        when: "the user clicks the logout button"
        then: "the user is at the start page"
        and: "the login button is visible"
    }

    @Issue(["http://assertagile.de/issues/STY-4712", "http://assertagile.de/issues/BUG-666", "http://issues.com/4711"])
    def "this is a manual tested feature description with additional information"() {
        given: "the user is not logged in"
        when: "the user enters unknown credentials"
        then: "the user stayed at the start page"
        and: "the login button is visible"
    }
}