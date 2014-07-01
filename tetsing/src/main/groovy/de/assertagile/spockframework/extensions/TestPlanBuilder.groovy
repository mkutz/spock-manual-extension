package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 27.05.14.
 */
abstract class TestPlanBuilder {

    private static final CONFIG = ManualExtension.CONFIG

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

    public TestPlanBuilder(String filePath) {
        this.file = new File(filePath)
    }

    abstract public TestPlanBuilder appendSpec(Manual annotation, SpecInfo spec)

    abstract public TestPlanBuilder appendFeature(Manual annotation, FeatureInfo feature)

    public static String getSpecTitle(Manual annotation, SpecInfo spec) {
        annotation.value() ?: camelCaseToString(spec.name)
    }

    protected Writer getWriter() {
        if (!writer) {
            writer = new FileWriter(file, false)
        }
        return writer
    }

    protected void setWriter(Writer writer) {
        this.writer = writer
    }

    protected static String camelCaseToString(String camelCase) {
        camelCase.replaceAll(/(.)([A-Z])/, /$1 $2/)
    }

    protected static String blockKindToString(BlockKind blockKind) {
        BLOCK_NAME[blockKind][CONFIG.locale as Locale]
    }

}
