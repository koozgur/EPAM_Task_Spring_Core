package com.gymcrm.workload.component;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit Platform entry point for Cucumber component tests (workload service).
 *
 * <p>Discovers {@code .feature} files under {@code features/component/} on the
 * classpath and wires them to step definitions in {@code com.gymcrm.workload.component}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.gymcrm.workload.component")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/component.html, json:target/cucumber-reports/component.json")
public class CucumberComponentTest {
}
