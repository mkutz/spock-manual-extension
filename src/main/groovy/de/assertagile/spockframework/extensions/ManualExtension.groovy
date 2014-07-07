package de.assertagile.spockframework.extensions

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 22.05.14.
 */
public class ManualExtension extends AbstractAnnotationDrivenExtension<Manual> {

    private ConfigObject config
    private List<TestPlanBuilder> testPlanBuilders

    private SpecInfo currentSpec = null

    /**
     * Standard constructor.
     */
    public ManualExtension() {
        this.config = new ConfigSlurper().parse(GroovyResourceLoader.getResource("/SpockManualConfig.groovy"))
        this.testPlanBuilders = config.get("testPlanBuilders", []) as List<TestPlanBuilder>
        this.testPlanBuilders.each { it.appendHeader() }
    }

    /**
     * Called when a marked specification type is visited. Adds all contained feature methods to the test plan using
     * {@link #visitFeatureAnnotation(Manual, org.spockframework.runtime.model.FeatureInfo)}.
     *
     * @param annotation
     *          the {@link Manual} annotation at the specification type. Might contain an alternative title as value.
     * @param spec
     *          the {@link org.spockframework.runtime.model.SpecInfo} for the visited specification class.
     */
    public void visitSpecAnnotation(Manual annotation, SpecInfo spec) {
        testPlanBuilders.each { it.appendSpec(spec, annotation.story(), annotation.knownBugs()) }
    }

    /**
     * Called when a marked feature method is visited. Adds the feature to the test plan.
     *
     * @param annotation
     *          the {@link Manual} annotation at the specification type. Might contain an alternative title as value.
     * @param spec
     *          the {@link org.spockframework.runtime.model.SpecInfo} for the visited specification class.
     */
    public void visitFeatureAnnotation(Manual annotation, FeatureInfo feature) {
        markFeature(feature)
        Manual specAnnotation = feature.parent.getAnnotation(Manual)
        if (currentSpec != feature.parent) {
            testPlanBuilders.each { it.appendSpec(feature.parent, specAnnotation?.story(), specAnnotation?.knownBugs()) }
            currentSpec = feature.parent
        }
        testPlanBuilders.each { it.appendFeature(feature, annotation.story() ?: specAnnotation?.story() ?: "",
                (annotation.knownBugs() + (specAnnotation?.knownBugs() ?: [])) as String[]) }
    }

    private markFeature(FeatureInfo feature) {
        if (config.get("markManualTestsAsExcluded")) feature.excluded = true
        else feature.skipped = true
    }
}