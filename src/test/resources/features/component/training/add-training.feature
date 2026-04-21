@component @training @add
Feature: Add Training

  Background:
    Given a registered trainee with first name "John" and last name "Doe"
    And a registered trainer with first name "Mike" and last name "Johnson" and specialization id 1
    And the trainer is assigned to the trainee
    And the user is authenticated

  @positive
  Scenario: Successfully add a training
    When the user adds a training with name "Morning Cardio", date "2026-03-15", and duration 60
    Then the response status should be 200

  @negative @validation
  Scenario: Add training with missing name returns 400
    When the user adds a training with name "", date "2026-03-15", and duration 60
    Then the response status should be 400

  @negative
  Scenario: Add training with non-existent trainee returns 404
    When the user adds a training with trainee "non.existent" and the registered trainer
    Then the response status should be 404

  @negative @validation
  Scenario: Add training with negative duration returns 400
    When the user adds a training with name "Cardio", date "2026-03-15", and duration -10
    Then the response status should be 400
