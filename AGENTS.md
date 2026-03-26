# AGENTS Guide for `MyApplication`

## Project snapshot
- Single-module Android app (`:app`) configured in `settings.gradle`; no feature modules yet.
- Java-only app code (Java 11) under `app/src/main/java/com/example/myapplication/`.
- Build uses Gradle Version Catalog (`gradle/libs.versions.toml`) and AGP plugin aliasing from root `build.gradle`.

## Architecture and code flow
- Entry point is `MainActivity` (`app/src/main/java/com/example/myapplication/MainActivity.java`) declared as launcher in `app/src/main/AndroidManifest.xml`.
- UI is XML-based (`app/src/main/res/layout/activity_main.xml`) with a `ConstraintLayout` root id `@id/main`.
- `MainActivity.onCreate()` enables edge-to-edge and applies system bar insets to `R.id.main`; keep that id if replacing the root view.
- Current app behavior is minimal: inflate `activity_main`, show centered `TextView` (currently hardcoded `"Hello World!"`).
- Theme chain is `Theme.MyApplication -> Base.Theme.MyApplication -> Theme.Material3.DayNight.NoActionBar` in `app/src/main/res/values/themes.xml`.

## Build and test workflows (Windows / PowerShell)
- Use wrapper from project root:
  - `./gradlew.bat :app:assembleDebug`
  - `./gradlew.bat :app:testDebugUnitTest`
  - `./gradlew.bat :app:connectedDebugAndroidTest` (requires emulator/device)
- If dependency resolution fails, check repo policy in `settings.gradle`: `repositoriesMode.set(FAIL_ON_PROJECT_REPOS)` blocks module-local repos.
- Gradle distribution is pinned in `gradle/wrapper/gradle-wrapper.properties` (Gradle 9.3.1).

## Project conventions agents should follow
- Keep package/namespace/applicationId aligned as `com.example.myapplication` (`app/build.gradle`, tests, manifest).
- Add dependencies through `gradle/libs.versions.toml` and reference via `libs.*` in `app/build.gradle`.
- Prefer updating XML resources for UI text and theme values under `app/src/main/res/values/`.
- Preserve launcher manifest wiring unless intentionally changing app entry behavior.
- Keep compatibility with `minSdk 29`, `targetSdk 36`, `compileSdk 36.1` (`app/build.gradle`).

## Testing and verification patterns in this repo
- Local JVM tests live in `app/src/test/...` (`ExampleUnitTest.java`, JUnit4).
- Instrumented tests live in `app/src/androidTest/...` (`ExampleInstrumentedTest.java`, AndroidJUnit4 + InstrumentationRegistry).
- Instrumented package assertion expects `com.example.myapplication`; update test if applicationId changes.

## Integration boundaries and dependencies
- External UI/runtime deps: `androidx.appcompat`, `material`, `androidx.activity`, `constraintlayout` (see catalog).
- Test deps: `junit:junit`, `androidx.test.ext:junit`, `androidx.test.espresso:espresso-core`.
- No network/database/service layer exists yet; all logic currently sits at Activity + XML resource level.
