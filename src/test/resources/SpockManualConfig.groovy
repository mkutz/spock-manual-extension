import de.assertagile.spockframework.extensions.MarkDownTestPlanBuilder

Locale locale = Locale.ENGLISH
boolean markManualTestsAsExcluded = true

testPlanBuilders = [
    new MarkDownTestPlanBuilder("target/test_plan.md", locale),
]