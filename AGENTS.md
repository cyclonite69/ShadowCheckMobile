# Repository Guidelines

## Project Structure & Module Organization
This is a multi-module Android project. Key paths:
- `app/` for the Android application module (Compose UI, Android entry points).
- `core/`, `domain/`, `data/` for shared, clean-architecture layers.
- `app/src/main/` for production code, resources, and assets.
- `app/src/test/` for unit tests.
- `docs/` for development and build guides.

Package by feature (not by layer), e.g. `com.shadowcheck.mobile.wifi/` with `data/`, `domain/`, `presentation/` subpackages.

## Build, Test, and Development Commands
- `./gradlew clean build` — full build with checks.
- `./gradlew assembleDebug` — build debug APK.
- `./gradlew installDebug` — install on a connected device.
- `./gradlew test` — run unit tests.
- `./gradlew detekt` — run static analysis (must be clean before PR).

## Coding Style & Naming Conventions
- Kotlin conventions, 4-space indentation, max line length 120.
- Use meaningful names; avoid decompilation artifacts like `var1`.
- Naming patterns:
  - ViewModels: `WifiViewModel.kt`
  - Use cases: `GetAllWifiNetworksUseCase.kt`
  - Repositories: `WifiNetworkRepository.kt` / `WifiNetworkRepositoryImpl.kt`
- DI via Hilt with KAPT (do not migrate to KSP without explicit approval).

## Testing Guidelines
- Frameworks: JUnit4, MockK, coroutines test.
- Coverage targets (new code): Use cases 100%, ViewModels 90%+, Repos 80%+.
- Naming: `*Test.kt` in `app/src/test/` (e.g., `GetAllWifiNetworksUseCaseTest`).

## Commit & Pull Request Guidelines
- Conventional commits: `type(scope): subject` (e.g., `feat(wifi): Add WPA3 detection`).
- PRs must include: clear description, tests run, screenshots for UI changes, and linked issues.
- Run `./gradlew detekt` and `./gradlew test` before opening a PR.
- Address review feedback in new commits (do not force-push).

## Security & Configuration Tips
- Copy `local.properties.example` to `local.properties` and add API keys locally.
- Never commit secrets; use `SecureApiKeyManager` for API key handling.
