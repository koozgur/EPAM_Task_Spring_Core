@integration @end-to-end
Feature: Training Creation Propagates to Workload Service
  As the system operator I want training events in the main service to
  update the trainer workload in the workload service, so that both stores
  stay in sync through asynchronous JMS messaging.

  Background:
    Given both microservices are running
    And a trainee and trainer are registered in the main service
    And the user is authenticated in the main service

  @positive
  Scenario: Creating a training updates the trainer workload
    When the user adds a training for 60 minutes on "2026-06-15" via the main service
    Then within 10 seconds the workload service shows 60 minutes for that trainer in June 2026

  @positive
  Scenario: Multiple trainings accumulate in workload
    When the user adds a training for 60 minutes on "2026-07-10" via the main service
    And the user adds a training for 30 minutes on "2026-07-20" via the main service
    Then within 10 seconds the workload service shows 90 minutes for that trainer in July 2026

  @positive
  Scenario: Deleting a trainee propagates workload removal
    Given a training for 45 minutes on "2026-06-20" exists in the main service
    And within 10 seconds the workload service shows 45 minutes for that trainer in June 2026
    When the trainee is deleted in the main service
    Then within 10 seconds the workload service shows 0 minutes for that trainer in June 2026
