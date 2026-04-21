@component @trainer @profile
Feature: Trainer Profile
  As a registered trainer I want to view my profile

  Background:
    Given a registered trainer with first name "Mike" and last name "Johnson" and specialization id 1
    And the trainer is authenticated

  @positive
  Scenario: Get trainer profile
    When the trainer requests their profile
    Then the response status should be 200
    And the response should contain field "firstName" with value "Mike"
    And the response should contain field "lastName" with value "Johnson"
    And the response should contain field "isActive" with value "true"

  @negative
  Scenario: Get non-existent trainer profile returns 404
    When the trainer requests profile for username "non.existent"
    Then the response status should be 404
