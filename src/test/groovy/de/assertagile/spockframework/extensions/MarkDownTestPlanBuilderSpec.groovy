package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

class MarkDownTestPlanBuilderSpec extends TestPlanBuilderSpec {

    @Subject
    MarkDownTestPlanBuilder testPlanBuilder = new MarkDownTestPlanBuilder(
            testPlanFile.absolutePath, issueTrackerBaseUrl)

    def "appendHeader should just add a static top level head line"() {
        when:
        testPlanBuilder.appendHeader()

        then:
        testPlanFile.text == "#Manual Test Plan\n"
    }

    def "appendSpec should add a second level head line and the spec's issues"(
            String specName, String specTitle, List<String> issues, String content) {
        given:
        SpecInfo specInfoMock = Mock {
            getName() >> specName
            getAnnotation(Title) >> Mock(Title) {
                value() >> specTitle
            }
            getAnnotation(Issue) >> Mock(Issue) {
                value() >> issues
            }
        }

        when:
        testPlanBuilder.appendSpec(specInfoMock)

        then:
        testPlanFile.text == content

        where:
        specName       | specTitle | issues              || content
        "Name"         | "Title"   | []                  || "\n##Title\n"
        null           | "Title"   | ["issue"]           || "\n##Title\n[issue](${issueTrackerBaseUrl}/issue)\n"
        "MyManualSpec" | null      | ["first", "second"] || "\n##MyManualSpec\n[first](${issueTrackerBaseUrl}/first), [second](${issueTrackerBaseUrl}/second)\n"
    }

    def "appendFeature should add a third level headline, the features issues and the feature description"(
            String featureName, List<String> issues, String content) {
        given:
        FeatureInfo featureInfoMock = Mock {
            getName() >> featureName
            getBlocks() >> [Mock(BlockInfo) { getKind() >> BlockKind.EXPECT; getTexts() >> ["First", "Second"] }]
            getFeatureMethod() >> Mock(MethodInfo) {
                getAnnotation(Issue) >> Mock(Issue) {
                    value() >> issues
                }
            }
        }

        when:
        testPlanBuilder.appendFeature(featureInfoMock)

        then:
        testPlanFile.text == content

        where:
        featureName        | issues                         || content
        "Feature Name"     | []                             || "\n###Feature Name\n\n- *Expect* First\n- *And* Second\n\n"
        "Other Feature"    | ["first", "second"]            || "\n###Other Feature\n[first](${issueTrackerBaseUrl}/first), [second](${issueTrackerBaseUrl}/second)\n\n- *Expect* First\n- *And* Second\n\n"
        "One More Feature" | ["http://issues.com/BUG-4711"] || "\n###One More Feature\n[http://issues.com/BUG-4711](http://issues.com/BUG-4711)\n\n- *Expect* First\n- *And* Second\n\n"
    }
}
