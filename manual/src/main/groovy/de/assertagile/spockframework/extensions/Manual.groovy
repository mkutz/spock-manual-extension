package de.assertagile.spockframework.extensions

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Indicates the marked specification type or feature method as manual test. In case of a specification type all its
 * feature methods will be marked manual.
 *
 * The extension will automatically exclude the marked features as excluded from the test run (meaning they do not
 * appear in any way in the test statistics) and uses the feature's block texts to create a test plan file.
 *
 * For instance this feature
 *
 * <pre>
 * @Manual("Login and logout")
 * class LoginLogoutSystemSpec {
 *     def "Logout should work." {
 *         given: "a logged in user"
 *         when: "the user clicks the \"logout\" button"
 *         then: "the user is logged out."
 *     }
 * }
 * </pre>
 *
 * will result in the following markdown test plan entry
 *
 * <pre>
 * Login and logout
 * ================
 *
 * Login should work.
 * ------------------
 * Given a logged in user
 * When the user clicks the "logout" button
 * Then the user is logged out
 * </pre>
 *
 * @author Michael Kutz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(ManualExtension)
public @interface Manual {
    /**
     * The title of the marked specification type or feature method. The test plan file will use this if given,
     * otherwise the specification's or feature's name will be used.
     *
     * @return the title of the marked specification type or method.
     */
    String value() default "";

    String story() default "";

    String[] knownBugs() default [];
}