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

/**
 * @author rewe.mkutz
 * @version $Id$
 */
class MarkDownTestPlanBuilderSpec extends Specification {

    File testPlanFile = File.createTempFile("testplan", "md")

    @Subject
    MarkDownTestPlanBuilder testPlanBuilder = new MarkDownTestPlanBuilder(testPlanFile.absolutePath)

    def "AppendHeader"() {
        when:
        testPlanBuilder.appendHeader()

        then:
        testPlanFile.text == "#Manual Test Plan\n"
    }

    def "AppendSpec"(String specName, String specTitle, List<String> issues, String content) {
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
        null           | "Title"   | ["issue"]           || "\n##Title\nissue\n"
        "MyManualSpec" | null      | ["first", "second"] || "\n##MyManualSpec\nfirst, second\n"
    }

    def "AppendFeature"(String featureName, List<String> issues, String content) {
        given:
        FeatureInfo featureInfoMock = Mock(FeatureInfo) {
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
        featureName     | issues              || content
        "Feature Name"  | []                  || "\n###Feature Name\n\n- *Expect* First\n- *And* Second\n\n"
        "Other Feature" | ["first", "second"] || "\n###Other Feature\nfirst, second\n\n- *Expect* First\n- *And* Second\n\n"
    }
}
