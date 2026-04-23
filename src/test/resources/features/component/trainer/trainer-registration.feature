@component @trainer @registration
Feature: Trainer Registration

  @positive
  Scenario: Register with valid data
    When a trainer registers with first name "Mike" and last name "Johnson" and specialization id 1
    Then the response status should be 201
    And the response should contain a generated username and password and token

  @negative @validation
  Scenario: Blank first name returns 400
    When a trainer registers with first name "" and last name "Johnson" and specialization id 1
    Then the response status should be 400
    And the response should contain field error for "firstName"

  @negative
  Scenario: Unknown specialization id returns 404
    When a trainer registers with first name "Mike" and last name "Johnson" and specialization id 999
    Then the response status should be 404
