package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject

import java.lang.reflect.AnnotatedElement

/**
 * Created by mkutz on 02.06.14.
 */
class ManualExtensionSpec extends Specification {

    TestPlanBuilder testPlanBuilderMock = Mock()

    ConfigObject configMock = Mock()

    @Subject
    ManualExtension manualExtension = new ManualExtension(testPlanBuilders: [testPlanBuilderMock], config: configMock)

    SpecInfo specInfoMock = Mock() { getReflection() >> Mock(AnnotatedElement) }
    Manual annotationMock = Mock() { story() >> ""; knownBugs() >> [] }
    FeatureInfo featureInfoMock = Mock() { getParent() >> specInfoMock }

    def "annotated specs should be added to the test plan"() {
        when:
        manualExtension.visitSpecAnnotation(annotationMock, specInfoMock)

        then:
        1 * testPlanBuilderMock.appendSpec(specInfoMock, annotationMock.story(), annotationMock.knownBugs())
    }

    def "annotated features should be added to the test plan and set to the configured state"(
            boolean markManualTestsAsExcluded) {
        given:
        configMock.get("markManualTestsAsExcluded") >> markManualTestsAsExcluded

        when:
        manualExtension.visitFeatureAnnotation(annotationMock, featureInfoMock)

        then:
        1 * testPlanBuilderMock.appendFeature(featureInfoMock, annotationMock.story(), annotationMock.knownBugs())

        and:
        if (markManualTestsAsExcluded) 1 * featureInfoMock.setExcluded(true)
        else featureInfoMock.setSkipped(true)

        where:
        markManualTestsAsExcluded << [true, false]
    }

    def "annotated features should use their spec's annotation data"() {
        given:
        Manual specAnnotation = Mock() {
            story() >> "SPEC-1"
            knownBugs() >> ["BUG-1", "BUG-2"]
        }
        Manual featureAnnotation = Mock() {
            story() >> "FEAT-1"
            knownBugs() >> ["BUG-3"]
        }
        specInfoMock.getReflection().getAnnotation(Manual) >> specAnnotation

        when:
        manualExtension.visitFeatureAnnotation(featureAnnotation, featureInfoMock)

        then:
        1 * testPlanBuilderMock.appendFeature(featureInfoMock, featureAnnotation.story(),
                featureAnnotation.knownBugs() + specAnnotation.knownBugs())
    }
}
