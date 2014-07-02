package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 28.05.14.
 */
class CsvTestPlanBuilder extends TestPlanBuilder {

    private static final Map<Locale, List<String>> HEADER = [
            (Locale.ENGLISH): ["User Story", "Feature", "Specification", "Test", "Known Issues"],
            (Locale.GERMAN): ["User Story", "Feature", "Spezifikation", "Test", "Bekannte Fehler"]
    ]

    SpecInfo currentSpec = null

    CsvTestPlanBuilder(String filePath, Locale locale = Locale.ENGLISH) {
        super(filePath, locale)
    }

    @Override
    void appendHeader() {
        writer << HEADER[locale].collect { "\"${it}\"" }.join(";")
        writer << "\n"
        writer.flush()
    }

    @Override
    void appendSpec(Manual annotation, SpecInfo spec) {
        currentSpec = spec
    }

    @Override
    void appendFeature(Manual annotation, FeatureInfo feature) {
        writer << "\"${annotation?.story() ?: ""}\";"

        if (currentSpec != feature.parent) {
            appendSpec(null, feature.parent)
        }
        writer << "\"${currentSpecTitle}\";\"${feature.getName()}\";"

        List<String> featureTexts = []
        feature.blocks.each { BlockInfo block ->
            featureTexts << "${blockKindToString(block.kind)} ${block.texts.join(" and ")}"
        }
        writer << "\"${featureTexts.join("\n")}.\";"
        writer << "\"${annotation?.knownBugs()?.join("\n") ?: ""}\";"
        writer << "\n"

        writer.flush()
    }

    String getCurrentSpecTitle() {
        return currentSpec.getReflection().getAnnotation(Manual)?.value() ?: currentSpec.getName()
    }
}
