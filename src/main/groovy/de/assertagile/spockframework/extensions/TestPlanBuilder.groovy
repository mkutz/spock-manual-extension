package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.util.Pair

/**
 * Abstract super class for all test plan builders.
 */
abstract class TestPlanBuilder {

    /** Translations of {@link org.spockframework.runtime.model.BlockKind}s to be used in the test plan. */
    private static final Map<BlockKind, Map<Locale, String>> BLOCK_NAME = [
            (BlockKind.SETUP): [(Locale.ENGLISH): "Given", (Locale.GERMAN): "Gegeben"],
            (BlockKind.CLEANUP): [(Locale.ENGLISH): "Cleanup", (Locale.GERMAN): "Abschließend"],
            (BlockKind.EXPECT): [(Locale.ENGLISH): "Expect", (Locale.GERMAN): "Erwarte"],
            (BlockKind.THEN): [(Locale.ENGLISH): "Then", (Locale.GERMAN): "Dann"],
            (BlockKind.WHERE): [(Locale.ENGLISH): "Where", (Locale.GERMAN): "Mit"],
            (BlockKind.WHEN): [(Locale.ENGLISH): "When", (Locale.GERMAN): "Wenn"]
    ]

    /** The {@link File} to write the test plan to. */
    private File file

    /** The {@Writer} used to write the test plan. */
    private Writer writer

    /** The {@link Locale} for the test plan. */
    protected Locale locale

    /** The base URL for the issue tracker (used to generate links in test plans). */
    protected String issueTrackerBaseUrl

    /**
     * @param filePath the location of the file to write the test plan to.
     * @param issueTrackerBaseUrl the base URL of the issue tracker. This is used to generate an issue's URL or extract
     * an issues name from its URL (see {@link #getIssueNameAndUrl(java.lang.String)}).
     * @param locale the {@link Locale} to use for the test plan.
     */
    public TestPlanBuilder(String filePath, String issueTrackerBaseUrl = null, Locale locale = Locale.ENGLISH) {
        this.file = new File(filePath).absoluteFile
        this.locale = locale
        if (issueTrackerBaseUrl) {
            this.issueTrackerBaseUrl = issueTrackerBaseUrl.endsWith("/") ? issueTrackerBaseUrl : "${issueTrackerBaseUrl}/"
        }
    }

    /**
     * Appends the general header to the test plan.
     */
    abstract public void appendHeader()

    /**
     * @param spec the {@link SpecInfo} whose feature methods should be appended to the test plan.
     */
    abstract public void appendSpec(SpecInfo spec)

    /**
     * @param feature the {@link FeatureInfo} to be appended to the test plan.
     */
    abstract public void appendFeature(FeatureInfo feature)

    /**
     * @return the {@link Writer} to be used for output. Will initialize {@link #writer} if necessary.
     */
    protected Writer getWriter() {
        if (!writer) {
            writer = new FileWriter(file, false)
        }
        return writer
    }

    /**
     * @param blockKind the {@link BlockKind}.
     * @return a localized {@link String} for the given {@link BlockKind}.
     */
    protected String blockKindToString(BlockKind blockKind) {
        BLOCK_NAME[blockKind][locale]
    }

    /**
     * @param issue the issue {@link String} taken from {@link spock.lang.Issue#value()}.
     * @return a {@link Pair} of {@link String}s. The {@link Pair#first()} represents the issue's name,
     * {@link Pair#second()} the URL in the issue tracker.
     */
    protected Pair<String, String> getIssueNameAndUrl(String issue) {
        if (issue.startsWith(issueTrackerBaseUrl)) return Pair.of(issue - issueTrackerBaseUrl, issue)
        if (!issue.startsWith("http")) return Pair.of(issue, issueTrackerBaseUrl + issue)
        return Pair.of(issue, issue)
    }
}
