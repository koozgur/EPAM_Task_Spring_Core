@component @trainee @update
Feature: Trainee Profile Update
  As a registered trainee I want to update my profile

  Background:
    Given a registered trainee with first name "John" and last name "Doe"
    And the user is authenticated

  @positive
  Scenario: Update trainee profile with all fields
    When the user updates the trainee profile with first name "Johnny", last name "Updated", DOB "1990-01-01", address "456 New St", active true
    Then the response status should be 200
    And the response should contain field "firstName" with value "Johnny"
    And the response should contain field "lastName" with value "Updated"

  @negative
  Scenario: Update non-existent trainee returns 404
    When the user updates profile for username "non.existent" with first name "X", last name "Y", active true
    Then the response status should be 404
