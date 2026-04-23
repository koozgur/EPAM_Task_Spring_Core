# Integration Tests

This module contains BDD integration tests (Cucumber + Testcontainers) that exercise the full `gym-crm` ↔ `trainer-workload-service` flow.

## Prerequisites

This is a **standalone Maven module** — the root project is **not** a multi-module aggregator, so the SNAPSHOT dependencies must be installed in your local Maven repository before running these tests.

### 1. Build and install `gym-crm`

```bash
# From the repository root
mvn -pl . install -DskipTests
```

This produces both the executable fat-jar and the plain jar (classifier `plain`) that is used as a dependency here.

### 2. Build and install `trainer-workload-service`

```bash
# From the repository root
mvn -pl trainer-workload-service install -DskipTests
```

### 3. Run the integration tests

```bash
cd integration-tests
mvn verify
```

Testcontainers will spin up the required infrastructure (PostgreSQL, MongoDB, ActiveMQ) automatically; no external services need to be running.

> **Tip:** You can run the two install steps together in a single command from the repository root:
> ```bash
> mvn install -DskipTests && cd integration-tests && mvn verify
> ```
