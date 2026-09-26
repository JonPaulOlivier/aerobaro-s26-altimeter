# AeroBaro S26 — Android 16 Barometric Aircraft Altimeter

Precision barometric aircraft altimeter and elevation calibration application for **Android 16 (API 36)** and **Samsung Galaxy S26**.

## Direct Signed APK Download Link (GitHub Releases)

[![Build Android 16 APK](https://github.com/jonpaulolivier/aerobaro-s26-altimeter/actions/workflows/build-apk.yml/badge.svg)](https://github.com/jonpaulolivier/aerobaro-s26-altimeter/actions/workflows/build-apk.yml)

- **Direct Latest Signed APK Download**:
  [**Download AeroBaro-S26-Android16.apk**](https://github.com/jonpaulolivier/aerobaro-s26-altimeter/releases/latest/download/AeroBaro-S26-Android16.apk)

## How It Works
1. Every push to `main` triggers `.github/workflows/build-apk.yml`.
2. GitHub Actions compiles the Android 16 project, signs `AeroBaro-S26-Android16.apk` with an RSA-2048 release keystore, uploads the artifact, and publishes a GitHub Release with the direct download link above.
