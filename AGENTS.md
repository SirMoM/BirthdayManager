# AGENTS.md
This file is for agentic coding assistants working in this repository.
It captures practical build/test commands and project-specific coding rules.

## 1) Project Snapshot
- Project: `BirthdayManager`
- Build system: Gradle (single module)
- Language/runtime: Java 21 toolchain
- UI stack: JavaFX (`controls`, `fxml`, `graphics`, `swing`)
- Logging: Log4j2
- Tests: JUnit Jupiter + AssertJ

## 2) Repository Layout
- Main code: `src/main/java/application`
- Key packages:
  - `application.controller`
  - `application.model`
  - `application.processes`
  - `application.util`
- UI resources: `src/main/resources`
  - FXML: `src/main/resources/application/view`
  - i18n bundles: `lang_*.properties`
  - CSS: resource root (e.g. `dark-mode.css`)
  - Images: `src/main/resources/img`
- Tests: `src/test/java` (mirrors main package layout)
- Test data: `src/test/resources`
- CI/CD workflows: `.github/workflows`

## 3) Build / Run / Test Commands
Prefer local `gradle` over `./gradlew` in this repo.

### Core commands
- Run app locally: `gradle run`
- Run full test suite: `gradle test`
- Build (compile + test + assemble): `gradle build`
- Build shaded distribution jar: `gradle shadowJar`
- Format Java sources: `gradle spotlessApply`

### Single-test commands (important)
- Run one test class:
  - `gradle test --tests "application.model.PersonTest"`
- Run one test method:
  - `gradle test --tests "application.model.PersonTest.parseFromCSVLine_SuccessTest"`
- Run test classes by wildcard:
  - `gradle test --tests "application.processes.*Test"`

### Useful verification commands
- List available tasks: `gradle -q tasks --all`
- Re-run tests even when up-to-date: `gradle test --rerun-tasks`

### Notes on lint/format
- No Checkstyle/PMD/SpotBugs tasks are configured.
- Formatting is handled by the `Spotless` Gradle plugin using `google-java-format`.
- `spotlessCheck` participates in the normal Gradle `check` lifecycle.

## 4) Coding Style Guidelines
These rules are based on existing code and build configuration.

### Formatting and structure
- Use 4-space indentation.
- Keep one top-level class per file.
- Keep lines/spacing consistent with google-java-format output.
- Run `gradle spotlessApply` after Java edits.
- Keep package declarations lowercase (e.g. `application.processes`).

### Imports
- Prefer explicit imports over wildcard imports in new/edited code.
- Existing files already use wildcard imports; avoid unrelated import-only refactors.
- Keep static imports mainly for test assertions/helpers.
- Remove unused imports.

### Naming conventions
- Classes/interfaces: PascalCase (`MainController`, `LoadPersonsTask`).
- Methods/fields/locals: camelCase.
- Constants: `UPPER_SNAKE_CASE`.
- Controllers should end with `Controller`.
- Background jobs should end with `Task` (or clearly process-oriented names).
- Tests should end with `Test` and mirror production package structure.

### Types and API shape
- Prefer concrete, readable types in public method signatures.
- Use `final` for parameters/locals when it improves clarity and matches nearby style.
- Keep visibility narrow (`private` by default).
- Preserve JavaFX property-based patterns in model classes where already used.
- Do not introduce new frameworks/annotation processors without explicit need.

### Nullability and state handling
- Be explicit about nullable values in control flow.
- Use guard clauses before side effects (e.g. file chooser cancellation returning `null`).
- In new code, prefer direct null checks over catching `NullPointerException` for normal flow.
- Preserve behavior compatibility when touching older code that catches NPE.

### Error handling and logging
- Prefer typed exceptions over broad `catch (Exception)` unless required at boundaries.
- Log failures with context (`file`, `operation`, `user action`) before showing alerts.
- Keep UI-facing error alerts concise and actionable.
- Use existing Log4j2 logger pattern (`private static final Logger LOG = ...`).
- Do not swallow exceptions silently.

### JavaFX and UI code
- Keep controllers focused on view wiring and user interaction handling.
- Move business logic into `model`/`processes` where practical.
- Keep background work off the FX Application Thread.
- Update UI state on the correct thread boundary.
- Keep resource path references stable (FXML/CSS/image keys).

