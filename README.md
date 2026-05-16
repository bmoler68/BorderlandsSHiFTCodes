# Borderlands SHiFT Codes

An Android application for viewing and managing Borderlands SHiFT codes, with a sibling **static dashboard** (**`dashboard/`**) that draws from the **same Supabase dataset**. This unofficial fan project provides easy access to SHiFT codes for all Borderlands games.

**Current status (v2.0.0):** Both the app and **`dashboard/`** read **`borderlands_shift.shift_codes_current`** via Supabase PostgREST. Maintainer data flows **CSV → ETL → Postgres** (`appdata/BL_SHIFT_CODES.csv`, **`etl/`**, GitHub Actions). The Android client is **offline-first** (Room **v4**), syncs on launch and every four hours, and applies the **same row-validation rules** as the dashboard so invalid catalog rows never appear in either client.



## 🤖 About This Project

This application was completely written and is maintained using AI-assisted development tools as a personal project in AI application development. The entire codebase, architecture, and documentation were created with the assistance of AI coding agents, demonstrating modern AI-powered software development workflows. This project serves as both a functional application for the Borderlands community and a personal project in exploring AI-assisted development practices.

**Contribution Policy:** Since this application is an experiment in AI development, contributions are not currently being accepted. This allows the project to remain completely AI-developed and maintained, which is central to the experimental nature of this project. However, you are free to clone the repository and create your own forks or modifications for personal use.



## 🎮 Features

### Core Functionality
- 📱 **Modern Material 3 UI** with Jetpack Compose
- 🎯 **Comprehensive Game Support** for all Borderlands titles:
  - Borderlands 1 (BL1)
  - Borderlands 2 (BL2) 
  - Borderlands 3 (BL3)
  - Borderlands: The Pre-Sequel (TPS)
  - Tiny Tina's Wonderlands (Wonderlands)
  - Borderlands 4 (BL4)
- 🔄 **Remote sync via Supabase** — reads `borderlands_shift.shift_codes_current` (same REST source as the **`dashboard/`** GitHub Pages build)
- 📊 **Stable list ordering** — expiration descending, then `ingested_at_utc` descending, then code ascending (matches **`dashboard/`** detail grid)
- ✅ **Catalog row validation** — rows that fail app rules (code length, expiration flags, games, etc.) are skipped during sync with log warnings (dashboard applies the same rules in the browser console)
- 💾 **Offline-First Architecture** with Room database for local storage
- 📋 **One-tap Code Copying** to clipboard with visual feedback
- 🌐 **Direct SHiFT Website Access** via navigation drawer
- ✅ **User Redemption Tracking** with persistent local storage
- 🔔 **Smart Notifications** for new active SHiFT codes with automatic background sync

### Advanced Filtering System
- 🎛️ **Floating Action Button + Bottom Sheet** filter interface
- 🔍 **Smart Status Filtering**: All, Active, Expired, Non-expiring
- 🎮 **Game-specific Filtering**: Individual game selection or "All Games"
- 🎁 **Reward Type Filtering**: Filter by Keys, Cosmetics, or Gear
- 💾 **Persistent Filter Selections** that remember user preferences
- 📊 **Real-time Filter Summary** showing all active filters including reward type

### User Experience
- 📐 **Responsive Design** with window size class support for all screen orientations
- 🌙 **Light/Dark Theme Support** with system preference detection and manual override
- 🎨 **Theme-Aware UI Components** with consistent colors and contrast across themes
- 🍔 **Enhanced Navigation Drawer** with scrolling support and responsive sizing
- 📱 **Adaptive Layout** that works seamlessly on phones and tablets
- 🎨 **Consistent Visual Design** with proper spacing, typography, and colors
- 🔄 **Smart Sync System** with background data synchronization
- 📊 **Offline Mode Support** with cached data when network is unavailable
- 🔔 **Automatic Notifications** for new active codes every 4 hours
- ⚡ **Background Sync** using WorkManager for optimal battery efficiency
- 📅 **Precise Expiration Tracking** with date and time support in Eastern Time
- ⏰ **Expiration Time Display** showing exact expiration time on shift code cards
- 📦 **Compact View Mode** for ultra-efficient code browsing (shows 3x more codes)
- 🎚️ **View Toggle** switchable between normal and compact card layouts



## 📋 Release History

### v2.0.0 - Latest Release
**Supabase remote source:** The Android app syncs from the same **PostgREST** view as **`dashboard/`** instead of CSV URLs.

