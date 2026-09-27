<!--
  File: .github/README.md
  Purpose: Door to the repository's GitHub configuration: the automation GitHub runs for this repository
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# GitHub

Holds what GitHub reads from this repository to automate it: today, the one CI workflow.

**Paper:** none — the CI job runs the witness command the [program](../docs/architecture/program.md) page states, and the paper does not describe GitHub's configuration · **Conventions:** [conventions.md](../docs/architecture/conventions.md)

## Why

GitHub looks for its configuration in this folder by name, so it cannot live anywhere else. Only files GitHub reads belong here; the build it runs is described by the root `pom.xml` and the program page, and scripts a person runs belong at the repository root.

## How it works

GitHub reads every workflow file in [workflows](workflows/README.md) and runs it on the events the file names. The CI workflow runs the witness command on every push and pull request to `main`.

**Start reading at:** `jobs` in [workflows/ci.yml](workflows/ci.yml).

## Depends on

- nothing in this repository

## Used by

- nothing in this repository — GitHub reads it

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [workflows/](workflows/README.md) | The workflows GitHub Actions runs | — |
