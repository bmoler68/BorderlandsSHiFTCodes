# Borderlands SHiFT Codes

An Android application for viewing and managing Borderlands SHiFT codes. This unofficial fan project provides easy access to SHiFT codes for all Borderlands games.



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
- 🔄 **Real-time Data Fetching** from Google Sheets with automatic updates
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

### v1.6.0 - Latest Release
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
**Improvements:** Added fallback URL system for improved reliability when Google Sheets CSV is unavailable.

- **New:** Added fallback URL system for improved reliability when Google Sheets CSV is unavailable
- Automatic fallback to backup CSV source if primary Google Sheets URL fails
- Enhanced network resilience and user experience
- Maintains backward compatibility with existing functionality

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
- Real-time data fetching from Google Sheets
- Advanced filtering and search capabilities
- Responsive design for all screen sizes and orientations
- One-tap code copying and direct SHiFT website access



## 🚀 Getting Started

### 📋 Requirements

Before installing and running the application, ensure your device meets the following requirements:

#### Device Requirements
- **Minimum Android Version**: Android 7.0 (API level 24)
- **Target Android Version**: Android 14 (API level 36)
- **RAM**: Minimum 2GB RAM recommended for optimal performance
- **Storage**: ~50MB free space for installation
- **Network**: Internet connection required for fetching SHiFT codes

#### Required Permissions
The app requires the following permissions to function properly:
- **INTERNET**: Required to fetch the latest SHiFT codes from remote servers
  - *Used for*: Downloading SHiFT code data from Google Sheets API
  - *Privacy*: No personal data is transmitted; only public SHiFT code data is fetched
- **POST_NOTIFICATIONS**: Required for Android 13+ devices to show notifications for new codes
  - *Used for*: Displaying notifications when new active SHiFT codes are detected
  - *Privacy*: Only shows notifications for new codes; no personal data is collected
- **WAKE_LOCK**: Required for background sync operations
  - *Used for*: Allowing WorkManager to perform sync operations when device is sleeping
  - *Privacy*: No personal data is accessed; only syncs public SHiFT code data

#### Development Requirements (for developers)
- **Android Studio**: Iguana (2024.1.1) or later (required for Android Gradle Plugin 8.13.1)
- **Gradle**: 9.2.0 (as specified in gradle-wrapper.properties)
- **Java**: JDK 11 or later
- **Kotlin**: 2.2.21 (as specified in libs.versions.toml)

### 🔧 Installation Steps

