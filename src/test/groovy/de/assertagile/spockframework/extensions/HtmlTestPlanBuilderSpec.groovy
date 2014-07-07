package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject

/**
 * Created by mkutz on 28.05.14.
 */
class HtmlTestPlanBuilderSpec extends TestPlanBuilderSpec {

    @Subject
    HtmlTestPlanBuilder htmlTestPlanBuilder = new HtmlTestPlanBuilder("some/file/path")

    def setup() {
        htmlTestPlanBuilder.writer = stringWriter
    }

    def "appending a spec should add its title as an h2"() {
        given:
        SpecInfo specInfoMock = Mock() { getName() >> "spec name" }

        when:
        htmlTestPlanBuilder.appendSpec(specInfoMock, "", [] as String[])

        then:
        stringWriter.toString() =~ /<h2>${specInfoMock.name}<\/h2>/
    }

    def "appending a feature should append its name as an h3 with steps as list"() {
        given:
        String story = "SRY-4711"
        String[] knownBugs = ["BUG-10", "BUG-666"]

        FeatureInfo featureInfoMock = Mock() {
            getName() >> "I'm a feature's name."
            getBlocks() >> [
                    Mock(BlockInfo) { getKind() >> BlockKind.SETUP; getTexts() >> ["prepare A", "prepare B"] },
                    Mock(BlockInfo) { getKind() >> BlockKind.WHEN; getTexts() >> ["do something"] },
                    Mock(BlockInfo) { getKind() >> BlockKind.THEN; getTexts() >> ["something happened with A", "B is red"] }
            ]
            getParent() >> Mock(SpecInfo) { getName() >> "SpecName" }
        }

        when:
        htmlTestPlanBuilder.appendFeature(featureInfoMock, story, knownBugs)

        then:
        String result = stringWriter.toString()
        result.contains("<span class='story'>SRY-4711: </span>")
        result.contains("<span class='featureName'>I'm a feature's name.</span>")
        featureInfoMock.blocks*.texts*.each { result.contains(it) }
    }
}