- **Changed:** Remote catalog from **Supabase** — **`borderlands_shift.shift_codes_current`** (paged REST, `Accept-Profile: borderlands_shift`, anon key)
- **Changed:** **`secrets.properties`** uses **`supabaseUrl`** and **`supabaseAnonKey`** (public anon key only; no service-role key in the client)
- **Removed:** **`csv.url`** / **`csv.fallback.url`** — CSV is maintainer input for **ETL** only (**`appdata/BL_SHIFT_CODES.csv`** → **`etl/`** → Postgres)
- **New:** Room **v4** schema aligned with Supabase — **`expirationDate`**, **`isNonExpiring`**, **`isUnknownExpiration`**, **`ingestedAtUtcMillis`** (single **3→4** migration from released v3 databases; drops legacy single **`expiration`** column)
- **New:** Shared **`ShiftCodeExpiration`** helpers — sort keys, lenient **`ingested_at_utc`** parsing, expiration normalization (dashboard uses equivalent JS validation)
- **New:** List order matches **dashboard** — expiration millis descending, ingest descending (unknown/placeholder ingest sorts last within the same expiration), code ascending
- **New:** Ingest backfill on sync when local ingest was unknown but remote has a real timestamp
- **New:** Row validation on sync — same rules as **`dashboard/`** (e.g. SHiFT code ≤ **29** characters, reward ≤ **200**, at least one game, valid expiration when flags are false); invalid rows are skipped
- **Updated:** Network security config for **`*.supabase.co`** / **`*.supabase.in`**
- **Updated:** **`dashboard/`** skips invalid rows and logs **`Skipping row …`** in the browser console (app parity)

### v1.8.0
**Stability & Data Integrity Update:** Fixed intermittent duplicate SHiFT code display on startup and hardened sync behavior for safer local database updates.

- **Fixed:** Intermittent startup issue where newly added codes could briefly appear twice
- **Enhanced:** Sync flow now enforces safer one-code-per-entry handling during remote/local reconciliation
- **Enhanced:** Added sync serialization to reduce race conditions during startup/background refresh overlap
- **Enhanced:** Soft-delete restore/update handling improved for codes that reappear in remote data
- **Improved:** Data-layer cleanup and deduplication logic refactored to reduce dead paths and technical debt
- **Maintained:** Full backward compatibility with existing functionality

### v1.7.0
**Maintenance & UI Improvements:** Updated build configuration/tooling and refined compact-view layout to improve readability.

- **Updated:** Target/compile SDK updated to **API 36**
- **Enhanced:** Release builds now enable **code minification** and **resource shrinking** for smaller APKs
- **Fixed:** Removed the redundant **status dot** in **compact view** to prevent SHiFT code text from being truncated
- **Maintained:** Full backward compatibility with existing functionality

### v1.6.0
**Open Source Release:** Application is now open source under the MIT License.

- **New:** Open source release with MIT License
- **New:** Updated copyright information in UI to reflect MIT License
- **New:** Comprehensive documentation for secrets configuration file
- **New:** Complete CSV file format documentation for data sources
- **Enhanced:** README updated with open source setup instructions
- **Enhanced:** Installation guide includes secrets file configuration steps
- **Maintained:** Full backward compatibility with existing functionality

### v1.5.0
**Major Update:** Added expiration time support with Eastern Time zone handling, reward type filtering (Keys, Cosmetics, Gear), and ultra-compact view mode for efficient code browsing.

- **New:** Expiration time support with precise date and time tracking
- **New:** Eastern Time (ET) zone support for accurate expiration calculations
- **New:** Expiration time display on shift code cards with ET notation
- **New:** Time-aware expiration logic considering both date and time values
- **New:** Reward type filtering system (Keys, Cosmetics, Gear)
- **New:** Ultra-compact view mode for space-efficient code browsing
- **New:** Compact view toggle in navigation drawer with persistent preference
- **New:** Compact card layout showing 3x more codes (60-80dp vs 180-220dp)
- **New:** Colored left border indicator for quick status identification
- **New:** Filter summary display showing reward filter state
- **Enhanced:** Expiration filter logic to account for date + time + timezone
- **Enhanced:** Database migration to support new expiration time and reward columns
- **Enhanced:** CSV parsing to handle EXPIRATION TIME, IS_KEY, IS_COSMETIC, IS_GEAR columns
- **Enhanced:** Status filtering consistency between display and filter logic
- **Fixed:** Expiration time parsing for 12-hour AM/PM format support
- **Fixed:** Filter logic to properly consider expiration date and time
- **Maintained:** Full backward compatibility with existing functionality

