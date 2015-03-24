package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.util.Pair

/**
 * @author rewe.mkutz
 * @version $Id$
 */
class CsvTestPlanBuilder extends TestPlanBuilder {

    private static final List<String> HEADER = [ "specification", "feature", "steps", "issues" ]

    CsvTestPlanBuilder(String filePath, String issueTrackerBaseUrl, Locale locale) {
        super(filePath, issueTrackerBaseUrl, locale)
    }

    @Override
    void appendHeader() {
        appendLine(HEADER)
    }

    @Override
    void appendSpec(SpecInfo spec) {}

    @Override
    void appendFeature(FeatureInfo feature) {
        String specificationName = getSpecName(feature.spec)
        String featureName = feature.name
        String issues = "${getIssueLinks(getIssues(feature.spec)).join(", ")}\n${getIssueLinks(getIssues(feature)).join(", ")}"

        List<String> blockTexts = feature.blocks.collect { BlockInfo block ->
            "${blockKindToString(block.kind)} ${block.texts.join("\nAnd ")}"
        }
        String steps = blockTexts.join("\n")

        appendLine([specificationName, featureName, steps, issues] as List<String>)
    }

    private void appendLine(List<String> cells) {
        writer << "\"${cells.join("\";\"")}\"\n"
        writer.flush()
    }

    private List<String> getIssueLinks(List<String> issues) {
        return issues.collect { String issue ->
            Pair<String, String> issueNameAndUrl = getIssueNameAndUrl(issue)
            "${issueNameAndUrl.first()} (${issueNameAndUrl.second()})"
        }
    }
}
