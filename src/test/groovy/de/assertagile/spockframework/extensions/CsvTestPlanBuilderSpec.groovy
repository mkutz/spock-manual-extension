package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Subject

import java.lang.reflect.AnnotatedElement

class CsvTestPlanBuilderSpec extends TestPlanBuilderSpec {

    @Subject
    CsvTestPlanBuilder csvTestPlanBuilder = new CsvTestPlanBuilder("some/file/path")

    def setup() {
        csvTestPlanBuilder.writer = stringWriter
    }

    def "appending a spec should do nothing but add the spec's title to each following feature"() {
        when:
        csvTestPlanBuilder.appendSpec(Mock(SpecInfo), "", [] as String[])

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
        String story = "STY-4711"
        String[] knownBugs = []

        when:
        csvTestPlanBuilder.appendFeature(featureInfoMock, story, knownBugs)

        then:
        stringWriter.toString() == "\"${story}\";" +
                "\"${specInfoMock.name}\";" +
                "\"${featureInfoMock.name}\";" +
                "\"Given prepare A and prepare B\nWhen do something\nThen something happened with A and B is red.\";" +
                "\"${knownBugs.join("\n")}\";" +
                "\n"
        println stringWriter.toString()
    }
}
