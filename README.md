# FlipCoin

An Android app: tap to flip a coin, watch it spin, and learn a fun fact about a topic you picked during onboarding.

## Project structure

The app follows Google's [recommended app architecture](https://developer.android.com/topic/architecture) and [modularization guide](https://developer.android.com/topic/modularization), in the style of the [Now in Android](https://github.com/android/nowinandroid) sample.

| Module | What it holds | Depends on |
| --- | --- | --- |
| `app` | Application class, `MainActivity`, navigation, DI wiring | everything |
| `core:model` | Plain Kotlin data types (`CoinSide`, `Topic`, `Fact`) | nothing |
| `core:domain` | Use cases and repository interfaces, pure Kotlin | `core:model` |
| `core:designsystem` | Compose theme and shared UI components | Compose |
| `feature:*` (coming) | One module per screen: onboarding, flip | `core:*` only |
| `build-logic` | Gradle convention plugins shared by all modules | |

Rules that keep it modular:
- Features never depend on other features; `app` connects them.
- `core:domain` defines interfaces (for example `RandomSource`) and other layers implement them, so business logic stays testable without Android.
- Every module gets its build setup from a convention plugin (`flipcoin.android.feature`, `flipcoin.jvm.library`, ...), so a new module needs only a few lines of Gradle.

## Building

Requires JDK 17 and the Android SDK (Android Studio sets both up).

```bash
./gradlew assembleDebug     # build the debug APK
./gradlew test              # unit tests
./gradlew ktlintCheck lint  # code style and Android lint
```

CI runs the same commands on every pull request, then installs the app on an emulator and walks through it. See [docs/TESTING.md](docs/TESTING.md) for testing on a phone or a laptop emulator.
