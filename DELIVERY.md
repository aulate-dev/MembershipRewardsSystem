# Delivery / Submission Instructions

## What to include in the zip

- `pom.xml`
- `mvnw`, `mvnw.cmd`, `.mvn/wrapper/*`
- `src/main/java/**`
- `src/test/java/**`
- `README.md`
- `docs/TestPlan.md`

## What to exclude

- `target/`
- `.idea/` (if present)
- `.vscode/` (if present)
- `.m2/` (never include)

---

## How to run tests (Windows)

From the project root:

```powershell
.\mvnw test
```

---

## Generate the zip (PowerShell)

Run from the **project root** (folder that contains `pom.xml`):

```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
Compress-Archive -Path .\pom.xml, .\mvnw, .\mvnw.cmd, .\.mvn, .\src, .\README.md, .\docs -DestinationPath .\MembershipRewardsSystem.zip -Force
```

This creates `MembershipRewardsSystem.zip` in the project root with the required files and no `target` folder.
