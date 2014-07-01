package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 28.05.14.
 */
class CsvTestPlanBuilder extends TestPlanBuilder {

    SpecInfo currentSpec = null

    CsvTestPlanBuilder(String filePath) {
        super(filePath)
    }

    @Override
    TestPlanBuilder appendSpec(Manual annotation, SpecInfo spec) {
        currentSpec = spec
        return this
    }

    @Override
    TestPlanBuilder appendFeature(Manual annotation, FeatureInfo feature) {
        writer << "\"${annotation.story()}\";"

        if (currentSpec != feature.parent) {
            appendSpec(null, feature.parent)
        }
        writer << "\"${currentSpecTitle}\";\"${feature.getName()}\";"

        List<String> featureTexts = []
        feature.blocks.each { BlockInfo block ->
            featureTexts << "${blockKindToString(block.kind)} ${block.texts.join(" and ")}"
        }
        writer << "\"${featureTexts.join("\n")}.\";"

        writer << "\"${annotation.knownBugs().join("\n")}\";"

        writer << "\n"

        return this
    }

    String getCurrentSpecTitle() {
        return currentSpec.getReflection().getAnnotation(Manual).value() ?: currentSpec.getName()
    }
}
