package de.assertagile.spockframework.extensions

import groovy.xml.MarkupBuilder
import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 27.05.14.
 */
class HtmlTestPlanBuilder extends TestPlanBuilder {

    private final String jiraUrl
    private final String jiraPid
    private final boolean jiraEnabled

    private MarkupBuilder _htmlWriter

    public HtmlTestPlanBuilder(String filePath, String jiraUrl = null, String jiraPid = null) {
        super(filePath)

        this.jiraEnabled = jiraUrl && jiraPid
        this.jiraUrl = jiraUrl
        this.jiraPid = jiraPid
    }

    @Override
    TestPlanBuilder appendSpec(Manual annotation, SpecInfo spec) {
        htmlWriter.h2 getSpecTitle(annotation, spec)
        return this
    }

    @Override
    TestPlanBuilder appendFeature(Manual annotation, FeatureInfo feature) {
        htmlWriter.h3 feature.name

        htmlWriter.p {
            a(href: issueJiraLink(annotation.story()), "Story")
            h4 "Bugs: "
            annotation.knownBugs().each { String bugId ->
                a(href: issueJiraLink(bugId), bugId)
            }
        }

        htmlWriter.p {
            a(href: openBugJiraLink(feature), "create Bug")
        }

        htmlWriter.dl {
            feature.blocks.each { BlockInfo block ->
                dt blockKindToString(block.kind)
                dd {
                    ul {
                        block.texts.each { String text ->
                            li text
                        }
                    }
                }
            }
        }
        return this
    }

    private String openBugJiraLink(FeatureInfo feature) {
        StringBuilder description = new StringBuilder()
        feature.blocks.each { BlockInfo block ->
            description << "${block.kind} ${block.texts.join("\nand")}\n"
        }

        "${jiraUrl}/secure/CreateIssueDetails!init.jspa?" +
                "pid=${jiraPid}&" +
                "issuetype=1&" +
                "summary=${URLEncoder.encode(feature.name, "utf-8")}&" +
                "description=${URLEncoder.encode(description.toString(), "utf-8")}"
    }

    private String issueJiraLink(String issueId) {
        "${jiraUrl}/browse/${issueId}"
    }

    private MarkupBuilder getHtmlWriter() {
        if (!_htmlWriter) {
            _htmlWriter = new MarkupBuilder(writer)
            _htmlWriter.escapeAttributes = false
        }
        return _htmlWriter
    }
}
