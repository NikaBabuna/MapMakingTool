<!--
  File: docs/architecture/conventions.md
  Purpose: Code conventions — names, where a file goes, file headers, and the folders that need no README
  Audience: Agents and humans
  Update when: A convention is added or changed, or a language, a kind of file, or an exempt folder enters or leaves the project
-->

# Code conventions

How the code of this project is written: the names, the place of each file, the file headers, and the folders that need no README. Every Step that writes code follows this page. When a Step brings in something this page does not cover — a new language, a new kind of file or folder, or a new pattern — that Step adds the convention here, so the agents after it follow it too.

## Languages

| Language | Where it lives | Base convention |
|----------|----------------|-----------------|
| Java | `engine/`, `product/`, `ui/`, `cli/`: each module's `src/main/java/` and `src/test/java/` | Classic Java naming, as the Java Language Specification describes it |
| TypeScript and React (`.ts`, `.tsx`, `.mts`) | `ui/web/src/`, and the web front's configuration files in `ui/web/` | The TypeScript and React community conventions, and the file names Next.js reserves |
| Rust | `ui/desktop/src-tauri/src/`, `ui/desktop/src-tauri/build.rs` | The naming conventions of the Rust API Guidelines |
| CSS | `ui/web/src/app/globals.css` | kebab-case, as CSS itself is written |
| HTML | `ui/desktop/public/` | The file names the desktop shell requires |
| XML: Maven build files | `pom.xml` at the root and in each module | Maven's build-file conventions |
| TOML: Cargo build file | `ui/desktop/src-tauri/Cargo.toml` | Cargo's manifest conventions |
| JSON: package and tool configuration | `ui/web/package.json`, `ui/web/tsconfig.json`, `ui/desktop/package.json`, `ui/desktop/src-tauri/tauri.conf.json`, `ui/desktop/src-tauri/capabilities/` | The file names each tool requires |
| YAML: CI workflow | `.github/workflows/` | The file layout GitHub Actions requires |
| Windows scripts (`.cmd`) | The repository root | `this page only` |
| Other configuration (`.gitignore`, `.env.example`) | The repository root, `ui/web/` | The file names each tool requires |

## Names

### Java

| Kind of name | Rule | Example |
|--------------|------|---------|
| Module folder | Lowercase, one word | `engine` |
| Package | `com.aethelgard.<module>[.<area>[.<chapter>]]`: lowercase, one word per segment, using the same word the architecture paper uses for that area, chapter or mechanism | `com.aethelgard.engine.pool` |
| Type (class, record, enum, interface) | UpperCamelCase noun or noun phrase. No `I` prefix on interfaces | `PlateRegistry`, `EventEmissionPolicy` |
| Type that carries out a process | Named after the process, as a noun, never with a verb | `Orogeny` |
| Acronym inside a name | Written as a word: its first letter capital and the rest lowercase, or all lowercase where it begins a member name | `CliRunner`, `plateId` |
| Method | lowerCamelCase verb or verb phrase | `emitPath` |
| Boolean query | Starts with `is`, `has` or `can`. A verb that already reads as a question (`contains`, `matches`) keeps its form. A record accessor is named after its component. A method that overrides or implements a library method keeps that method's name | `isEmpty`, `hasForeignNeighbor`, `contains` |
| Field, parameter, local variable | lowerCamelCase noun or noun phrase | `updateCount` |
| Constant (a `static final` field holding an immutable value) and enum constant | UPPER_SNAKE_CASE | `FIRST_GENERATION_UPDATE`, `COLLIDE` |
| Type parameter | One capital letter | `T` |
| File | The name of the one top-level type it declares, then `.java`. A package is documented by its README only: no package keeps a `package-info.java` | `PlateRegistry.java` |

### TypeScript and React

| Kind of name | Rule | Example |
|--------------|------|---------|
| Folder | Lowercase, one word | `components` |
| Component, and its file | PascalCase noun | `MapTool`, `MapTool.tsx` |
| Other module file | lowerCamelCase | `host.ts`, `fakeHost.ts` |
| Function | lowerCamelCase verb or verb phrase | `fetchStatus` |
| Variable, parameter | lowerCamelCase noun | `factor` |
| Type, interface | PascalCase noun, no `I` prefix | `HostStatus` |
| Module-level constant | UPPER_SNAKE_CASE | `DEFAULT_HOST` |
| Hook | `use`, then PascalCase | `useViewport` |
| Name Next.js reserves | Exactly as Next.js requires | `page.tsx`, `layout.tsx`, `metadata` |

### Rust

| Kind of name | Rule | Example |
|--------------|------|---------|
| Module, and its file | snake_case | `lib.rs` |
| Function | snake_case verb phrase | `start_map_host` |
| Variable, parameter | snake_case noun | `manifest` |
| Type (struct, enum, trait) | UpperCamelCase noun | `HostProcess` |
| Constant, static | SCREAMING_SNAKE_CASE | `CREATE_NO_WINDOW` |

### CSS

| Kind of name | Rule | Example |
|--------------|------|---------|
| Class name | kebab-case, beginning with the region it styles | `map-view`, `map-neatline` |
| Custom property | `--`, then kebab-case | `--accent-strong` |

### Windows scripts

| Kind of name | Rule | Example |
|--------------|------|---------|
| Script file | `run-<what it starts>.cmd`, lowercase kebab-case | `run-product.cmd` |

## Where a file goes