### v1.4.0
**Major Update:** Implemented offline-first architecture with Room database, user redemption tracking, comprehensive light/dark theme support, and automatic notification system for new SHiFT codes.

- **New:** Room database implementation for offline data storage
- **New:** User redemption tracking with persistent local storage
- **New:** Offline-first architecture with smart sync system
- **New:** Background data synchronization with conflict resolution
- **New:** Complete light/dark theme system with Material 3 theming
- **New:** Theme mode selection in navigation drawer (System, Light, Dark)
- **New:** Theme-aware UI components with consistent colors across themes
- **New:** Automatic notification system for new active SHiFT codes
- **New:** WorkManager integration for periodic background sync (every 4 hours)
- **New:** Smart notification channel management for Android 8.0+
- **New:** Rich notification content with code details and rewards
- **New:** Automatic permission handling for Android 13+ notification permissions
- **New:** Background sync constraints (network connected, battery not low)
- **New:** Notification system with proper error handling and retry logic
- **Enhanced:** Data layer with separate local and remote repositories
- **Enhanced:** Improved error handling and network resilience
- **Enhanced:** Comprehensive backup and data extraction rules
- **Enhanced:** Updated ProGuard rules for Room database support
- **Enhanced:** Theme-aware color functions for optimal contrast and accessibility
- **Enhanced:** Manual dependency management without DI frameworks
- **Enhanced:** Comprehensive notification testing and validation
- **Enhanced:** Battery-optimized sync scheduling with flex intervals
- **Maintained:** Full backward compatibility with existing functionality

### v1.3.0
**Maintenance:** Updated build configuration to improve management of version numbering.

- **Improved:** Enhanced version management system in AppConfig
- **Updated:** Build configuration for better version handling and display
- **Enhanced:** Repository and view model for improved version functionality
- **Refreshed:** Navigation drawer with updated version system
- **Updated:** Test cases to reflect new version management approach
- **Maintained:** Full backward compatibility with existing functionality

### v1.2.0
**Improvements:** Added CSV **primary/fallback URLs** when the Android client still downloaded Sheets/CSV endpoints directly.

- Fallback URL improved reliability when the primary Google-hosted CSV endpoint failed
- **Historical note:** later releases migrated the client to **Supabase PostgREST**; **`appdata/BL_SHIFT_CODES.csv`** remains the maintainer-fed input for **`etl/`**

### v1.1.0
**Improvements:** Enhanced handling of unknown expiration dates for SHiFT codes to provide better user experience.

- Added support for codes with expiration date `2075-12-31` to be treated as active status
- Codes with unknown expiration now display "Unknown" instead of the confusing far-future date
- Improved user experience by providing clearer status information
- Maintains backward compatibility with existing date handling

### v1.0.0 - Initial Release
**Features:** Initial release with core SHiFT code viewing and management functionality.

- Modern Material 3 UI built with Jetpack Compose
- Comprehensive game support for all Borderlands titles
- Published CSV / Sheets URLs fetched by earlier Android builds (prior to Supabase)
- Advanced filtering and search capabilities
- Responsive design for all screen sizes and orientations
- One-tap code copying and direct SHiFT website access



## 🚀 Getting Started

### 📋 Requirements

Before installing and running the application, ensure your device meets the following requirements:

#### Device Requirements
- **Minimum Android Version**: Android 7.0 (API level 24)
- **Target Android Version**: Android 16 (API level 36)
- **RAM**: Minimum 2GB RAM recommended for optimal performance
- **Storage**: ~50MB free space for installation
- **Network**: Internet connection required to sync catalog data from Supabase

#### Required Permissions
The app requires the following permissions to function properly:
- **INTERNET**: Required to synchronize SHiFT codes from your Supabase project (PostgREST)
  - *Used for*: HTTP requests to `{supabaseUrl}/rest/v1/...` with the anon key from `secrets.properties`
  - *Privacy*: Only public/read-only catalog data configured in Supabase is fetched (no login to Supabase from the app)
- **POST_NOTIFICATIONS**: Required for Android 13+ devices to show notifications for new codes
  - *Used for*: Displaying notifications when new active SHiFT codes are detected
  - *Privacy*: Only shows notifications for new codes; no personal data is collected
- **WAKE_LOCK**: Required for background sync operations
  - *Used for*: Allowing WorkManager to perform sync operations when device is sleeping
  - *Privacy*: No personal data is accessed; only syncs public SHiFT code data

#### Development Requirements (for developers)
- **Android Studio**: Iguana (2024.1.1) or later (recommended for Android Gradle Plugin 9.x used in this project)
- **Gradle**: 9.4.1 (as specified in `gradle/wrapper/gradle-wrapper.properties`)
- **Java**: JDK 11 or later
- **Kotlin**: 2.2.21 (as specified in libs.versions.toml)

