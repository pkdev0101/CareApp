# CareApp 🏥
A memory care safety app built for seniors with Alzheimer's and dementia.

## What's This About?
CareApp helps seniors with memory loss stay safe and connected. The app is designed to be simple and friendly - big buttons, clear instructions, and all the important medical info in one place.

### Key Features
- **Medication Management** - Keep track of meds with reminders and completion tracking
- **Emergency Help** - Quick access to call caregivers or emergency services
- **Lost Mode** - Display info for finding your way home (caregiver name, phone, address)
- **Family Cards** - Photo/info of loved ones stored right on the phone
- **Daily Routine** - Track morning, afternoon, and evening tasks
- **Caregiver Notes** - Handoff notes so caregivers can stay in the loop
- **Emergency Card** - All medical info on one page (allergies, conditions, medications)
- **Safety Readiness** - Shows what's set up and what still needs attention
- **Doctor Reports** - Weekly care summaries for healthcare providers
- **QR Emergency Code** - Scannable emergency information card
- **Accessibility** - Text sizing, high contrast mode, simple interface

## Building & Running
You'll need:
- Android Studio (latest)
- Android device or emulator (API 30+)
- Java 17+

```bash
# Clone and build
git clone https://github.com/<your-account>/CareApp.git
cd CareApp
./gradlew assembleDebug

# Install to running device/emulator
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch
adb shell am start -n com.careapp.app/.MainActivity
```

## Tech Stack
- **UI:** Jetpack Compose
- **Database:** Room with SQLite
- **State Management:** Kotlin Flow + StateFlow
- **Notifications:** WorkManager for scheduled reminders
- **QR Codes:** ZXing library
- **Async:** Kotlin Coroutines

## Color Scheme
Warm, calm palette optimized for seniors:
- **Primary:** Soft slate blue (trust, calm)
- **Secondary:** Soft peach (family, warmth)
- **Accent:** Soft gold (gentle reminders)
- **Danger:** Warm red (emergency only)

## Features
- Medication logging with duplicate-dose warnings
- Task completion tracking with timestamps
- Emergency timeline logging
- Location sharing (local coordinates)
- Dark/light theme toggle
- Push notifications for medication reminders
- Android contacts integration
- PDF export for reports
- QR code generation for emergency info
- Caregiver handoff notes and summaries
- Weekly doctor reports

## Notes
All data is stored locally on the device. No cloud sync or external services required. The emergency call buttons are safe - they open the dialer but do not automatically place calls.
