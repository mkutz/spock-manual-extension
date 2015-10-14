package de.assertagile.demonstration.spock.manual

import de.assertagile.spockframework.extensions.Manual
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Title


/**
 * Created by mkutz on 02.07.14.
 */
@Manual
@Title("Another manual")
@Issue("STY-4713")
class AnotherManualSpec extends Specification {

    @Issue("BUG-42")
    def "this is a manual tested feature description"() {
        expect: "this works"
    }
}