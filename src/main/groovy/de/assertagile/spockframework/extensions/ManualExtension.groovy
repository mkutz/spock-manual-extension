package de.assertagile.spockframework.extensions

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * <p>
 * Extension for the <a href="http://spockframework.org">Spock Framework</a> to be able to write manual test plans in
 * Spock beside of automated tests.
 * </p>
 *
 * <p>
 * Just mark a singe feature method or a whole {@link Specification} with the annotation {@link Manual}.
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
    }

    private ConfigObject getConfig() {
        if (!config) {
            URL configUrl = getClass().getClassLoader().getResource("SpockManualConfig.groovy")
            config = configUrl ? new ConfigSlurper().parse(configUrl) : new ConfigObject()
        }
        return config
    }

    private List<TestPlanBuilder> getTestPlanBuilders() {
        if (testPlanBuilders == null) {
            testPlanBuilders = getConfig().get("testPlanBuilders", []) as List<TestPlanBuilder>
            testPlanBuilders.each { it.appendHeader() }
        }
        return testPlanBuilders
    }

    /**
     * Called when a marked specification type is visited. Adds all contained feature methods to the test plan using
     * {@link #visitFeatureAnnotation(Manual, org.spockframework.runtime.model.FeatureInfo)}.
     *
     * @param annotation
     *          the {@link Manual} annotation at the specification type. Might contain an alternative title as value.
     * @param spec
     *          the {@link SpecInfo} for the visited specification class.
     */
    public void visitSpecAnnotation(Manual annotation, SpecInfo spec) {
        currentSpec = spec

        getTestPlanBuilders().each { it.appendSpec(spec) }
        spec.features.each { FeatureInfo feature ->
            markFeature(feature)
            getTestPlanBuilders().each { TestPlanBuilder testPlanBuilder ->
                testPlanBuilder.appendFeature(feature)
            }
        }
    }

    /**
     * Called when a marked feature method is visited. Adds the feature to the test plan.
     *
     * @param annotation
     *          the {@link Manual} annotation at the specification type. Might contain an alternative title as value.
     * @param feature
     *          the {@link FeatureInfo} for the visited feature method.
     */
    public void visitFeatureAnnotation(Manual annotation, FeatureInfo feature) {
        markFeature(feature)

        if (currentSpec != feature.spec) {
            getTestPlanBuilders().each { it.appendSpec(feature.spec) }
            getTestPlanBuilders().each { TestPlanBuilder testPlanBuilder ->
                testPlanBuilder.appendFeature(feature)
            }
        }
    }

    /**
     * Marks the {@link FeatureInfo} as excluded or skipped depending on the configuration parameter
     * {@code markManualTestsAsExcluded}.
     *
     * @param feature the {@link FeatureInfo} it be marked.
     */
    private void markFeature(FeatureInfo feature) {
        if (getConfig().get("markManualTestsAsExcluded")) feature.excluded = true
        else feature.skipped = true
    }
}