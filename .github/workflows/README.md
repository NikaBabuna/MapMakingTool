<!--
  File: .github/workflows/README.md
  Purpose: GitHub Actions workflows index
  Audience: Agents and humans
  Update when: Workflows are added or removed
-->

# Workflows

| Workflow | Purpose |
|----------|---------|
| [ci.yml](ci.yml) | Temurin JDK 21 and Node 22, `npm ci` in `ui/web`, then `./mvnw -B test`, on `push` / `pull_request` to `main` |

Same Accept bar as local `mvnw test`. Details: [../../docs/architecture/program.md](../../docs/architecture/program.md).
