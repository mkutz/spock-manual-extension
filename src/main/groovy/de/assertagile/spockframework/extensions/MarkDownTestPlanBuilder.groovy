package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Issue
import spock.lang.Title

/**
 * @author rewe.mkutz
 * @version $Id$
 */
class MarkDownTestPlanBuilder extends TestPlanBuilder {

    MarkDownTestPlanBuilder(String filePath, Locale locale = Locale.ENGLISH) {
        super(filePath, locale)
    }

    @Override
    void appendHeader() {
        writer << "#Manual Test Plan\n"
        writer.flush()
    }

    @Override
    void appendSpec(SpecInfo spec) {
        writer << "\n##${spec.getAnnotation(Title)?.value() ?: spec.name}\n"

        List<String> issues = spec.getAnnotation(Issue)?.value()
        if (issues) {
            writer << "${issues.join(", ")}\n"
        }

        writer.flush()
    }

    @Override
    void appendFeature(FeatureInfo feature) {
        List<String> issues = feature.featureMethod.getAnnotation(Issue)?.value()

        writer << "\n###${feature.name}\n"
        if (issues) writer << "${issues.join(", ")}\n"
        writer << "\n"
        feature.blocks.each { BlockInfo block ->
            writer << "- *${blockKindToString(block.kind)}* "
            writer << block.texts.join("\n- *And* ")
            writer << "\n"
        }
        writer << "\n"
        writer.flush()
    }
}
