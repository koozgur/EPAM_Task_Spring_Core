@integration @security @cross-service
Feature: JWT Authentication Works Across Services
  Both services sign and validate JWTs with the same secret; a token minted
  by the main service at registration time must authenticate against the
  workload service, and invalid/missing tokens must be rejected.

  Background:
    Given both microservices are running

  @positive
  Scenario: JWT from main service is accepted by workload service
    Given a trainee is registered in the main service and receives a JWT
    When the user calls the workload service summary endpoint with that JWT
    Then the response status should be 200

  @negative
  Scenario: Invalid JWT is rejected by workload service
    When the user calls the workload service summary endpoint with an invalid JWT
    Then the response status should be 401

  @negative
  Scenario: No JWT provided to workload service returns 401
    When the user calls the workload service summary endpoint without a JWT
    Then the response status should be 401
