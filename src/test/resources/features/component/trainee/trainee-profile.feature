@component @trainee @profile
Feature: Trainee Profile
  As a registered trainee I want to view my profile

  Background:
    Given a registered trainee with first name "John", last name "Doe", DOB "1995-06-15", address "123 Main St"
    And the user is authenticated

  @positive
  Scenario: Get trainee profile
    When the user requests the trainee profile
    Then the response status should be 200
    And the response should contain field "firstName" with value "John"
    And the response should contain field "lastName" with value "Doe"
    And the response should contain field "isActive" with value "true"

  @negative
  Scenario: Get non-existent trainee profile returns 404
    When the user requests profile for username "non.existent"
    Then the response status should be 404
