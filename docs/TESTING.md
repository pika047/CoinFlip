# Testing FlipCoin on a device

There are three ways to try the app, from least to most setup.

## 1. Let CI do it

Every pull request runs a **device-test** job. It boots an Android 14 emulator (Pixel 6), installs the debug APK and walks through the app with `scripts/smoke-test.sh`:

1. Pick Space and Animals during onboarding.
2. Flip twice.
3. Open **Topics**, switch to Ocean only, and save.
4. Flip again.
5. Restart the app and check that onboarding is skipped.

The job fails if any screen doesn't appear or the app crashes. Screenshots of every step are in the run's **device-screenshots** artifact.

## 2. Install on your Android phone

1. Open the pull request, then **Checks → CI → Summary**, and download **flipcoin-debug-apk**.
2. Unzip it and copy the `.apk` to your phone, or download it on the phone directly.
3. Open it and allow "Install unknown apps" for your browser or file manager when asked.

## 3. Run it on an emulator on your laptop

### One-time setup (Windows, macOS or Linux)

1. Install [Android Studio](https://developer.android.com/studio). The setup wizard installs the Android SDK, the emulator and `adb`.
2. In Android Studio, open **Device Manager** (**More Actions → Virtual Device Manager** on the welcome screen), click **+**, pick **Pixel 6**, choose a recent system image (API 34 or newer), and finish.
3. Start the virtual device with the ▶ button.

On Windows, the emulator needs hardware virtualisation (Intel VT-x or AMD-V) turned on in the BIOS. Android Studio warns you if it's off.

### Installing a build

Pick whichever is easiest:

- **Drag and drop:** download **flipcoin-debug-apk** from CI, unzip it, and drag the `.apk` onto the emulator window.
- **Build from source:** open this repository in Android Studio, wait for Gradle sync, choose the `app` run configuration and the emulator, then click **Run**.
- **One command** (macOS, Linux or Git Bash on Windows): with an emulator running or a phone plugged in with USB debugging on, run

  ```bash
  scripts/run-latest-build.sh            # latest green build of your current branch
  scripts/run-latest-build.sh step-3-flip
  ```

  This needs `adb` on your `PATH` (it lives in the SDK's `platform-tools` folder) and the [GitHub CLI](https://cli.github.com) logged in with `gh auth login`.

### Running the automated walkthrough locally

With an emulator running:

```bash
./gradlew assembleDebug
scripts/smoke-test.sh app/build/outputs/apk/debug/app-debug.apk
```

Screenshots are saved to `screenshots/`. The script needs `python3`.
