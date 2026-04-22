@component @workload @jms
Feature: Workload JMS Consumer
  As the workload service
  I want to process workload events received via JMS
  So that trainer hours are updated asynchronously

  Background:
    Given a valid JWT token

  @positive
  Scenario: JMS ADD message updates MongoDB
    When a JMS workload ADD message is sent for trainer "jane.doe" with 45 minutes on "2026-08-10"
    Then within 10 seconds the workload summary for "jane.doe" should show 45 minutes for year 2026 month 8

  @positive
  Scenario: JMS DELETE message updates MongoDB
    When a JMS workload ADD message is sent for trainer "jane.doe" with 90 minutes on "2026-08-10"
    And within 10 seconds the workload summary for "jane.doe" should show 90 minutes for year 2026 month 8
    When a JMS workload DELETE message is sent for trainer "jane.doe" with 30 minutes on "2026-08-10"
    Then within 10 seconds the workload summary for "jane.doe" should show 60 minutes for year 2026 month 8

  @edge-case
  Scenario: JMS messages accumulate across months
    When a JMS workload ADD message is sent for trainer "jane.doe" with 60 minutes on "2026-09-05"
    And a JMS workload ADD message is sent for trainer "jane.doe" with 30 minutes on "2026-10-12"
    Then within 10 seconds the workload summary for "jane.doe" should show 60 minutes for year 2026 month 9
    And within 10 seconds the workload summary for "jane.doe" should show 30 minutes for year 2026 month 10
