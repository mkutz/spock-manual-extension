package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.util.Pair
import spock.lang.Issue
import spock.lang.Title

/**
 * {@link TestPlanBuilder} for Markdown test plans.
 */
class MarkDownTestPlanBuilder extends TestPlanBuilder {

    MarkDownTestPlanBuilder(String filePath, String issueTrackerBaseUrl = null, Locale locale = Locale.ENGLISH) {
        super(filePath, issueTrackerBaseUrl, locale)
    }

    @Override
    void appendHeader() {
        writer << "#Manual Test Plan\n"
        writer.flush()
    }

    @Override
    void appendSpec(SpecInfo spec) {
        writer << "\n##${getSpecName(spec)}\n"

        List<String> issues = getIssues(spec)

        if (issues) {
            writer << "${getIssueLinks(issues).join(", ")}\n"
        }

        writer.flush()
    }

    @Override
    void appendFeature(FeatureInfo feature) {
        List<String> issues = getIssues(feature)

        writer << "\n###${feature.name}\n"
        if (issues) writer << "${getIssueLinks(issues).join(", ")}\n"
        writer << "\n"
        feature.blocks.each { BlockInfo block ->
            writer << "- *${blockKindToString(block.kind)}* "
            writer << block.texts.join("\n- *And* ")
            writer << "\n"
        }
        writer << "\n"
        writer.flush()
    }

    private List<String> getIssueLinks(List<String> issues) {
        return issues.collect { String issue ->
            Pair<String, String> issueNameAndUrl = getIssueNameAndUrl(issue)
            "[${issueNameAndUrl.first()}](${issueNameAndUrl.second()})"
        }
    }
}
