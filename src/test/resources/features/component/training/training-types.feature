@component @training-types
Feature: Training Types
  As an authenticated user I want to list all available training types

  @positive
  Scenario: List all training types
    Given a registered trainee and the user is authenticated
    When the user requests all training types
    Then the response status should be 200
    And the response should be a non-empty list
