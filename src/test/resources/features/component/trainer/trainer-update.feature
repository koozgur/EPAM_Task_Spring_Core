@component @trainer @update
Feature: Trainer Profile Update

  Background:
    Given a registered trainer with first name "Mike" and last name "Johnson" and specialization id 1
    And the trainer is authenticated

  @positive
  Scenario: Update trainer profile
    When the trainer updates their profile with first name "Michael", last name "J", active true
    Then the response status should be 200
    And the response should contain field "firstName" with value "Michael"
    And the response should contain field "lastName" with value "J"
