@component @trainer @trainings
Feature: Trainer Trainings List
  As a trainer I want to view my trainings with optional filters

  Background:
    Given a registered trainee with first name "John" and last name "Doe"
    And a registered trainer with first name "Mike" and last name "Johnson" and specialization id 1
    And the trainer is assigned to the trainee
    And the trainer is authenticated

  @positive
  Scenario: Get trainer trainings filtered by date range
    Given a training "Morning Cardio" on "2026-07-10" for 60 minutes exists
    When the trainer requests their trainings from "2026-07-01" to "2026-07-31"
    Then the response status should be 200
    And the response should be a non-empty list

  @edge-case
  Scenario: Get trainer trainings with no match returns empty list
    When the trainer requests their trainings from "2030-01-01" to "2030-12-31"
    Then the response status should be 200
    And the response should be an empty list
