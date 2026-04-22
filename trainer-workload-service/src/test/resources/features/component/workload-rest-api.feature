@component @workload
Feature: Workload REST API
  As a service consumer
  I want to manage trainer workloads via REST API
  So that training hours are tracked and summarized correctly

  @positive
  Scenario: Add workload via POST and verify via GET summary
    Given a valid JWT token
    When a workload ADD event is posted for trainer "john.doe" with 60 minutes on "2026-06-15"
    Then the response status should be 200
    When the workload summary is requested for "john.doe"
    Then the response status should be 200
    And the summary should show 60 minutes for year 2026 month 6

  @positive
  Scenario: Subtract workload via DELETE action
    Given a valid JWT token
    And a workload ADD event is posted for trainer "john.doe" with 90 minutes on "2026-06-15"
    When a workload DELETE event is posted for trainer "john.doe" with 30 minutes on "2026-06-15"
    Then the response status should be 200
    When the workload summary is requested for "john.doe"
    Then the summary should show 60 minutes for year 2026 month 6

  @edge-case
  Scenario: Unknown trainer returns empty summary
    Given a valid JWT token
    When the workload summary is requested for "unknown.trainer"
    Then the response status should be 200
    And the summary years list should be empty

  @positive
  Scenario: Multi-month aggregation
    Given a valid JWT token
    When a workload ADD event is posted for trainer "john.doe" with 60 minutes on "2026-06-15"
    And a workload ADD event is posted for trainer "john.doe" with 45 minutes on "2026-07-10"
    And a workload ADD event is posted for trainer "john.doe" with 30 minutes on "2026-06-20"
    When the workload summary is requested for "john.doe"
    Then the summary should show 90 minutes for year 2026 month 6
    And the summary should show 45 minutes for year 2026 month 7

  @edge-case
  Scenario: Delete floors duration at zero
    Given a valid JWT token
    And a workload ADD event is posted for trainer "john.doe" with 30 minutes on "2026-06-15"
    When a workload DELETE event is posted for trainer "john.doe" with 100 minutes on "2026-06-15"
    Then the response status should be 200
    When the workload summary is requested for "john.doe"
    Then the summary should show 0 minutes for year 2026 month 6

  @negative @security
  Scenario: Unauthenticated GET returns 401
    When the workload summary is requested for "john.doe" without authentication
    Then the response status should be 401

  @negative @security
  Scenario: Expired JWT token returns 401
    Given an expired JWT token
    When a workload ADD event is posted for trainer "john.doe" with 60 minutes on "2026-06-15"
    Then the response status should be 401
