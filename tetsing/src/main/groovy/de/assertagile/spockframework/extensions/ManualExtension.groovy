package de.assertagile.spockframework.extensions

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 22.05.14.
 */
public class ManualExtension extends AbstractAnnotationDrivenExtension<Manual> {

    protected static ConfigObject CONFIG = new ConfigSlurper().parse(GroovyResourceLoader.getResource("/SpockManualConfig.groovy"))
    private List<TestPlanBuilder> testPlanBuilders = CONFIG.testPlanBuilders

    private SpecInfo currentSpec = null

    /**
     * Standard constructor.
     */
    public ManualExtension() {
    }

    /**
     * Called when a marked specification type is visited. Adds all contained feature methods to the test plan using
     * {@link #visitFeatureAnnotation(Manual, FeatureInfo)}.
     *
     * @param annotation
     *          the {@link Manual} annotation at the specification type. Might contain an alternative title as value.
     * @param spec
     *          the {@link SpecInfo} for the visited specification class.
     */
    public void visitSpecAnnotation(Manual annotation, SpecInfo spec) {
        testPlanBuilders.each { it.appendSpec(annotation, spec) }
        currentSpec = spec
        spec.features.each { FeatureInfo feature ->
            if (!feature.getReflection().getAnnotations().find { it.annotationType() == Manual }) {
                testPlanBuilders.each { it.appendFeature(null, feature) }
            }
        }
    }

    /**
     * Called when a marked feature method is visited. Adds the feature to the test plan.
     *
     * @param annotation
     *          the {@link Manual} annotation at the specification type. Might contain an alternative title as value.
     * @param spec
     *          the {@link SpecInfo} for the visited specification class.
     */
    public void visitFeatureAnnotation(Manual annotation, FeatureInfo feature) {
        if (currentSpec != feature.getParent()) {
            Manual specAnnotation = feature.getParent().getReflection().getAnnotations().find {
                it.annotationType() == Manual
            }
            testPlanBuilders.each { it.appendSpec(specAnnotation, feature.getParent()) }
            currentSpec = feature.getParent()
        }
        testPlanBuilders.each { it.appendFeature(annotation, feature) }
    }
}