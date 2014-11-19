import de.assertagile.spockframework.extensions.CsvTestPlanBuilder
import de.assertagile.spockframework.extensions.HtmlTestPlanBuilder

Locale locale = Locale.ENGLISH
String jiraUrl = null
String jiraPid = null

testPlanBuilders = [
    new HtmlTestPlanBuilder("target/test_plan.html", locale, jiraUrl, jiraPid),
    new CsvTestPlanBuilder("target/test_plan.csv", locale)
]