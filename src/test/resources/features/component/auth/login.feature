@component @auth @login
Feature: User Login
  As a registered user I want to authenticate so I receive a JWT token

  Background:
    Given a registered trainee with first name "John" and last name "Doe"

  @positive
  Scenario: Successful login with valid credentials
    When the user logs in with the registered credentials
    Then the response status should be 200
    And the response should contain a non-empty "token" field
    And the response should contain the username

  @negative
  Scenario: Login with wrong password returns 401
    When the user logs in with password "wrongPassword"
    Then the response status should be 401

  @negative
  Scenario: Login with non-existent username returns 401
    When the user logs in with username "ghost" and password "any"
    Then the response status should be 401
