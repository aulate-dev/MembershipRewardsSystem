# MembershipRewardsSystem

A minimal Java Maven project for membership rewards **business logic** with **unit tests** (TestNG). No UI, no database.

## Prerequisites

- **JDK 17** (or 21) installed and `JAVA_HOME` set

You do **not** need Maven installed globally: this project includes the **Maven Wrapper** (`mvnw` / `mvnw.cmd`). The first time you run the wrapper, it will download Maven automatically.

## Run tests

**Windows (PowerShell or cmd):**

```bash
.\mvnw test
```

**Verify wrapper and Maven version:**

```bash
.\mvnw -version
```

## Troubleshooting

- **JAVA_HOME not set:** Set it to your JDK install path, e.g. `C:\Program Files\Java\jdk-25`, then restart the terminal.
- Prefer **`.\mvnw test`** as the primary command (no global Maven required).

## Project scope

- **No UI** — command-line / library only.
- **No database** — pure in-memory business logic.
- **Unit tests** — TestNG + Mockito; run with `.\mvnw test`.

Structure: `src/main/java` for production code, `src/test/java` for tests.
