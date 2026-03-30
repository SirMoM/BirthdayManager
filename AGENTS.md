# Repository Guidelines

## Project Structure & Module Organization
`BirthdayManager` is a single-module Gradle project.
Application code lives in `src/main/java/application`, split into `controller`, `model`, `processes`, and `util` packages.
JavaFX views and other runtime assets live in `src/main/resources`, with FXML files under `src/main/resources/application/view`, translations in `lang_*.properties`, CSS files beside them, and images in `src/main/resources/img`.
Tests mirror the main package layout under `src/test/java`.
Sample test data lives in `src/test/resources`.
CI and CD workflows are in `.github/workflows`, and screenshots or other project media belong in `misc/`.

## Build, Test, and Development Commands
Prefer the locally installed `gradle` command when it is available.
Use the wrapper only when a local Gradle installation is unavailable
or an environment explicitly requires it.

```bash
gradle run
gradle test
gradle build
gradle googleJavaFormat
gradle shadowJar
```

`run` starts the JavaFX app through `application.Launcher`.
`test` runs the JUnit 5 suite.
`build` compiles, tests, and assembles the project.
`googleJavaFormat` applies the repository formatter.
`shadowJar` builds a bundled jar for distribution.

## Coding Style & Naming Conventions
Follow standard Java style with 4-space indentation and one top-level class per file.
Packages stay lowercase.
Classes use PascalCase, constants use `UPPER_SNAKE_CASE`, and JavaFX controllers and background jobs use descriptive suffixes such as `*Controller` and `*Task`.
Keep resource names stable and descriptive, for example `BirthdaysOverview.fxml` or `lang_en_GB.properties`.
Format Java code with `google-java-format` before submitting changes.

## Testing Guidelines
Tests use JUnit Jupiter and AssertJ.
Add new tests in the matching package under `src/test/java`, and name files `*Test.java`.
Prefer focused method names that describe the behavior under test, for example `parseFromCSVLine_SuccessTest`.
Cover model parsing, task behavior, and any controller-adjacent logic that can be exercised without UI automation.

## Commit & Pull Request Guidelines
Recent history favors short, descriptive commit subjects such as `Adds localization for the about button` and release commits like `Version 0.4.5`.
Before proposing a commit message, inspect recent `git log --oneline` entries and mirror the existing repository style.
Keep commits single-purpose and write the subject as a concise summary of the visible change.
Do not use semantic or conventional commit prefixes such as `feat:`, `fix:`, or `chore:`.
Match the existing repository style with plain descriptive subjects instead.
Pull requests should explain the user-facing impact, note any affected views or resources, and include screenshots when changing FXML, CSS, or other UI assets.
Link related issues when available and mention the verification command you ran, usually `gradle test`.

## Security & Configuration Tips
Do not commit personal data from the local `.gbm` directory or any publish credentials.
Publishing uses GitHub Packages credentials from Gradle properties or environment variables such as `GITHUB_TOKEN`.
