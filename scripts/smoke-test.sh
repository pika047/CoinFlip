#!/usr/bin/env bash
# Drives the app on a connected device or emulator through onboarding,
# flipping and changing topics, saving a screenshot at each step.
#
# Usage: scripts/smoke-test.sh path/to/app.apk [screenshot-dir]
set -euo pipefail

APK="${1:?Usage: $0 path/to/app.apk [screenshot-dir]}"
OUT="${2:-screenshots}"
PACKAGE="io.github.pika047.flipcoin"
mkdir -p "$OUT"

step() { echo "==> $*"; }

shot() {
    adb exec-out screencap -p > "$OUT/$1.png"
    echo "    saved $OUT/$1.png"
}

# Prints the centre "x y" of the first on-screen element whose text contains $1.
find_text() {
    adb shell uiautomator dump /sdcard/ui.xml > /dev/null 2>&1 || return 1
    adb exec-out cat /sdcard/ui.xml | python3 -c '
import re, sys, xml.etree.ElementTree as ET
needle = sys.argv[1]
try:
    root = ET.parse(sys.stdin)
except ET.ParseError:
    sys.exit(1)  # The screen dump was empty, e.g. while the app is still starting.
for node in root.iter("node"):
    if needle in node.get("text", "") or needle in node.get("content-desc", ""):
        x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.get("bounds")))
        print((x1 + x2) // 2, (y1 + y2) // 2)
        sys.exit(0)
sys.exit(1)
' "$1"
}

wait_for() {
    for _ in $(seq 1 30); do
        if find_text "$1" > /dev/null; then return 0; fi
        sleep 1
    done
    echo "Timed out waiting for \"$1\" on screen" >&2
    shot "failure"
    exit 1
}

tap() {
    wait_for "$1"
    read -r x y < <(find_text "$1")
    adb shell input tap "$x" "$y"
}

check_no_crash() {
    if adb logcat -d -b crash | grep -q "$PACKAGE"; then
        echo "The app crashed:" >&2
        adb logcat -d -b crash >&2
        exit 1
    fi
}

step "Installing $APK"
adb wait-for-device
adb install -r "$APK"
adb shell pm clear "$PACKAGE" > /dev/null
adb logcat -c -b crash || true
adb shell monkey -p "$PACKAGE" -c android.intent.category.LAUNCHER 1 > /dev/null

step "Onboarding"
wait_for "What do you want to learn about?"
shot "01-onboarding"
tap "🚀  Space"
tap "🐾  Animals"
shot "02-topics-picked"
tap "Start flipping"

step "Flipping"
wait_for "Flip the coin"
shot "03-flip-screen"
tap "Flip the coin"
sleep 0.5
shot "04-mid-flip"
wait_for "It's"
shot "05-first-result"
tap "Flip the coin"
wait_for "Flips: 2"
wait_for "It's"
shot "06-second-result"

step "Changing topics"
tap "Topics"
wait_for "Choose your topics"
shot "07-topic-picker"
tap "🚀  Space"
tap "🐾  Animals"
tap "🌊  Ocean"
shot "08-topic-picker-changed"
tap "Save"
tap "Flip the coin"
wait_for "Flips: 3"
wait_for "It's"
shot "09-result-after-topic-change"

step "Restarting the app keeps onboarding done"
adb shell am force-stop "$PACKAGE"
adb shell monkey -p "$PACKAGE" -c android.intent.category.LAUNCHER 1 > /dev/null
wait_for "Flip the coin"
shot "10-after-restart"

check_no_crash
step "All steps passed"
