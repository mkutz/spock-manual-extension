package de.assertagile.spock.extensions.manual

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 22.05.14.
 */
public class ManualExtension extends AbstractAnnotationDrivenExtension<Manual> {

    private static final Map<BlockKind, Map<Locale, String>> BLOCK = [
            (BlockKind.SETUP): [(Locale.ENGLISH): "Given", (Locale.GERMAN): "Gegeben"],
            (BlockKind.CLEANUP): [(Locale.ENGLISH): "Cleanup", (Locale.GERMAN): "Abschließend"],
            (BlockKind.EXPECT): [(Locale.ENGLISH): "Expected", (Locale.GERMAN): "Erwartet"],
            (BlockKind.THEN): [(Locale.ENGLISH): "Then", (Locale.GERMAN): "Dann"],
            (BlockKind.WHERE): [(Locale.ENGLISH): "Where", (Locale.GERMAN): "Mit"],
            (BlockKind.WHEN): [(Locale.ENGLISH): "When", (Locale.GERMAN): "Wenn"]
    ]

    private SpecInfo appendedSpec = null
    private List<FeatureInfo> appendedFeatures = []
    private File testPlanFile

    public ManualExtension() {
        getTestPlanFile().delete()
    }

    public void visitSpecAnnotation(Manual annotation, SpecInfo spec) {
        spec.features.each {
            visitFeatureAnnotation(annotation, it)
        }
    }

    public void visitFeatureAnnotation(Manual annotation, FeatureInfo feature) {
        appendSpecToTestPlan(feature.parent)
        appendFeatureToTestPlan(feature)
        feature.excluded = true  //feature.skipped = true
    }

    private void appendFeatureToTestPlan(FeatureInfo feature) {
        if (appendedFeatures.contains(feature)) return

        getTestPlanFile().write("${feature.name}\n${"-" * feature.name.size()}\n")
        feature.blocks.each {
            getTestPlanFile() << "* ${BLOCK[it.kind][Locale.GERMAN]} ${it.texts[0]}\n"
            if (it.texts.size() > 1) it.texts[1..-1].each {
                getTestPlanFile() << "  * and ${it}\n"
            }
        }
        getTestPlanFile() << "\n"

        appendedFeatures << feature
    }

    private void appendSpecToTestPlan(SpecInfo spec) {
        if (appendedSpec != spec) {
            String specTitle = specTitle(spec)
            getTestPlanFile() << "${specTitle}\n${"=" * specTitle.size()}\n\n"
        }
        appendedSpec = spec
    }

    private File getTestPlanFile() {
        return testPlanFile ?: new File("target/manual_tests.md").absoluteFile
    }

    private static String specTitle(SpecInfo spec) {
        spec.name.replace("SystemSpec", "").replaceAll(/([A-Z])/, ' $1').trim()
    }
}