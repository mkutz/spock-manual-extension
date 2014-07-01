package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject

import java.lang.reflect.AnnotatedElement

/**
 * Created by mkutz on 28.05.14.
 */
class CsvTestPlanBuilderSpec extends Specification {

    Writer stringWriter = new StringWriter()

    @Subject
    CsvTestPlanBuilder csvTestPlanBuilder = new CsvTestPlanBuilder("some/file/path")

    def setup() {
        csvTestPlanBuilder.writer = stringWriter
    }

    def "appending a spec should do nothing but add the spec's title to each following feature"() {
        when:
        csvTestPlanBuilder.appendSpec(Mock(Manual), Mock(SpecInfo))

        then:
        stringWriter.toString() == ""
    }

    def "appending a feature should append a new line"() {
        given:
        List<BlockInfo> blockMocks = [
                Mock(BlockInfo) { getKind() >> BlockKind.SETUP; getTexts() >> ["prepare A", "prepare B"] },
                Mock(BlockInfo) { getKind() >> BlockKind.WHEN; getTexts() >> ["do something"] },
                Mock(BlockInfo) { getKind() >> BlockKind.THEN; getTexts() >> ["something happened with A", "B is red"] }
        ]
        SpecInfo specInfoMock = Mock() {
            getName() >> "spec name"
            getReflection() >> Mock(AnnotatedElement) { getAnnotation(Manual) >> Mock(Manual) }
        }
        FeatureInfo featureInfoMock = Mock() {
            getName() >> "feature name"
            getBlocks() >> blockMocks
            getParent() >> specInfoMock
        }
        Manual annotationMock = Mock() {
            knownBugs() >> ["BUG-666", "BUG-1"]
            story() >> "STY-4711"
        }

        when:
        csvTestPlanBuilder.appendFeature(annotationMock, featureInfoMock)

        then:
        stringWriter.toString() == "\"${annotationMock.story()}\";" +
                "\"${specInfoMock.name}\";" +
                "\"${featureInfoMock.name}\";" +
                "\"Given prepare A and prepare B\nWhen do something\nThen something happened with A and B is red.\";" +
                "\"${annotationMock.knownBugs().join("\n")}\";" +
                "\n"
        println stringWriter.toString()
    }
}
