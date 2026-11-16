#!/usr/bin/env bash
# Downloads the latest debug APK built by CI and installs it on a connected
# phone or a running emulator.
#
# Usage: scripts/run-latest-build.sh [branch]   (default: current git branch)
# Needs: adb (Android SDK platform-tools) and gh (GitHub CLI, logged in).
set -euo pipefail

REPO="pika047/CoinFlip"
PACKAGE="io.github.pika047.flipcoin"
BRANCH="${1:-$(git rev-parse --abbrev-ref HEAD)}"

command -v adb > /dev/null || { echo "adb not found. See docs/TESTING.md, step 1." >&2; exit 1; }
command -v gh > /dev/null || { echo "gh not found. Install it from https://cli.github.com" >&2; exit 1; }

if ! adb devices | grep -qw "device$"; then
    echo "No phone or emulator connected. Start an emulator or plug in a phone (see docs/TESTING.md)." >&2
    exit 1
fi

RUN_ID=$(gh run list -R "$REPO" --branch "$BRANCH" --workflow CI --status success --limit 1 --json databaseId --jq '.[0].databaseId')
if [ -z "$RUN_ID" ]; then
    echo "No successful CI run found for branch $BRANCH." >&2
    exit 1
fi

DIR=$(mktemp -d)
echo "Downloading APK from CI run $RUN_ID ($BRANCH)"
gh run download "$RUN_ID" -R "$REPO" --name flipcoin-debug-apk --dir "$DIR"

adb install -r "$DIR"/*.apk
adb shell monkey -p "$PACKAGE" -c android.intent.category.LAUNCHER 1 > /dev/null
echo "FlipCoin is installed and open on your device."
