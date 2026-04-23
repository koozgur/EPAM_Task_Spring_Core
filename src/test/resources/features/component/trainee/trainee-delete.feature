@component @trainee @delete
Feature: Trainee Deletion
  As a registered trainee I want to delete my account

  @positive
  Scenario: Delete trainee and verify removal
    Given a registered trainee with first name "John" and last name "Doe"
    And the user is authenticated
    When the user deletes the trainee
    Then the response status should be 200
    When a new user verifies the deleted trainee profile
    Then the response status should be 404

  @negative
  Scenario: Delete non-existent trainee returns 404
    Given a registered trainee with first name "Jane" and last name "Smith"
    And the user is authenticated
    When the user deletes trainee with username "non.existent"
    Then the response status should be 404