Each folder of code has one job, and a new file goes into the folder whose job it serves. A Java package holds one area, chapter or mechanism of the implementation paper and is named with the paper's word for it, so a page of the paper and its code folder share a name. A file that serves no existing folder's job starts a new folder, named by this page and given its README.

| Language | Folder | What goes there |
|----------|--------|-----------------|
| Java | `<module>/src/main/java/com/aethelgard/<module>/…` | One package per area, chapter or mechanism of the implementation paper, named with the paper's word for it. When a module is one area of the paper, the module's own package is that area |
| Java | `<module>/src/main/java/com/aethelgard/<module>/…/<package>/` | How the packages of one area depend on each other: the mechanism packages of an area are peers and may call each other; a package of values or geometry, such as `fields` or `topology`, calls no package above it; and the area's own package, when it wires the area's mechanisms together, is called only from outside the area |
| TypeScript and React | `ui/web/src/app/` | Next.js routes: the page, its layout, and the global styles |
| TypeScript and React | `ui/web/src/components/` | React components, one per file, each with its test beside it |
| TypeScript and React | `ui/web/src/lib/` | Logic that uses no React, one concern per file, each with its test beside it |
| TypeScript and React | `ui/web/src/test/` | Helpers shared by several tests: the stand-in host and the test setup |
| Rust | `ui/desktop/src-tauri/src/` | The desktop shell's code: `main.rs` starts it, and `lib.rs` holds it |
| JSON: package and tool configuration | `ui/desktop/src-tauri/capabilities/` | The desktop shell's permission sets, one file per set |
| HTML | `ui/desktop/public/` | The page the desktop shell bundles, which forwards to the web front |
| Windows scripts (`.cmd`) | The repository root | Launch scripts, one per thing a person starts |
| XML: Maven build files | The repository root, and each module's root | One build file per module, and the parent build file |

**Folder limit:** a folder holds at most 12 source files, not counting its README or test files kept beside the code. A folder over the limit, or a folder that holds two jobs, is split by job.

## Tests

Where test files live and how a test file is named: the **Tests** line of [program.md](program.md).

| Language | Rule | Example |
|----------|------|---------|
| Java | A test method is a lowerCamelCase sentence stating the outcome it proves, with an `@DisplayName` that states the same outcome in plain words | `everyContactIsFoundOnceAndClassified` |
| TypeScript and React | An `it` title states the outcome it proves in plain words. A `describe` title names the situation the tests share | `it("Space plays and pauses")` |

## File headers

Every source file begins with the four lines of the source header, in its language's comment form.

| Language or kind of file | Form | First line |
|--------------------------|------|------------|
| Java | Block comment: `/*`, then ` * <line>` for each header line, then ` */` | `/*` |
| TypeScript and React | Block comment, as for Java | `/*` |
| Rust | Block comment, as for Java | `/*` |
| CSS | Block comment, as for Java | `/*` |
| HTML | `<!--`, then each header line indented by two spaces, then `-->`, directly after `<!DOCTYPE html>` | `<!--` |
| XML: Maven build files | As for HTML, directly after the XML declaration | `<!--` |
| TOML, YAML, and other configuration (`.gitignore`, `.env.example`) | `# <line>` for each header line | `# File: <path from the repository root>` |
| Windows scripts (`.cmd`) | `REM <line>` for each header line, directly after `@echo off` | `REM File: <path from the repository root>` |
| JSON, and lock files | none — JSON allows no comments, and lock files are written by their package manager | — |
| Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`), and the stored test world | none — written by their tools and replaced, never edited by hand | — |

A Purpose line states the file's one responsibility in one sentence. It names no Goal, Step or decision id, and tells no history: not what changed, and not which Step added it. In code, a record id appears in one place only: the comment beside a test that names the requirements it proves, in the form the **Tests** line of [program.md](program.md) gives.

## Exempt folders

These folders need no README.

| Folder | Kind | Reason |
|--------|------|--------|
| `engine/target/`, `product/target/`, `ui/target/`, `cli/target/`, `ui/desktop/src-tauri/target/` | build output | Maven and Cargo write them on every build; nobody reads them |
| `ui/web/.next/` | build output | Next.js writes it on every build |
| `ui/desktop/src-tauri/gen/` | generated | Tauri writes its schemas there on every build |
| `ui/web/node_modules/`, `ui/desktop/node_modules/` | dependency install | Installed packages, not this project's code |
| `.mvn/wrapper/` | wrapper internals | The Maven Wrapper's own files, replaced by the wrapper |
| `.tools/` | tool cache | A local Maven install that is not part of the repository |
| `<module>/src/`, `<module>/src/main/`, `<module>/src/main/java/`, `<module>/src/test/`, `<module>/src/test/java/`, `<module>/src/test/resources/` | layout segment | Maven's layout; the module's README introduces what lies below |
| `<module>/src/main/java/com/`, `<module>/src/main/java/com/aethelgard/`, and the same two under `src/test/java/` | layout segment | Java namespace segments above each module's own package |
| `<module>/src/main/java/com/aethelgard/<module>/`, and the same under `src/test/java/`, when it holds only folders | layout segment | The module's own namespace level; the module's README introduces the packages below it |
| `ui/web/src/` | layout segment | The layout Next.js expects; the web front's README introduces its folders |
| `ui/desktop/src-tauri/icons/`, and every folder below it | generated | The Tauri icon tool writes them all from one source image |
| `product/src/test/resources/worlds/` | generated | The stored dump of a world, written from the program's own output |
