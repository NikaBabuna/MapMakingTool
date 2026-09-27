<!--
  File: .github/workflows/README.md
  Purpose: Door to the GitHub Actions workflows: the CI job that runs the witness command on main
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Workflows

Holds the GitHub Actions workflows: one CI job that runs the witness command on every push and pull request to `main`.

**Paper:** none — the job runs the witness command the [program](../../docs/architecture/program.md) page states, and the paper does not describe GitHub's configuration · **Conventions:** [conventions.md](../../docs/architecture/conventions.md)

## Why

GitHub Actions reads workflows only from this folder, one YAML file per workflow. The CI job holds no build logic of its own: it prepares the tools and runs the same command a person runs locally, so a green run on GitHub and a green run on a machine mean the same thing. What the command builds and tests belongs to the build files and to the [program](../../docs/architecture/program.md) page.

## How it works

`ci.yml` runs the job `test` on `ubuntu-latest` for a push or a pull request to `main`. It checks out the repository, sets up JDK 21 (Temurin, with the Maven cache) and Node 22 (with the npm cache of `ui/web/package-lock.json`), runs `npm ci` in `ui/web` so the web front's tests can run, makes `mvnw` executable, and runs `./mvnw -B test`.

**Start reading at:** `steps` in [ci.yml](ci.yml).

## Depends on

- [the UI module](../../ui/README.md) — its build runs the web front's tests, which need the packages `npm ci` installs in `ui/web`

## Used by

- nothing in this repository — GitHub runs it

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [ci.yml](ci.yml) | Runs the witness command on `main`: JDK 21 for the Java modules, Node for the web front's tests | — |