### Internationalization
- Reuse `LangResourceManager` and `LangResourceKeys` for user-facing strings.
- Add new text consistently to `lang_*.properties` bundles.
- Avoid hardcoded UI strings where localization infrastructure already exists.

## 5) Testing Guidelines
- Frameworks: JUnit Jupiter + AssertJ.
- Place tests under matching package paths in `src/test/java`.
- Favor descriptive names indicating behavior and expected outcome.
- Keep tests deterministic and independent of local machine state.
- Prefer focused unit tests for parsing/comparison/task behavior.
- For controller-adjacent behavior, test extracted pure/static logic when possible.

## 6) Change Scope and Refactoring Discipline
- Make minimal, task-focused changes.
- Do not perform broad stylistic rewrites unless requested.
- If touching legacy code, improve local readability/safety without unexpected behavior changes.
- Keep commits single-purpose when commits are requested.

## 7) Commit and PR Conventions
- Follow existing history style: short, descriptive commit subjects.
- Example recent style:
  - `Strip UTF-8 BOM before loading birthdays`
  - `Prevents saving birthdays without a selected file.`
- Do not force conventional commit prefixes unless explicitly requested.
- PR descriptions should include:
  - user-visible impact
  - affected views/resources
  - screenshots for FXML/CSS/UI changes
  - verification commands run (typically `gradle test`)

## 8) Security and Local Data
- Do not commit personal data under `.gbm`.
- Do not commit publishing credentials or tokens.
- Publishing uses Gradle properties and/or env vars like `GITHUB_TOKEN`.

## 9) Deploy / Release Notes
- Release preparation is not just `gradle release`.
- The app updater reads the latest public version from `origin/releases/master:gradle.properties` via `CheckForUpdatesTask`.
- The website repo lives next to this repo at `../BirthdayManagerWebsite`.

### Version bump and website sync
- Bump `gradle.properties` to the target release version.
- Keep the static fallback in `src/main/resources/application/view/AboutView.fxml` aligned with the same version.
- Run `gradle build`.
- Run `gradle javadoc`.
- `gradle javadoc` automatically syncs generated docs to `../BirthdayManagerWebsite/javaDocs`.
- Verify that `syncWebsiteJavadocs` reported a successful sync or that `../BirthdayManagerWebsite` contains the expected `javaDocs` updates.
- Update `../BirthdayManagerWebsite/index.html` for the new release text, changelog entry, and download label, then commit/push the website repo separately.

### Clean git state before release
- The Gradle Release Plugin is configured with `failOnCommitNeeded`, `failOnPublishNeeded`, `failOnUnversionedFiles`, and `failOnUpdateNeeded`.
- Push `master` before running `gradle release`, otherwise the plugin aborts.
- Stash local helper files like `AGENTS.md` and `TASKS.md` if they are untracked, then restore them after the release.

### Release command
- Use an explicit non-interactive command:
  - `gradle release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=<version> -Prelease.newVersion=<version>`
- In this repo, both values are intentionally the same because `commitNewVersion` is disabled and no follow-up version-bump commit is created.
- The current plugin setup tags the release correctly, but do not assume it updates `releases/master` for the updater branch.

### Branches and verification after release
- After the tag exists, make sure these refs all point to the released commit:
  - `origin/master`
  - `origin/releases/master`
  - `origin/releases/<version>`
  - tag `<version>`
- If needed, push them manually:
  - `git push origin master:releases/master HEAD:refs/heads/releases/<version>`
- Verify:
  - `git show origin/releases/master:gradle.properties`
  - `git show origin/releases/<version>:gradle.properties`
  - `git ls-remote --tags origin <version>`
  - `gh run list --workflow CD_Gradle.yml --limit 5`

### CD caveat
- `releases/<version>` is the important publish run for a new package version.
- A second `CD` run on `releases/master` may fail with HTTP `409 Conflict` from GitHub Packages if the same version was already published by `releases/<version>`.
- That conflict is expected duplicate-publish behavior, not automatically a broken release, as long as the package was already published and `releases/master` points to the correct version.

## 10) Cursor / Copilot Rules Check
No additional agent rules were found in:
- `.cursor/rules/`
- `.cursorrules`
- `.github/copilot-instructions.md`
If any of those files are added later, treat them as authoritative supplements.
