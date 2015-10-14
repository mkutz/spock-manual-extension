package de.assertagile.demonstration.spock.manual

import de.assertagile.spockframework.extensions.Manual
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Title


/**
 * Created by mkutz on 02.07.14.
 */
@Title("Partially manual spec")
@Issue("STY-4714")
class PartiallyManualSpec extends Specification {

    @Manual
    def "this is a manual tested feature description"() {
        expect: "this to work"
    }


    def "this is not manual"() {
        expect:
        1 == 1
    }
}