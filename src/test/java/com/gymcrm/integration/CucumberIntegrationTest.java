package com.gymcrm.integration;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit Platform entry point for Cucumber end-to-end integration tests.
 *
 * <p>Discovers {@code .feature} files under {@code features/integration/} on the
 * classpath and wires them to step definitions in {@code com.gymcrm.integration}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/integration")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.gymcrm.integration")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/integration.html, json:target/cucumber-reports/integration.json")
public class CucumberIntegrationTest {
}
