#!/bin/bash

# Define variables for readability
APK_PATH="./app/build/outputs/apk/debug/app-debug.apk"
PACKAGE_NAME="com.wiconic.domoticzapp"
MAIN_ACTIVITY=".ui.MainActivity"
LOG_FILE="output.log"

./gradlew assembleDebug

echo "Installing APK..."
adb install -r "$APK_PATH"

if [ $? -ne 0 ]; then
    echo "❌ APK installation failed!"
    exit 1
fi

echo "Waiting briefly after install..."
sleep 2

echo "Launching application..."
adb shell am start -n "$PACKAGE_NAME/$MAIN_ACTIVITY"

sleep 1

echo "Capturing logcat output to $LOG_FILE..."
adb logcat -d | grep -E 'com.wiconic.domoticzapp|AndroidRuntime|Exception' > "$LOG_FILE"

adb shell logcat --pid=$(adb shell pidof com.wiconic.domoticzapp)

 
