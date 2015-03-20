Spock Manual Extension
======================

[![Build Status](https://travis-ci.org/mkutz/spock-manual-extension.svg?branch=master)](https://travis-ci.org/mkutz/spock-manual-extension) [![Coverage Status](https://img.shields.io/coveralls/mkutz/spock-manual-extension.svg)](https://coveralls.io/r/mkutz/spock-manual-extension)

Extension to define manual test case specification using the [Spock framwork](http://spockframework.org/).

When executing a Specification marked ``@Manual`` the test will be set "ignored" and the block comments will be written to a test plan file.

Example
-------

Given this Specification
```groovy
package de.assertagile.spockframework.extensions.demonstration

import de.assertagile.spockframework.extensions.Manual
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Title

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
```

and the following ``SpockManualConfig.groovy`` file:

```groovy
import de.assertagile.spockframework.extensions.MarkDownTestPlanBuilder

Locale locale = Locale.ENGLISH
String issueTrackerBaseUrl = "http://assertagile.de/issues"
boolean markManualTestsAsExcluded = true

testPlanBuilders = [
    new MarkDownTestPlanBuilder("target/test_plan.md", issueTrackerBaseUrl, locale),
]
```
When executing the Specification in your IDE or via ``mvn test`` you will find the following test plan at
``target/test_plan.md``:
```markdown
#Manual Test Plan

##My manual spec is manual
[STY-4711](http://assertagile.de/issues/STY-4711)

###this is a manual tested feature description
[BUG-42](http://assertagile.de/issues/BUG-42)

- *Given* the user is logged in
- *When* the user clicks the logout button
- *Then* the user is at the start page
- *And* the login button is visible


###this is a manual tested feature description with additional information
[STY-4712](http://assertagile.de/issues/STY-4712), [BUG-666](http://assertagile.de/issues/BUG-666), [http://issues.com/4711](http://issues.com/4711)

- *Given* the user is not logged in
- *When* the user enters unknown credentials
- *Then* the user stayed at the start page
- *And* the login button is visible
```


Usage
-----

To use this extension you need to create a Groovy config script in you projects test resources path named
``SpockManualConfig.groovy``.
In default Maven projects the file should be located in ``src/test/resources``.
In order to generate a test plan, you need to configure at least one ``TestPlanBuilder``.

Extension
---------

Currently there is only one ``MarkDownTestPlanBuilder``, but you can always create more. Just extend ``TestPlanBuilder``
and add it to your ``SpockManualConfig.groovy``
