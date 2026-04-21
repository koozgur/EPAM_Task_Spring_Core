@component @trainer @activation
Feature: Trainer Activation/Deactivation

  Background:
    Given a registered trainer with first name "Mike" and last name "Johnson" and specialization id 1
    And the trainer is authenticated

  @positive
  Scenario: Deactivate an active trainer
    When the trainer deactivates their account
    Then the response status should be 200
    When the trainer requests their profile
    Then the response should contain field "isActive" with value "false"

  @negative @edge-case
  Scenario: Deactivating already-inactive trainer returns 409
    Given the trainer has been deactivated
    When the trainer deactivates their account
    Then the response status should be 409
