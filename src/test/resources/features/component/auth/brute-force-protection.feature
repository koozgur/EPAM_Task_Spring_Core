@component @auth @brute-force @security
Feature: Brute-force Login Protection

  Background:
    Given a registered trainee with first name "Jane" and last name "Smith"

  @negative @edge-case
  Scenario: Account locked after 3 failed attempts
    When the user fails login 3 times with wrong password
    And the user attempts login with the correct credentials
    Then the response status should be 429

  @positive
  Scenario: Successful login resets failure counter
    When the user fails login 2 times with wrong password
    And the user logs in with the registered credentials
    Then the response status should be 200