> **Note:** Pre-built APK files for the current version are available for download at the [Android Application Releases page](https://bmoler68.github.io/Releases/). If you prefer to build from source, follow the steps below.

1. **Clone the repository**
   ```bash
   git clone https://github.com/bmoler68/BorderlandsSHiFTCodes.git
   ```

2. **Create the secrets configuration file**
   - Copy `app/src/main/assets/secrets.properties.example` to `app/src/main/assets/secrets.properties`
   - Edit `secrets.properties` and fill in your actual URL values:
     - `csv.url`: URL to your primary CSV data source (e.g., Google Sheets CSV export URL)
     - `csv.fallback.url`: URL to your fallback CSV data source
     - `privacy.policy.url`: URL to your privacy policy page
     - `about.page.url`: URL to your about page
   - **Important**: The `secrets.properties` file is gitignored and will not be committed to the repository
   - See the [Secrets Configuration](#-secrets-configuration) section below for more details

3. **Open in Android Studio**
   - Launch Android Studio
   - Open the project folder
   - Sync Gradle files

4. **Run on device or emulator**
   - Connect Android device or start emulator
   - Click the Run button or use `Shift + F10`



## 🔐 Secrets Configuration

This application requires a `secrets.properties` file to store configuration URLs. This file is intentionally excluded from version control (via `.gitignore`) to keep sensitive URLs private.

### Setting Up Secrets

1. **Copy the example file**:
   ```bash
   cp app/src/main/assets/secrets.properties.example app/src/main/assets/secrets.properties
   ```

2. **Edit `secrets.properties`** with your actual values:
   ```properties
   # Network URLs
   csv.url=https://your-primary-csv-url-here
   csv.fallback.url=https://your-fallback-csv-url-here
   
   # App URLs
   privacy.policy.url=https://your-privacy-policy-url-here
   about.page.url=https://your-about-page-url-here
   ```

3. **Required Properties**:
   - `csv.url` (required): Primary URL for fetching SHiFT code data in CSV format
   - `csv.fallback.url` (required): Fallback URL if primary URL fails
   - `privacy.policy.url` (required): URL to your privacy policy page
   - `about.page.url` (required): URL to your about page

> **Development Note:** The primary and fallback CSV URL configuration was created because of the Google Sheets CSV implementation. Google Sheets may not always reliably serve CSV exports, so a fallback option was implemented in the app to automatically switch to a self-hosted version of the CSV file when Google Sheets fails to load. This ensures the app continues to function even when the primary data source is unavailable.

### How It Works

- The `secrets.properties` file is loaded at app startup from `app/src/main/assets/`
- If the file is missing or incomplete, the app will fail to start with a clear error message
- The file is automatically excluded from git via `.gitignore`
- A template file (`secrets.properties.example`) is included in the repository for reference

### CSV Data Sources

The app expects CSV data from URLs that return CSV-formatted content. Common sources include:
- Google Sheets (export as CSV)
- GitHub raw files
- Any web server hosting CSV files

See the [CSV File Format](#-csv-file-format) section below for the expected format.



## 📊 CSV File Format

The application fetches SHiFT code data from CSV files hosted at the URLs specified in `secrets.properties`. The CSV must follow a specific format for the app to parse it correctly.

### Required Columns

The CSV file must include a header row with the following columns (case-insensitive):

| Column Name | Required | Description | Example Values |
|------------|----------|-------------|----------------|
| `CODE` | ✅ Yes | The SHiFT code string | `K3KBT-9XJ6T-3WBR3-TT3JJ-9WX9H` |
| `EXPIRATION` | ✅ Yes | Expiration date in `yyyy-MM-dd` format | `2025-12-31`, `1999-12-31` (non-expiring), `2075-12-31` (unknown) |
| `REWARD` | ✅ Yes | Description of the reward | `3 Golden Keys` |

### Optional Columns

| Column Name | Required | Description | Example Values |
|------------|----------|-------------|----------------|
| `EXPIRATION TIME` | ❌ No | Expiration time in `HH:mm` or `HH:mm:ss` format (12-hour or 24-hour) | `11:59 PM`, `23:59:59`, `12:00 AM` |
| `BL` | ❌ No | Borderlands 1 compatibility (`Y` or `N`) | `Y`, `N` |
| `BL:TPS` | ❌ No | Borderlands: The Pre-Sequel compatibility (`Y` or `N`) | `Y`, `N` |
| `BL2` | ❌ No | Borderlands 2 compatibility (`Y` or `N`) | `Y`, `N` |
| `BL3` | ❌ No | Borderlands 3 compatibility (`Y` or `N`) | `Y`, `N` |
| `BL4` | ❌ No | Borderlands 4 compatibility (`Y` or `N`) | `Y`, `N` |
| `WONDERLANDS` | ❌ No | Tiny Tina's Wonderlands compatibility (`Y` or `N`) | `Y`, `N` |
| `IS_KEY` | ❌ No | Whether reward is a key (`Y` or `N`) | `Y`, `N` |
| `IS_COSMETIC` | ❌ No | Whether reward is cosmetic (`Y` or `N`) | `Y`, `N` |
| `IS_GEAR` | ❌ No | Whether reward is gear (`Y` or `N`) | `Y`, `N` |

### Special Date Values

- `1999-12-31`: Indicates a non-expiring code
- `2075-12-31`: Indicates an unknown expiration date (treated as active)

> **Development Note:** These special date values were originally created in earlier versions of the application to assist with identifying non-expiring and unknown expiration codes, and were also intended to ensure codes were sorted in the desired order. There are plans to eventually change these values to CSV flags (e.g., `IS_NON_EXPIRING`, `IS_UNKNOWN_EXPIRATION`), but this change has not been implemented yet to preserve backwards compatibility with prior versions of the application and avoid the need to maintain multiple CSV file sources to match different application versions.

### CSV Example

```csv
CODE,EXPIRATION,EXPIRATION TIME,REWARD,BL,BL2,BL3,BL4,BL:TPS,WONDERLANDS,IS_KEY,IS_COSMETIC,IS_GEAR
K3KBT-9XJ6T-3WBR3-TT3JJ-9WX9H,2025-12-31,11:59 PM,3 Golden Keys,Y,Y,Y,Y,N,N,Y,N,N
C35TB-WS6ST-TXBRK-TTTJT-JW6XX,1999-12-31,,Permanent Reward,Y,N,N,N,N,N,N,Y,N
```

### Time Format Support

The `EXPIRATION TIME` column supports multiple formats:
- **12-hour format**: `11:59 PM`, `12:00 AM`, `1:30 PM`
- **24-hour format**: `23:59:59`, `00:00:00`, `13:30:00`
- **With or without seconds**: `11:59 PM` or `11:59:00 PM`

All times are interpreted as **Eastern Time (ET)** for expiration calculations.

### Column Matching

- Column names are matched case-insensitively
- The `WONDERLANDS` column matches any header containing "Wonderlands" (case-insensitive)
- Missing optional columns default to `false` or empty string
- At least one game compatibility column (`BL`, `BL2`, `BL3`, `BL4`, `BL:TPS`, `WONDERLANDS`) must be `Y` for a valid code

### Parsing Behavior

- The CSV parser handles quoted fields properly
- Empty or blank required fields cause the row to be skipped
- Invalid date formats cause the row to be skipped
- The parser is tolerant of extra columns (they are ignored)



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
- `com.squareup.okhttp3:okhttp` (5.3.0) - HTTP client for network requests
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` (1.10.2) - Coroutines for asynchronous operations

**Testing Libraries:**
- `androidx.test.ext:junit` (1.3.0) - Android JUnit extensions
- `com.android.tools:desugar_jdk_libs` (2.1.5) - Core library desugaring

**Development Tools:**
- Kotlin language features and compiler (2.2.21)
- Android Gradle Plugin (8.13.1)

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