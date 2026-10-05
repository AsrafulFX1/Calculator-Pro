# Calculator

A beautifully crafted Android calculator designed with Jetpack Compose, Material 3, and Kotlin. Featuring high-precision arithmetic calculations, smart percentage operations, scientific functions, an age calculator, local calculation history with Room Database persistence, and seamless automatic device light/dark mode adaptation.

---

## Features

- **Standard & Real-Time Arithmetic**:
  - Instant expression evaluation with BODMAS / operator precedence.
  - Real-time number grouping (`1,000`, `1,000,000`) while typing.
  - Smart percentage calculation: Handles additive/subtractive percentages (`100 - 30% = 70`, `100 + 30% = 130`) as well as multiplicative percentages (`200 × 25% = 50`).
  - Auto-balancing parentheses and multi-parentheses support.

- **Scientific Mode**:
  - Trigonometric functions (`sin`, `cos`, `tan` and inverse `sin⁻¹`, `cos⁻¹`, `tan⁻¹`).
  - Angle unit toggle between Degrees (DEG) and Radians (RAD).
  - Exponential and power functions (`x²`, `x³`, `xʸ`), square root (`√`), cube root (`∛`).
  - Logarithms (`log`, `ln`), reciprocal (`1/x`), and mathematical constants (`π`, `e`).

- **Age Calculator**:
  - Exact age breakdown in years, months, and days.
  - Life statistics summary: Total months, total weeks, total days, total hours, and total minutes lived.
  - Next birthday countdown with day of week indication.
  - Clean date input with dynamic auto-slash formatting and date picker dialog.

- **Calculation History**:
  - Local persistence powered by Android Room Database.
  - Quick tap to recall expressions back to the display.
  - Copy expression or result to clipboard.
  - Individual item deletion and clear-all with confirmation.

- **Design & Theming**:
  - Signature **Purple / Violet** theme (Pastel Lilac Purple in Light Mode, Deep Violet in Dark Mode).
  - **Automatic System Sync**: App and top-right toggle switch automatically adapt to device's Light or Dark system theme.
  - Manual toggle support via top-right Sun ☀️ / Moon 🌙 switch.
  - Tactile squircle buttons with smooth touch feedback and configurable haptics/audio.
  - Sliding preferences drawer with auto-closing mode switcher.

---

## Technology Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
- **Programming Language**: [Kotlin](https://kotlinlang.org/)
- **Architecture**: MVVM (Model-View-ViewModel) with Kotlin Coroutines & `StateFlow`
- **Database**: [Room](https://developer.android.com/training/data-storage/room) with KSP (Kotlin Symbol Processing)
- **Testing**: JUnit 4 & [Robolectric](https://robolectric.org/) for local JVM unit and integration testing
- **Build System**: Gradle (Kotlin DSL, `build.gradle.kts`) with Version Catalog (`libs.versions.toml`)

---

## Project Structure

```text
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt               # Entry point Activity
│   │   │   │   ├── engine/
│   │   │   │   │   └── CalculatorEngine.kt       # Math parsing & calculation engine
│   │   │   │   ├── viewmodel/
│   │   │   │   │   └── CalculatorViewModel.kt    # UI state holder & business logic
│   │   │   │   ├── data/
│   │   │   │   │   ├── AppDatabase.kt            # Room Database
│   │   │   │   │   ├── HistoryDao.kt             # Room Data Access Object
│   │   │   │   │   ├── HistoryItem.kt            # Entity data class
│   │   │   │   │   ├── HistoryManager.kt         # Calculation history repository
│   │   │   │   │   ├── HistoryStorage.kt         # Storage interface & Room implementation
│   │   │   │   │   └── SettingsManager.kt        # User preferences (SharedPreferences)
│   │   │   │   ├── model/
│   │   │   │   │   ├── AngleMode.kt              # DEG / RAD enum
│   │   │   │   │   ├── CalculatorMode.kt         # BASIC / SCIENTIFIC / AGE enum
│   │   │   │   │   └── ThemeModels.kt            # ColorThemeScheme & Purple/Violet theme
│   │   │   │   └── ui/
│   │   │   │       ├── CalculatorScreen.kt       # Main screen with Display, Top Bar & Drawer
│   │   │   │       ├── components/
│   │   │   │       │   ├── CalcButton.kt         # Tactile squircle button
│   │   │   │       │   ├── CalculatorDisplaySection.kt # Top pill & display text
│   │   │   │       │   ├── ScientificKeypad.kt   # Scientific 5-row keypad
│   │   │   │       │   ├── SplitParenButton.kt   # Dual ( ) split button
│   │   │   │       │   ├── AgeCalculatorView.kt  # Age calculation screen
│   │   │   │       │   ├── HistoryPanel.kt       # Slide-in calculation history panel
│   │   │   │       │   ├── HistoryItemView.kt    # Individual history item card
│   │   │   │       │   └── CalculatorSidebar.kt  # Preferences & mode drawer
│   │   │   │       ├── theme/
│   │   │   │       │   ├── Theme.kt              # App Compose Theme
│   │   │   │       │   └── Type.kt               # Material 3 Typography
│   │   │   │       └── util/
│   │   │   │           └── ButtonFeedbackHelper.kt # Haptics & click sounds
│   │   │   └── res/                              # Drawables, mipmaps, strings, XML
│   │   └── test/
│   │       └── java/com/example/
│   │           ├── ExampleUnitTest.kt            # Math & engine unit tests
│   │           └── ExampleRobolectricTest.kt     # ViewModel, Settings, & Database tests
│   └── build.gradle.kts                          # Module-level Gradle configuration
├── gradle/
│   └── libs.versions.toml                        # Centralized dependency catalog
├── .env.example                                  # Safe environment variables template
├── .gitignore                                    # Production Git ignore rules
├── metadata.json                                 # App metadata
└── README.md                                     # Project documentation
```

---

## Getting Started

### Prerequisites

- **Android Studio**: Android Studio Hedgehog (2023.1.1) or newer (Ladybug / Iguana recommended)
- **JDK**: Java 17 or Java 21
- **Android SDK**: Android API level 36 (Minimum SDK: 24)

### Installation Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/calculator.git
   cd calculator
   ```

2. **Setup Environment Variables**:
   Copy the safe `.env.example` file to `.env` (optional, for external API keys):
   ```bash
   cp .env.example .env
   ```

3. **Open in Android Studio**:
   Open Android Studio, select **Open**, and navigate to the project root directory. Allow Gradle to sync dependencies automatically.

---

## Running and Building

### Run Locally on Emulator or Device

Connect an Android device with USB debugging enabled or launch an Android Virtual Device (AVD), then execute:
```bash
./gradlew installDebug
```
Or click the green **Run** button (Shift + F10) in Android Studio.

### Run Unit & Robolectric Tests

Execute the full suite of unit and integration tests:
```bash
./gradlew :app:testDebugUnitTest
```

### Build APK

To assemble a debug APK:
```bash
./gradlew assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

To build a release APK or AAB for Google Play:
```bash
./gradlew assembleRelease
# Or for Play Store App Bundle:
./gradlew bundleRelease
```

---

## Security & Privacy

- No sensitive credentials, private keys, or keystores are tracked in the repository.
- Sensitive files (`.env`, `debug.keystore`, `*.jks`, `local.properties`) are strictly excluded via `.gitignore`.
- Database data is stored strictly on-device using SQLite/Room. No user data leaves the device.

---

## Future Improvements

- Unit converter (Length, Weight, Temperature, Currency).
- Graphing calculator functionality for mathematical functions.
- Wear OS companion application.
