package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

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

    /**
     * @param filePath the location of the file to write the test plan to.
     * @param locale the {@link Locale} to use for the test plan.
     */
    public TestPlanBuilder(String filePath, Locale locale) {
        this.file = new File(filePath).absoluteFile
        this.locale = locale
    }

    /**
     * Appends the general header to the test plan.
     */
    abstract public void appendHeader()

    /**
     *
     * @param spec
     * @param story
     * @param knownBugs
     */
    abstract public void appendSpec(SpecInfo spec, String story, String[] knownBugs)

    abstract public void appendFeature(FeatureInfo feature, String story, String[] knownBugs)

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
     * @param writer to be used for output.
     */
    protected void setWriter(Writer writer) {
        this.writer = writer
    }
}
