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
    void appendSpec(SpecInfo spec, String story, String[] knownBugs) {
    }

    @Override
    void appendFeature(FeatureInfo feature, String story, String[] knownBugs) {
        writer << "\"${story ?: ""}\";"

        writer << "\"${feature.parent.name}\";\"${feature.name}\";"

        List<String> featureTexts = []
        feature.blocks.each { BlockInfo block ->
            featureTexts << "${blockKindToString(block.kind)} ${block.texts.join(" and ")}"
        }
        writer << "\"${featureTexts.join("\n")}.\";"
        writer << "\"${knownBugs?.join("\n") ?: ""}\";"
        writer << "\n"

        writer.flush()
    }
}
