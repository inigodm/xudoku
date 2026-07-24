# Copilot Instructions for xudoku

## Build & Test

This is a single-module Android Gradle project. Use the Gradle wrapper (`./gradlew`).

```bash
# Build
./gradlew assembleDebug

# Unit tests (JVM)
./gradlew test
# Single test class
./gradlew test --tests "com.inigo.xudoku.ExampleUnitTest"

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

## Architecture

- **Single-activity Compose app** — `MainActivity` uses `setContent` with a `XudokuTheme` wrapper and Material 3 `Scaffold`.
- **Package**: `com.inigo.xudoku` — source lives under `app/src/main/java/com/inigo/xudoku/`.
- **Theme**: Custom `XudokuTheme` in `ui/theme/` with Material 3 dynamic color support (Android 12+), falling back to static light/dark schemes.
- **Dependencies managed via version catalog** at `gradle/libs.versions.toml`.

## Conventions

- Kotlin with Jetpack Compose — no XML layouts. Use `@Composable` functions for all UI.
- Material 3 (`androidx.compose.material3`) — not Material 2.
- Edge-to-edge display is enabled (`enableEdgeToEdge()`); handle insets via `Scaffold` inner padding.
- Target SDK 36, min SDK 24, Java 11 source compatibility.
- Kotlin code style: `official` (configured in `gradle.properties`).