### 🔧 Installation Steps

> **Note:** Pre-built APK files for the current version are available for download at the [Android Application Releases page](https://bmoler68.github.io/Releases/). If you prefer to build from source, follow the steps below.

1. **Clone the repository**
   ```bash
   git clone https://github.com/bmoler68/BorderlandsSHiFTCodes.git
   ```

2. **Create the secrets configuration file**
   - Copy `app/src/main/assets/secrets.properties.example` to `app/src/main/assets/secrets.properties`.
   - Set **`supabaseUrl`** (e.g. `https://YOUR_PROJECT.supabase.co`) and **`supabaseAnonKey`** — the anon / public REST key (**not** the service-role key).
   - Set **`privacy.policy.url`** and **`about.page.url`** as before.
   - **Important:** `secrets.properties` is gitignored and will not be committed.
   - Your Supabase project must **expose schema `borderlands_shift`** on the REST API and allow **anonymous `SELECT`** on **`shift_codes_current`** — same prerequisites as **`dashboard/`** (see **`sql/supabase_borderlands_shift_codes.sql`** and **`dashboard/README.md`**).

3. **Open in Android Studio**
   - Launch Android Studio
   - Open the project folder
   - Sync Gradle files

4. **Run on device or emulator**
   - Connect Android device or start emulator
   - Click the Run button or use `Shift + F10`



## 🔐 Secrets Configuration

The app reads **`app/src/main/assets/secrets.properties`** at startup. The file is gitignored so keys and URLs stay out of the repository.

### Required keys

| Key | Purpose |
|-----|---------|
| `supabaseUrl` | Supabase project URL (`https://…supabase.co` / `.in`), no trailing slash required |
| `supabaseAnonKey` | **Anon / public** key for PostgREST read — same credential family as **`dashboard/`** |
| `privacy.policy.url` | Privacy policy page URL |
| `about.page.url` | About page URL |

Never ship the **service-role** key in the Android client; ingestion uses **`SUPABASE_SERVICE_ROLE_KEY`** only on the server-side ETL workflow.

### Setting up secrets

```bash
cp app/src/main/assets/secrets.properties.example app/src/main/assets/secrets.properties
```

Example excerpt:

```properties
supabaseUrl=https://YOUR_PROJECT.supabase.co
supabaseAnonKey=YOUR_ANON_PUBLIC_KEY_HERE
privacy.policy.url=https://your-privacy-policy-url-here
about.page.url=https://your-about-page-url-here
```

If a required key is missing, the app fails at startup with a clear error pointing at `secrets.properties`.


## 🗄️ Backend, dataset & web dashboard

The **Android app** and the **`dashboard/`** site both load **`borderlands_shift.shift_codes_current`** through Supabase **PostgREST** (same anon key category, same exposed schema requirement).

### What the app downloads

- Paged **`GET`** on `…/rest/v1/shift_codes_current` with `Accept-Profile: borderlands_shift`, `Authorization: Bearer <anon>`, and **`apikey`**
- Rows use Supabase semantics: **`expiration_date`** (nullable), **`is_non_expiring`**, **`is_unknown_expiration`**, game/reward flags, and **`ingested_at_utc`**
- Invalid rows are **not stored** (validation matches **`dashboard/`** — see **`dashboard/README.md`**)
- **`ingested_at_utc`** drives secondary list ordering (**after expiration**); placeholder ingest on **1999-12-31** UTC is treated as unknown for sort
- UI display still shows **Never** / **Unknown** for flagged rows (not raw sentinel dates)

### Maintainer pipeline (CSV → Supabase → clients)

Editors maintain **`appdata/BL_SHIFT_CODES.csv`**. Changes can trigger **`supabase-shift-codes-etl`** (see **`.github/workflows/supabase-shift-codes-etl.yml`**) which loads data into Postgres using the **`service_role`** key; the apps only need **anon** read access.

DDL and policy notes live in **`sql/supabase_borderlands_shift_codes.sql`**.

### Maintainer CSV (`appdata/BL_SHIFT_CODES.csv`)

Human-editable import format for ETL (**not** fetched by the phone app anymore). Booleans are `true` / `false`; times are Postgres-friendly (`HH:mm:ss`, optional 12-hour on import):

| Column | Notes |
|--------|-------|
| `code`, `reward` | Required textual fields |
| `expiration_date`, `expiration_time` | Real expiration when neither flag below is `true`; times follow **ET** semantics downstream |
| `is_non_expiring` | Row is non-expiring; DB keeps `expiration_date` NULL |
| `is_unknown_expiration` | Unknown end date; DB keeps `expiration_date` NULL |
| `bl`, `bl_tps`, `bl2`, `bl3`, `wonderlands`, `bl4`, `is_key`, `is_cosmetic`, `is_gear` | Boolean columns |

Legacy **Google Sheets** columns (`CODE`, `EXPIRATION` magic dates) are **historical only** — use the layout above.

**Example:**

```csv
code,reward,expiration_date,expiration_time,is_non_expiring,is_unknown_expiration,bl,bl_tps,bl2,bl3,wonderlands,bl4,is_key,is_cosmetic,is_gear
YOUR-CODE,Example Keys,2030-12-31,00:00:00,false,false,false,false,true,false,false,true,true,false,false
```

### Static web dashboard

Built from **`dashboard/`** (charts, filters, detail table, Supabase). Uses the **same validation and sort rules** as the app. Local run and Pages deploy are in **`dashboard/README.md`**; CI injects **`DASHBOARD_SUPABASE_URL`** / **`DASHBOARD_SUPABASE_ANON_KEY`** in **`.github/workflows/dashboard-pages.yml`** (auto-deploy when **`dashboard/**`** changes).



## 📄 Legal Notice

**Unofficial Fan Project**  
**No affiliation with Gearbox Software or 2K Games**

This is an unofficial fan project created for the Borderlands community. The application is not affiliated with, endorsed by, or sponsored by Gearbox Software, 2K Games, or any of their subsidiaries.



## 📚 Open Source Licenses

This app uses several open-source libraries under permissive licenses. We acknowledge and appreciate the contributions of the open source community:

### Apache License 2.0
Licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

**Core Android Libraries:**
- `androidx.core:core-ktx` (1.17.0) - Kotlin extensions for Android core
- `androidx.lifecycle:lifecycle-runtime-ktx` (2.9.4) - Lifecycle management
- `androidx.activity:activity-compose` (1.11.0) - Compose Activity integration

**Jetpack Compose Libraries:**
- `androidx.compose:compose-bom` (2025.10.01) - Compose BOM for version management
- `androidx.compose.ui:ui` - Compose UI foundation
- `androidx.compose.ui:ui-graphics` - Compose graphics and drawing
- `androidx.compose.ui:ui-tooling` - Compose UI tooling
- `androidx.compose.ui:ui-test-manifest` - Compose UI test manifest
- `androidx.compose.material3:material3` - Material Design 3 components
- `androidx.compose.material:material-icons-extended` (1.7.8) - Extended Material Icons

**Database Libraries:**
- `androidx.room:room-runtime` (2.8.3) - Room database runtime
- `androidx.room:room-ktx` (2.8.3) - Room Kotlin extensions
- `androidx.room:room-compiler` (2.8.3) - Room annotation processor

**Background Work Libraries:**
- `androidx.work:work-runtime-ktx` (2.11.0) - WorkManager for background tasks and notifications

**Network and Async Libraries:**
- `com.squareup.okhttp3:okhttp` (5.3.0) — HTTP client used for Supabase PostgREST (JSON-over-HTTPS). Response bodies use the Android SDK **`org.json`** API (no extra Maven artifact; platform component under the Android Software Development Kit license from Google).
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` (1.10.2) - Coroutines for asynchronous operations

**Testing Libraries:**
- `androidx.test.ext:junit` (1.3.0) - Android JUnit extensions
- `com.android.tools:desugar_jdk_libs` (2.1.5) - Core library desugaring

**Development Tools:**
- Kotlin language features and compiler (2.2.21)
- Android Gradle Plugin (9.2.0, from `gradle/libs.versions.toml`)

### Eclipse Public License 1.0
Licensed under [EPL 1.0](https://www.eclipse.org/legal/epl-v10.html)

**Testing Library:**
- `junit:junit` (4.13.2) - JUnit testing framework

### MIT License
Licensed under the [MIT License](https://opensource.org/licenses/MIT)

**Testing Libraries:**
- `org.robolectric:robolectric` (4.16) - Robolectric for Android framework testing
- `org.mockito:mockito-core` (5.20.0) - Mockito for mocking in tests
- `org.mockito:mockito-inline` (5.2.0) - Mockito inline for final classes



## 📜 License

This project is licensed under the MIT License.

Copyright (c) 2025 Brian Moler

See the [LICENSE](LICENSE) file in the project root for the full license text.

---

This project is open source and available under the MIT License.