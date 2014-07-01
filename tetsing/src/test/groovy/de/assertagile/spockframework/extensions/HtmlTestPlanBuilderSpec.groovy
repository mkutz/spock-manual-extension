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
class HtmlTestPlanBuilderSpec extends Specification {

    Writer stringWriter = new StringWriter()

    @Subject
    HtmlTestPlanBuilder htmlTestPlanBuilder = new HtmlTestPlanBuilder("some/file/path")

    def setup() {
        htmlTestPlanBuilder.writer = stringWriter
    }

    def "appending a spec with annotation, should add the annotation's title as an h2"() {
        given:
        Manual annotationMock = Mock() { value() >> "annotation value" }
        SpecInfo specInfoMock = Mock() { getName() >> "spec name" }

        when:
        htmlTestPlanBuilder.appendSpec(annotationMock, specInfoMock)

        then:
        stringWriter.toString() =~ /<h2>${annotationMock.value()}<\/h2>/
    }

    def "appending a spec with not annotation value, should add the annotation's name as an h2"() {
        Manual annotationMock = Mock() { value() >> "" }
        SpecInfo specInfoMock = Mock() { getName() >> "spec name" }

        when:
        htmlTestPlanBuilder.appendSpec(annotationMock, specInfoMock)

        then:
        stringWriter.toString() =~ /<h2>${specInfoMock.name}<\/h2>/
    }

    def "appending a feature should append its name as an h3 with steps as list"() {
        given:
        Manual annotationMock = Mock() {
            story() >> "SRY-4711"
            knownBugs() >> ["BUG-10", "BUG-666"]
        }
        List<BlockInfo> blockMocks = [
                Mock(BlockInfo) { getKind() >> BlockKind.SETUP; getTexts() >> ["prepare A", "prepare B"] },
                Mock(BlockInfo) { getKind() >> BlockKind.WHEN; getTexts() >> ["do something"] },
                Mock(BlockInfo) { getKind() >> BlockKind.THEN; getTexts() >> ["something happend with A", "B is red"] }
        ]
        FeatureInfo featureInfoMock = Mock() {
            getName() >> "I'm a feature's name."
            getBlocks() >> blockMocks
            getParent() >> Mock(SpecInfo) { getName() >> "SpecName" }
        }

        when:
        htmlTestPlanBuilder.appendFeature(annotationMock, featureInfoMock)

        then:
        String result = stringWriter.toString()
        result.contains("<h3>${featureInfoMock.name}</h3>")
        blockMocks*.texts*.each { result.contains(it) }
        println result
    }
}
