package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
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
        testPlanBuilder.writer.flush()

        then:
        testPlanFile.text == "#Manual Test Plan\n"
    }

    def "AppendSpec"(String specName, String specTitle, String content) {
        given:
        SpecInfo specInfoMock = Mock {
            getName() >> specName
            getAnnotation(Title) >> Mock(Title) {
                value() >> specTitle
            }
        }

        when:
        testPlanBuilder.appendSpec(specInfoMock)
        testPlanBuilder.writer.flush()

        then:
        testPlanFile.text == content

        where:
        specName       | specTitle    || content
        "Name"         | "Title"      || "\n##Title\n"
        null           | "Title"      || "\n##Title\n"
        "MyManualSpec" | null         || "\n##MyManualSpec\n"
    }

    def "AppendFeature"() {
        given:
        String featureName = "Name"
        FeatureInfo featureInfoMock = Mock {
            getName() >> featureName
            getBlocks() >> [Mock(BlockInfo) { getKind() >> BlockKind.EXPECT; getTexts() >> ["First", "Second"] }]
        }

        when:
        testPlanBuilder.appendFeature(featureInfoMock)
        testPlanBuilder.writer.flush()

        then:
        testPlanFile.text == "\n###${featureName}\n\n- *Expect* First\n- *Expect* Second\n\n"
    }
}
