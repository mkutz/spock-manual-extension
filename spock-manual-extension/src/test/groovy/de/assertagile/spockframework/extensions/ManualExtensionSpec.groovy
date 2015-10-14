package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject

import java.beans.FeatureDescriptor
import java.lang.reflect.AnnotatedElement

/**
 * Created by mkutz on 02.06.14.
 */
class ManualExtensionSpec extends Specification {

    TestPlanBuilder testPlanBuilderMock = Mock()

    ConfigObject configMock = Mock()

    @Subject
    ManualExtension manualExtension = new ManualExtension(testPlanBuilders: [testPlanBuilderMock], config: configMock)

    def "all features of an annotated spec should be regarded manual and be added to the test plan"(boolean markManualTestsAsExcluded) {
        given:
        SpecInfo specInfoMock = Mock()

        and:
        configMock.get("markManualTestsAsExcluded") >> markManualTestsAsExcluded
        specInfoMock.features >> [Mock(FeatureInfo), Mock(FeatureInfo)]

        when:
        manualExtension.visitSpecAnnotation(Mock(Manual), specInfoMock)

        then:
        1 * testPlanBuilderMock.appendFeature(specInfoMock.features[0])
        1 * testPlanBuilderMock.appendFeature(specInfoMock.features[1])

        and:
        (markManualTestsAsExcluded ? 0 : 1) * specInfoMock.features[0].setSkipped(true)
        (markManualTestsAsExcluded ? 1 : 0) * specInfoMock.features[0].setExcluded(true)
        (markManualTestsAsExcluded ? 0 : 1) * specInfoMock.features[1].setSkipped(true)
        (markManualTestsAsExcluded ? 1 : 0) * specInfoMock.features[1].setExcluded(true)

        where:
        markManualTestsAsExcluded << [true, false]
    }

    def "an annotated feature method should be regarded manual and be added to the test plan"(boolean markManualTestsAsExcluded) {
        given:
        FeatureInfo featureInfoMock = Mock {
            getSpec() >> Mock(SpecInfo)
        }

        and:
        configMock.get("markManualTestsAsExcluded") >> markManualTestsAsExcluded

        when:
        manualExtension.visitFeatureAnnotation(Mock(Manual), featureInfoMock)

        then:
        1 * testPlanBuilderMock.appendFeature(featureInfoMock)

        and:
        (markManualTestsAsExcluded ? 0 : 1) * featureInfoMock.setSkipped(true)
        (markManualTestsAsExcluded ? 1 : 0) * featureInfoMock.setExcluded(true)

        where:
        markManualTestsAsExcluded << [true, false]
    }

    def "a feature should only be added to the test plan if it was not added before due to a spec annotation"() {
        given:
        SpecInfo specInfoMock = Mock()
        FeatureInfo featureInfoMock = Mock {
            getSpec() >> specInfoMock
        }
        specInfoMock.features >> [featureInfoMock]

        when:
        manualExtension.visitSpecAnnotation(Mock(Manual), specInfoMock)
        manualExtension.visitFeatureAnnotation(Mock(Manual), featureInfoMock)

        then:
        1 * testPlanBuilderMock.appendFeature(featureInfoMock)
    }

    def "an annotated feature's spec should be added even if it was not annotated"() {
        SpecInfo specInfoMock = Mock()
        FeatureInfo featureInfoMock = Mock {
            getSpec() >> specInfoMock
        }

        when:
        manualExtension.visitFeatureAnnotation(Mock(Manual), featureInfoMock)

        then:
        1 * testPlanBuilderMock.appendSpec(specInfoMock)
    }

    def "test plan builders"(testPlanBuilders, markManualTestsAsExcluded) {
        given:
        manualExtension.testPlanBuilders >> testPlanBuilders

        and:
        configMock.get("markManualTestsAsExcluded") >> markManualTestsAsExcluded

        and:
        FeatureInfo featureInfoMock = Mock()

        when:
        manualExtension.visitFeatureAnnotation(Mock(Manual), featureInfoMock)

        then:
        (markManualTestsAsExcluded ? 0 : 1) * featureInfoMock.setSkipped(true)
        (markManualTestsAsExcluded ? 1 : 0) * featureInfoMock.setExcluded(true)

        where:
        testPlanBuilders << [ [Mock(TestPlanBuilder), Mock(TestPlanBuilder)], [] ]
        markManualTestsAsExcluded << [true, false]
    }
}
