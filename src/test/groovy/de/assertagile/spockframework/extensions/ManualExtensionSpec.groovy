package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.NodeInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject

import java.lang.reflect.AnnotatedElement

/**
 * Created by mkutz on 02.06.14.
 */
class ManualExtensionSpec extends Specification {

    TestPlanBuilder testPlanBuilderMock = Mock()

    @Subject
    ManualExtension manualExtension = new ManualExtension(testPlanBuilders: [testPlanBuilderMock])

    Manual annotationWithValueMock = Mock() {
        value() >> "annotation value"
    }
    FeatureInfo featureInfoMock = Mock()

    def "when a spec without features is visited only it should be appended"() {
        given:
        SpecInfo specInfoMock = Mock() { getFeatures() >> [] }
        Manual annotationMock = Mock()

        when:
        manualExtension.visitSpecAnnotation(annotationMock, specInfoMock)

        then:
        1 * testPlanBuilderMock.appendSpec(annotationMock, specInfoMock)
    }

    def "when a spec with features is visited it and all its unannotated features should be appended"() {
        given:
        Manual annotationMock = Mock()
        FeatureInfo unannotatedFeatureMock = Mock() {
            getFeatureMethod() >> Mock(MethodInfo) {
                getReflection() >> Mock(AnnotatedElement) { getAnnotations() >> [] }
            }
        }
        FeatureInfo annotatedFeatureInfoMock = Mock() {
            getFeatureMethod() >> Mock(MethodInfo) {
                getReflection() >> Mock(AnnotatedElement) {
                    getAnnotations() >> [Mock(Manual) { annotationType() >> Manual }] }
            }
        }
        SpecInfo specInfoMock = Mock() {
            getFeatures() >> [unannotatedFeatureMock, annotatedFeatureInfoMock]
        }

        when:
        manualExtension.visitSpecAnnotation(annotationMock, specInfoMock)

        then:
        1 * testPlanBuilderMock.appendSpec(annotationMock, specInfoMock)
        1 * testPlanBuilderMock.appendFeature(null, unannotatedFeatureMock)
        0 * testPlanBuilderMock.appendFeature(null, annotatedFeatureInfoMock)
    }

    def "when a feature is visited it should be appended"() {
        given:
        Manual annotationMock = Mock()

        when:
        manualExtension.visitFeatureAnnotation(annotationMock, featureInfoMock)

        then:
        1 * testPlanBuilderMock.appendFeature(annotationMock, featureInfoMock)
    }

    def "when a feature of an not yet appended spec is added, the spec should be added before the feature"() {
        given:
        Manual annotationMock = Mock()
        SpecInfo specInfoMock = Mock() {
            getReflection() >> Mock(AnnotatedElement) {
                getAnnotations() >> []
            }
        }
        FeatureInfo featureInfoMock = Mock() { getParent() >> specInfoMock }

        when:
        manualExtension.visitFeatureAnnotation(annotationMock, featureInfoMock)

        then:
        1 * testPlanBuilderMock.appendSpec(null, specInfoMock)

        and:
        1 * testPlanBuilderMock.appendFeature(annotationMock, featureInfoMock)
    }

    def "when an annotated spec is added due to a annotated feature, its annotation should be used"() {
        given:
        Manual specAnnotationMock = Mock(Manual) { annotationType() >> Manual }
        SpecInfo specInfoMock = Mock() {
            getReflection() >> Mock(AnnotatedElement) {
                getAnnotations() >> [specAnnotationMock]
            }
        }
        FeatureInfo featureInfoMock = Mock() { getParent() >> specInfoMock }

        when:
        manualExtension.visitFeatureAnnotation(Mock(Manual), featureInfoMock)

        then:
        1 * testPlanBuilderMock.appendSpec(specAnnotationMock, specInfoMock)
    }

    def "when two features of an not yet appended spec are added, the spec should be added only once"() {
        given:
        Manual annotationMock = Mock()
        SpecInfo specInfoMock = Mock() {
            getReflection() >> Mock(AnnotatedElement) {
                getAnnotations() >> []
            }
        }
        FeatureInfo firstFeatureInfoMock = Mock() { getParent() >> specInfoMock }
        FeatureInfo secondFeatureInfoMock = Mock() { getParent() >> specInfoMock }

        when:
        manualExtension.visitFeatureAnnotation(annotationMock, firstFeatureInfoMock)
        manualExtension.visitFeatureAnnotation(annotationMock, secondFeatureInfoMock)

        then:
        1 * testPlanBuilderMock.appendSpec(null, specInfoMock)

        and:
        1 * testPlanBuilderMock.appendFeature(annotationMock, firstFeatureInfoMock)
        1 * testPlanBuilderMock.appendFeature(annotationMock, secondFeatureInfoMock)
    }
}
