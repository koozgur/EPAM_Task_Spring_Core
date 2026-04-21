@component @trainee @activation
Feature: Trainee Activation/Deactivation

  Background:
    Given a registered trainee and the user is authenticated

  @positive
  Scenario: Deactivate an active trainee
    When the user deactivates the trainee
    Then the response status should be 200
    When the user requests the trainee profile
    Then the response should contain field "isActive" with value "false"

  @positive
  Scenario: Activate an inactive trainee
    Given the trainee has been deactivated
    When the user activates the trainee
    Then the response status should be 200
    When the user requests the trainee profile
    Then the response should contain field "isActive" with value "true"

  @negative @edge-case
  Scenario: Activating already-active trainee returns 409
    When the user activates the trainee
    Then the response status should be 409

  @negative @edge-case
  Scenario: Deactivating already-inactive trainee returns 409
    Given the trainee has been deactivated
    When the user deactivates the trainee
    Then the response status should be 409
