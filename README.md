# Web Search

Web Search is a small Android app that opens selected text in Google Search.

When text is selected in another Android app, the text selection menu can show
`Web search`. Tapping it opens:

```text
https://www.google.com/search?q=<URL-encoded selected text>
```

The app passes the search URL to the browser with `Intent.ACTION_VIEW`.

## Privacy and Permissions

- The app has no network permission.
- The app does not store selected text.
- The app does not log selected text.
- The app does not use clipboard, Accessibility, notification, or storage permissions.
- The app does not include advertising SDKs, analytics SDKs, or external runtime libraries.
- The search term is passed to the browser that opens the Google Search URL.

Android, Samsung One UI, and the target app decide the order and placement of
items in the text selection menu. This app cannot guarantee that `Web search`
always appears in the first row.

## Build APK

Open this project in Android Studio, then run:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Install on Galaxy

Connect the Galaxy device with USB debugging enabled, then run:

```powershell
.\gradlew.bat installDebug
```

You can also install the generated APK manually on the device.

## Verify

1. Open Chrome, Samsung Notes, or another text app.
2. Select some text.
3. Confirm that `Web search` appears in the text selection menu or overflow menu.
4. Tap `Web search`.
5. Confirm that Google Search opens in a browser.
6. Confirm that `app/src/main/AndroidManifest.xml` contains no `uses-permission`.

## Project Settings

- App name: `Web Search`
- Package name: `com.tatsuya.websearch`
- Minimum SDK: 23
- Target SDK: 36
- Language: Kotlin
- UI: Android Views only, no Jetpack Compose
