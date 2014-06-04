import de.assertagile.spockframework.extensions.CsvTestPlanBuilder
import de.assertagile.spockframework.extensions.HtmlTestPlanBuilder

locale = Locale.ENGLISH
jiraUrl = "http://jira.allesklar.org/"
jiraPid = "14800"

testPlanBuilders = [
    new HtmlTestPlanBuilder("test_plan.html", jiraUrl, jiraPid),
    new CsvTestPlanBuilder("test_plan.csv")
]