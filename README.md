# Morad TV — Native Android IPTV Client

A premium IPTV / streaming Android application built with **Kotlin + Jetpack Compose + Dagger Hilt**. The app is a client that connects to a remote control panel API to dynamically manage all content (live channels, movies, anime), application settings, ads, and in-app updates.

> This is a **native Android project** — no web framework, no Capacitor, no JavaScript/TypeScript. Open it directly in Android Studio and build with `./gradlew assembleRelease`.

## Tech Stack

| Layer | Technology |
|------|-------------|
| Language | Kotlin 2.1.0 |
| UI | Jetpack Compose (Material 3) — BOM 2024.12.01 |
| Architecture | MVVM + Clean Architecture |
| DI | Dagger Hilt 2.55 |
| Networking | Retrofit 2.11.0 + OkHttp 4.12.0 + Gson |
| Images | Coil Compose 2.7.0 |
| Video | ExoPlayer / Media3 1.5.1 (HLS) |
| Navigation | Navigation Compose 2.8.7 |
| Ads | AdMob, AppLovin MAX, StartApp, Unity Ads |
| Min SDK | 24 / Target & Compile SDK 35 |
| Build | Gradle Kotlin DSL + Version Catalog (AGP 8.7.3) |
| Java | 17 |

## Project Structure

```
app/
├── build.gradle.kts
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml
    ├── res/
    └── java/com/lite/streamvault/
        ├── StreamVaultApp.kt          # @HiltAndroidApp
        ├── MainActivity.kt            # Single-activity, edge-to-edge, bottom nav
        ├── data/
        │   ├── dto/                    # Retrofit DTOs (Gson @SerializedName)
        │   ├── remote/                 # StreamVaultApi, ContentMapper
        │   └── repository/            # StreamVaultRepository (resilient)
        ├── domain/model/              # Domain models
        ├── ads/                        # AdPlatform, AdManager, network impls
        ├── di/                         # Hilt NetworkModule
        ├── ui/
        │   ├── theme/                 # Color, Typography, Theme
        │   ├── components/            # GlassmorphismCard, Shimmer, dialogs
        │   ├── screens/               # splash, home, channels, movies, anime, search, player, detail
        │   └── navigation/            # Routes + NavGraph
        ├── viewmodel/                 # HiltViewModels
        └── util/Constants.kt          # BASE_URL, ad IDs
```

## Build

1. Open the project root in **Android Studio Ladybug+** (or any IDE supporting AGP 8.7.3 / Kotlin 2.1.0).
2. Let Gradle sync. The Gradle wrapper (8.9) will be downloaded automatically.
3. Build a release APK:
   ```bash
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release.apk`

> The release build is signed with the debug keystore by default for convenience. Replace `signingConfig` in `app/build.gradle.kts` with your own release keystore before publishing.

## Configuration

### API Base URL
Edit `app/src/main/java/com/lite/streamvault/util/Constants.kt`:

```kotlin
const val BASE_URL: String = "https://delicate-starburst-a02af3.netlify.app/"
```

Use the **permanent Netlify production URL** — not a deploy-preview hash URL (those change on every deploy).

### Ad Units
Replace the placeholder IDs in `Constants.kt` and the `manifestPlaceholders` in `app/build.gradle.kts` (`admobAppId`, `applovinSdkKey`) with your own.

## API Endpoints

All calls are plain REST under `/api`:

| Endpoint | Method | Returns |
|----------|--------|---------|
| `api/settings` | GET | `AppSettings` |
| `api/categories?type=...` | GET | `List<Category>` |
| `api/channels` | GET | `List<Channel>` |
| `api/movies` | GET | `List<Movie>` |
| `api/anime` | GET | `List<Anime>` |
| `api/episodes?anime_id=...` | GET | `List<AnimeEpisode>` |
| `api/ads` | GET | `List<AdCampaign>` |

Read endpoints are public; only admin mutations require the `morad_tv_session` cookie (the Android client never sends credentials). The repository catches `JsonSyntaxException` (server returning HTML) and returns empty/default data instead of crashing.

## App Settings (Remote Config)

`AppSettings` fields: `appName`, `appVersion`, `appDescription`, `maintenanceMode`, `maintenanceMessage`, `forceUpdate`, `showAds`, `enableChannels`, `enableMovies`, `enableAnime`, `updateEnabled`, `updateVersion`, `updateUrl`, `updateMessage`, `supportEmail`, `privacyUrl`, `termsOfService`.

Bottom-nav tabs are built dynamically from `enableChannels`/`enableMovies`/`enableAnime`.

## Color Palette

Dark theme only — Deep Blue + White accent on Navy surfaces. Exact hex values are defined in `ui/theme/Color.kt` (e.g. `Blue400 = #60A5FA`, `DarkBg = #050B18`, `TextPrimary = #FFFFFF`).

## Permissions

`INTERNET`, `ACCESS_NETWORK_STATE`, `POST_NOTIFICATIONS` (Android 13+), `WAKE_LOCK`, `FOREGROUND_SERVICE`. Cleartext traffic is enabled in the manifest.

## Notes

- Package/class names are `com.lite.streamvault` / `StreamVault*` for architectural continuity; only the user-facing brand (app_name, splash title, Home header) is "Morad TV". Refactor via Android Studio's Rename to rebrand the `applicationId` too.
- The Movie/Anime/Anime-Episode DTO field names follow the same camelCase convention as the confirmed `channels` response. Verify each with a real `GET` request before finalizing.
