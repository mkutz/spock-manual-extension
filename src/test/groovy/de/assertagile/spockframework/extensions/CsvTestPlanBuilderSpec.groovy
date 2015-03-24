package de.assertagile.spockframework.extensions

import org.spockframework.compiler.model.Spec
import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

/**
 * @author rewe.mkutz
 * @version $Id$
 */
class CsvTestPlanBuilderSpec extends TestPlanBuilderSpec {

    @Subject
    CsvTestPlanBuilder testPlanBuilder = new CsvTestPlanBuilder(
            testPlanFile.absolutePath, issueTrackerBaseUrl, Locale.ENGLISH)

    def "test appendHeader"() {
        when:
        testPlanBuilder.appendHeader()

        then:
        testPlanFile.text == "\"specification\";\"feature\";\"steps\";\"issues\"\n"
    }

    def "test appendSpec"() {
        when:
        testPlanBuilder.appendSpec(Mock(SpecInfo))

        then:
        testPlanFile.text == old(testPlanFile.text)
    }

    def "test appendFeature"() {
        given:
        FeatureInfo featureInfoMock = Mock {
            getName() >> "feature name"
            getBlocks() >> [
                    Mock(BlockInfo) { getKind() >> BlockKind.SETUP; getTexts() >> ["one thing", "another thing"] },
                    Mock(BlockInfo) { getKind() >> BlockKind.WHEN; getTexts() >> ["this"] },
                    Mock(BlockInfo) { getKind() >> BlockKind.THEN; getTexts() >> ["something", "something else"] },
            ]
            getSpec() >> Mock(SpecInfo) {
                getName() >> "spec name"
                getAnnotation(Title) >> Mock(Title) {
                    value() >> "spec title"
                }
                getAnnotation(Issue) >> Mock(Issue) {
                    value() >> ["spec-001"]
                }
            }
            getFeatureMethod() >> Mock(MethodInfo) {
                getAnnotation(Issue) >> Mock(Issue) {
                    value() >> ["${issueTrackerBaseUrl}/feat-001", "feat-002"]
                }
            }
        }

        when:
        testPlanBuilder.appendFeature(featureInfoMock)

        then:
        testPlanFile.text == "\"spec title\";\"feature name\";\"Given one thing\nAnd another thing\nWhen this\nThen something\nAnd something else\";\"spec-001 (${issueTrackerBaseUrl}/spec-001)\nfeat-001 (${issueTrackerBaseUrl}/feat-001), feat-002 (${issueTrackerBaseUrl}/feat-002)\"\n"
    }
}
