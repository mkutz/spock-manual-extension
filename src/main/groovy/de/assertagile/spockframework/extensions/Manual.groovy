package de.assertagile.spockframework.extensions

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * <p>
 * Indicates the marked {@link spock.lang.Specification} type or feature method as manual test.
 * In case of a {@link spock.lang.Specification} type all its feature methods will be marked manual.
 * </p>
 *
 * <p>
 * The extension will automatically exclude the marked features as excluded from the test run (meaning they do not
 * appear in any way in the test statistics) and uses the feature's block texts to create a test plan file.
 * </p>
 *
 * @author Michael Kutz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(ManualExtension)
public @interface Manual {

    String title() default "";

    String story() default "";

    String[] knownBugs() default [];
}