package de.assertagile.spockframework.extensions

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * <p>
 * Extension for the <a href="http://spockframework.org">Spock Framework</a> to be able to write manual test plans in
 * Spock beside of automated tests.
 * </p>
 * <p>
 * Just mark a singe feature method or a whole specification with the annotation {@link Manual}.
 * </p>
 */
public class ManualExtension extends AbstractAnnotationDrivenExtension<Manual> {

    /** {@link ConfigObject} for the extension. */
    private ConfigObject config

    /** A {@link List} of {@link TestPlanBuilder}s to build the test plan. */
    private List<TestPlanBuilder> testPlanBuilders

    /** The currently to be appended {@link SpecInfo}. */
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
        currentSpec = spec
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
        Manual specAnnotation = feature.parent.reflection.getAnnotation(Manual)
        if (currentSpec != feature.parent) {
            testPlanBuilders.each { it.appendSpec(feature.parent, specAnnotation?.story(), specAnnotation?.knownBugs()) }
            currentSpec = feature.parent
        }
        testPlanBuilders.each { it.appendFeature(feature, annotation.story() ?: specAnnotation?.story() ?: "",
                (annotation.knownBugs() + (specAnnotation?.knownBugs() ?: [])) as String[]) }
    }

    /**
     * Marks the {@link FeatureInfo} as excluded or skipped depending on the configuration parameter
     * {@code markManualTestsAsExcluded}.
     *
     * @param feature the {@link FeatureInfo} it be marked.
     */
    private void markFeature(FeatureInfo feature) {
        if (config.get("markManualTestsAsExcluded")) feature.excluded = true
        else feature.skipped = true
    }
}