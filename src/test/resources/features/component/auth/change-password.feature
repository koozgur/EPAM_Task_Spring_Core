@component @auth @change-password
Feature: Change Password

  Background:
    Given a registered trainee with first name "John" and last name "Doe"

  @positive
  Scenario: Change password and re-login with the new password
    When the user changes password to "newPassword123"
    Then the response status should be 200
    When the user logs in with the registered credentials
    Then the response status should be 200

  @negative
  Scenario: Wrong old password returns 401
    When the user changes password with wrong old password to "newPassword123"
    Then the response status should be 401

  @negative @security
  Scenario: Unauthenticated change password returns 401
    When the user attempts to change password without a token
    Then the response status should be 401
