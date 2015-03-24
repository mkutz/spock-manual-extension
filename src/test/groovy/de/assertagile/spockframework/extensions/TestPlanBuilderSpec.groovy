package de.assertagile.spockframework.extensions

import spock.lang.Specification
import spock.lang.Subject


/**
 * @author rewe.mkutz
 * @version $Id$
 */
class TestPlanBuilderSpec extends Specification {

    static File testPlanFile = File.createTempFile("testplan", "md")
    static String issueTrackerBaseUrl = "http://assertagile/issues"
}