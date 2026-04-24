# Testing Guide

This file documents how to run all test profiles in the current project layout.

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker running (required for Testcontainers-based component/integration tests)

## Main Service (root module)

Run from repository root:

```bash
mvn clean test
```

Runs unit-style tests in the main service module.

## Main Service Component Tests

Run from repository root:

```bash
mvn test -P component-test
```

Runs Cucumber component tests for the main service.

## Workload Service (unit + component)

Run from `trainer-workload-service`:

```bash
mvn clean install -P all-tests
```

Runs workload unit/component tests and installs artifact to local Maven repo.

## Integration Tests (dedicated module)

Both service artifacts must be installed in the local Maven repo first:

```bash
mvn -f trainer-workload-service/pom.xml clean install -P all-tests
mvn -f pom.xml clean install
```

Then run from `integration-tests`:

```bash
mvn -f integration-tests/pom.xml clean verify
```

Runs Cucumber integration tests (main service + workload service together).

## Run Everything (recommended sequence)

From repository root, use this order so dependencies are fresh:

```powershell
mvn -f trainer-workload-service/pom.xml clean install -P all-tests
mvn -f pom.xml clean install
mvn -f pom.xml test -P component-test
mvn -f integration-tests/pom.xml clean verify
```

The `install` step for the main service is required so the `gym-crm:plain` artifact is available in the local Maven repo when the `integration-tests` module resolves its dependency.

## One-line Command (PowerShell)

```powershell
mvn -f trainer-workload-service/pom.xml clean install -P all-tests; mvn -f pom.xml clean install; mvn -f pom.xml test -P component-test; mvn -f integration-tests/pom.xml clean verify
```
