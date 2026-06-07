# Web Search

Web Search is a small Android app that opens selected text in Google Search.

When text is selected in another Android app, the text selection menu can show
`Web search`. Tapping it opens:

```text
https://www.google.com/search?q=<URL-encoded selected text>
```

The app passes the search URL to the browser with `Intent.ACTION_VIEW`.

## Privacy and Permissions

- The app does not store selected text.
- The app does not log selected text.
- The app does not use clipboard, Accessibility, notification, or storage permissions.
- The app does not include advertising SDKs, analytics SDKs, or external runtime libraries.
- The search term is passed to the browser that opens the Google Search URL.
- The app uses `INTERNET` only to check and download its APK from this GitHub repository.
- The app uses `REQUEST_INSTALL_PACKAGES` only to hand the downloaded APK to Android's installer.

Android, Samsung One UI, Chrome, and the target app decide the order and placement
of items in the text selection menu. This app cannot guarantee that `Web search`
always appears in the first row.

On some Samsung/Chrome combinations, third-party `PROCESS_TEXT` actions may only
appear under the three-dot overflow menu, may require enabling the app in the
menu's app list, or may not appear in every app. The app also supports Android's
text share flow as a fallback: sharing selected text to `Web search` opens the
same Google Search URL.

## Download Latest APK from GitHub

The latest debug APK is published automatically by GitHub Actions whenever `main`
changes.

Direct APK:

```text
https://raw.githubusercontent.com/masakasakasama/Select_to_search/main/dist/web-search-debug.apk
```

Release page:

```text
https://github.com/masakasakasama/Select_to_search/releases/tag/debug-latest
```

This keeps the GitHub-hosted APK updated automatically. The app itself does not
silently install updates; Android still shows the installer confirmation.

## In-App Auto Update

When `Web Search` is opened from the launcher, it checks the `debug-latest`
GitHub Release APK.

- First launch stores the current APK marker as the baseline.
- Later launches download the APK when the GitHub Release asset changes.
- After download, Android's package installer opens.
- The user must confirm the install because normal Android apps cannot silently update themselves.
- If Android asks for permission to install unknown apps, allow installs for `Web Search`, then reopen the app.

## Download APK from GitHub Actions

1. Open the repository's Actions page.
2. Open the latest successful `Build debug APK` run.
3. Download the `web-search-debug-apk` artifact.
4. Extract the zip and install `app-debug.apk`.

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

Install `web-search-debug.apk` or `app-debug.apk` on the Galaxy device. If Android
blocks installation, allow installs from the browser or file manager you used to
open the APK.

For USB debugging installation, connect the device and run:

```powershell
.\gradlew.bat installDebug
```

## Verify

1. Install and open `Web Search` once.
2. Open Chrome, Samsung Notes, Gmail, or another app with selectable text.
3. Select some text.
4. Check both the first text selection menu and the three-dot overflow menu.
5. If Samsung shows an app selection/manage list, enable `Web search` there.
6. Tap `Web search` and confirm that Google Search opens in a browser.
7. If it does not appear in that app, use Share for the selected text and choose `Web search`.
8. Confirm that `app/src/main/AndroidManifest.xml` contains no `uses-permission`.

## Project Settings

- App name: `Web Search`
- Package name: `com.tatsuya.websearch`
- Minimum SDK: 23
- Target SDK: 36
- Language: Kotlin
- UI: Android Views only, no Jetpack Compose
