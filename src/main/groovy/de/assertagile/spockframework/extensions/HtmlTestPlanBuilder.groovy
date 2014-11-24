package de.assertagile.spockframework.extensions

import groovy.xml.MarkupBuilder
import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * A {@link TestPlanBuilder} to create HTML test plans.
 */
class HtmlTestPlanBuilder extends TestPlanBuilder {

    /** Base URL to build JIRA links to known bugs and story tickets. */
    private final String jiraUrl
    /** JIRA project ID needed to build JIRA links to create new bugs. */
    private final String jiraPid
    /** Toggle to activate JIRA links in test plans. */
    private final boolean jiraEnabled

    private MarkupBuilder _htmlWriter

    /**
     * @param filePath the location of the file to write the test plan to.
     * @param locale the {@link Locale} to use for the test plan.
     * @param jiraUrl base URL to build JIRA links to known bugs and story tickets.
     * @param jiraPid JIRA project ID needed to build JIRA links to create new bugs.
     */
    public HtmlTestPlanBuilder(String filePath, Locale locale = Locale.ENGLISH, String jiraUrl = null, String jiraPid = null) {
        super(filePath, locale)

        this.jiraEnabled = jiraUrl && jiraPid
        this.jiraUrl = jiraUrl
        this.jiraPid = jiraPid
    }

    @Override
    void appendHeader() {}

    @Override
    void appendSpec(SpecInfo spec, String story, String[] knownBugs) {
        htmlWriter.h2 "${story ? "${story}: " : ""}${spec.name}"
    }

    @Override
    void appendFeature(FeatureInfo feature, String story, String[] knownBugs) {
        htmlWriter.h3 {
            if (story) {
                if (jiraEnabled) {
                    a(class: "story", href: issueJiraLink(story), "${story}: ")
                } else {
                    span(class: "story", "${story}: ")
                }
            }
            span(class: "featureName", feature.name)
        }

        htmlWriter.p {
            if (knownBugs) {
                h4 "Bugs: "
                ul(class: "bugList") {
                    knownBugs.each { String bugId ->
                        li(class: "bug") {
                            jiraEnabled ? a(href: issueJiraLink(bugId), bugId) : span(bugId)
                        }
                    }
                }
            }
        }

        if (jiraEnabled) htmlWriter.p {
            a(href: openBugJiraLink(feature), "create Bug")
        }

        htmlWriter.dl(class: "testStepList") {
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
