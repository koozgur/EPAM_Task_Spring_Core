@component @trainee @registration
Feature: Trainee Registration

  @positive
  Scenario: Register with all fields
    When a trainee registers with first name "John", last name "Doe", DOB "1995-06-15", address "123 Main St"
    Then the response status should be 201
    And the response should contain a generated username and password and token

  @positive
  Scenario: Register with only required fields
    When a trainee registers with first name "Jane" and last name "Smith"
    Then the response status should be 201
    And the response should contain a generated username starting with "Jane.Smith"

  @negative @validation
  Scenario: Blank first name returns 400
    When a trainee registers with first name "" and last name "Doe"
    Then the response status should be 400
    And the response should contain field error for "firstName"

  @negative @validation
  Scenario: Blank last name returns 400
    When a trainee registers with first name "John" and last name ""
    Then the response status should be 400
    And the response should contain field error for "lastName"

  @positive @edge-case
  Scenario: Duplicate name gets serial suffix
    When a trainee registers with first name "John" and last name "Doe"
    And another trainee registers with first name "John" and last name "Doe"
    Then the second username should end with "1"
