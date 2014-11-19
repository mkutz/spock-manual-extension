package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 27.05.14.
 */
abstract class TestPlanBuilder {

    /**
     * Translations of {@link org.spockframework.runtime.model.BlockKind}s to be used in the test plan.
     */
    private static final Map<BlockKind, Map<Locale, String>> BLOCK_NAME = [
            (BlockKind.SETUP): [(Locale.ENGLISH): "Given", (Locale.GERMAN): "Gegeben"],
            (BlockKind.CLEANUP): [(Locale.ENGLISH): "Cleanup", (Locale.GERMAN): "Abschließend"],
            (BlockKind.EXPECT): [(Locale.ENGLISH): "Expect", (Locale.GERMAN): "Erwarte"],
            (BlockKind.THEN): [(Locale.ENGLISH): "Then", (Locale.GERMAN): "Dann"],
            (BlockKind.WHERE): [(Locale.ENGLISH): "Where", (Locale.GERMAN): "Mit"],
            (BlockKind.WHEN): [(Locale.ENGLISH): "When", (Locale.GERMAN): "Wenn"]
    ]

    private File file
    private Writer writer
    protected Locale locale

    public TestPlanBuilder(String filePath, Locale locale) {
        this.file = new File(filePath).absoluteFile
        this.locale = locale
    }

    abstract public void appendHeader()

    abstract public void appendSpec(SpecInfo spec, String story, String[] knownBugs)

    abstract public void appendFeature(FeatureInfo feature, String story, String[] knownBugs)

    protected Writer getWriter() {
        if (!writer) {
            writer = new FileWriter(file, false)
        }
        return writer
    }

    protected String blockKindToString(BlockKind blockKind) {
        BLOCK_NAME[blockKind][locale]
    }

    protected void setWriter(Writer writer) {
        this.writer = writer
    }
}
