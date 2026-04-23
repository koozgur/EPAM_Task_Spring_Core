@component @trainee @trainers
Feature: Trainee Trainer Management
  As a trainee I want to manage my assigned trainers

  Background:
    Given a registered trainee with first name "John" and last name "Doe"
    And a registered trainer with first name "Mike" and last name "Johnson" and specialization id 1
    And the user is authenticated

  @positive
  Scenario: Get available trainers for a trainee
    When the user requests available trainers for the trainee
    Then the response status should be 200
    And the response should be a non-empty list

  @negative
  Scenario: Replace trainer list with non-existent trainer returns 404
    When the user updates the trainee trainer list with usernames "non.existent"
    Then the response status should be 404
