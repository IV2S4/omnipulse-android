# OmniPulse

Android all-in-one super-app (Kotlin + Jetpack Compose): social feed, short video clips, messaging, and an AI assistant.

Original product inspired by common social features — not affiliated with YouTube, Facebook, TikTok, or Messenger.

Built incrementally by a Cursor Automation agent.

## Current MVP

The current MVP includes a Material 3 app shell, an interactive social feed, and a vertically paged short-form Clips experience with deterministic local fixtures and per-clip controls. See [PROGRESS.md](PROGRESS.md) for the completed scope and next slice.

## Build

1. Install JDK 17+ and Android SDK Platform 37.
2. Point `ANDROID_HOME` or `ANDROID_SDK_ROOT` at the SDK.
3. Run:

   ```bash
   ./gradlew assembleDebug
   ```
