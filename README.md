# Aquatank

> A clean Android water-intake tracker that helps users set a daily hydration goal, log water throughout the day, review progress history, and learn healthier hydration habits.

![Android](https://img.shields.io/badge/Platform-Android-brightgreen?style=for-the-badge\&logo=android)
![Java](https://img.shields.io/badge/Language-Java-orange?style=for-the-badge\&logo=openjdk)
![SQLite](https://img.shields.io/badge/Database-SQLite-blue?style=for-the-badge\&logo=sqlite)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=for-the-badge)

## Overview

**Aquatank** is a native Android application built to make daily hydration tracking simple and practical. Users can enter their body weight, calculate a recommended daily water goal, add water intake entries, monitor progress with a visual indicator, review previous records, and keep their data stored locally on the device.

The app is built with **Java**, **XML layouts**, **Android Jetpack Navigation**, **ViewModel/LiveData**, **View Binding**, and **SQLite** for persistent local storage.

## Features

* **Daily hydration dashboard** — view today’s intake, daily goal, and progress at a glance.
* **Water intake logging** — add water entries throughout the day and track the total consumed amount.
* **Recommended goal calculation** — use body weight from settings to calculate a suggested daily water target.
* **Today’s log list** — review individual water logs for the current day.
* **History screen** — browse previous daily intake records and hydration progress.
* **Local SQLite storage** — keep daily summaries, individual logs, and user/community data on-device.
* **Information screen** — learn about the importance and benefits of staying hydrated.
* **Settings screen** — manage personal inputs such as body weight and hydration goal preferences.
* **Bottom navigation** — move between Home, History, Users, Information, and Settings sections.
* **Material Android UI** — built with modern Android UI components and XML-based layouts.

## Tech Stack

| Layer              | Technology                                         |
| ------------------ | -------------------------------------------------- |
| Platform           | Android                                            |
| Language           | Java                                               |
| UI                 | XML Layouts, Material Components, ConstraintLayout |
| Architecture       | Activity + Fragments + ViewModels                  |
| Navigation         | Android Jetpack Navigation                         |
| State/UI Data      | LiveData, ViewModel                                |
| Persistence        | SQLite via `SQLiteOpenHelper`                      |
| Build System       | Gradle                                             |
| Minimum SDK        | 24                                                 |
| Target/Compile SDK | 33                                                 |

## Project Structure

```text
Aquatank/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/watertracker/
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── WaterIntakeDBHelper.java
│   │   │   │   ├── IntakeRecord.java
│   │   │   │   ├── WaterLog.java
│   │   │   │   ├── CustomViewModelFactory.java
│   │   │   │   └── ui/
│   │   │   │       ├── home/
│   │   │   │       ├── history/
│   │   │   │       ├── information/
│   │   │   │       ├── settings/
│   │   │   │       └── users/
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── menu/
│   │   │   │   ├── navigation/
│   │   │   │   ├── values/
│   │   │   │   └── drawable/
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/
│   │   └── test/
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── LICENSE
```

## Core Screens

### Home

The Home screen is the main hydration dashboard. It shows the user’s daily target, current progress, and water intake logs for the day. Users can add intake amounts and immediately see their progress update.

### History

The History screen displays previous intake records, helping users review consistency and spot patterns in their hydration habits.

### Users

The Users section supports a community/progress-style showcase model, giving the app a more social and motivational layer.

### Information

The Information screen provides educational hydration content so users understand why regular water intake matters.

### Settings

The Settings screen stores user preferences such as weight, which is used to calculate or update the recommended daily water intake goal.

## Data Model

Aquatank stores data locally using SQLite. The main database helper is `WaterIntakeDBHelper`, which manages:

* **Daily intake summaries**

  * Date
  * Total water consumed
  * Goal amount

* **Individual water logs**

  * Log ID
  * Date
  * Time
  * Amount consumed

* **Users/community data**

  * User ID
  * Name
  * Email
  * Status
  * Initials

This makes the app fully usable without requiring an external backend.

## Getting Started

### Prerequisites

Make sure you have the following installed:

* [Android Studio](https://developer.android.com/studio)
* Android SDK 33 or compatible SDK tools
* JDK 17 or the JDK version required by your Android Gradle Plugin setup
* A physical Android device or emulator running Android 7.0+ / API 24+

### Installation

Clone the repository:

```bash
git clone https://github.com/Shabbin/Aquatank.git
```

Open the project folder:

```bash
cd Aquatank
```

Open the project in **Android Studio**:

1. Launch Android Studio.
2. Select **Open**.
3. Choose the cloned `Aquatank` folder.
4. Let Gradle sync complete.
5. Connect a device or start an emulator.
6. Click **Run**.

### Build from Terminal

You can also build the debug APK using Gradle:

```bash
./gradlew assembleDebug
```

On Windows:

```bat
gradlew.bat assembleDebug
```

The generated APK will be available under:

```text
app/build/outputs/apk/debug/
```

## Usage

1. Open the app.
2. Go to **Settings** and enter your body weight.
3. Return to **Home** to view your recommended daily water goal.
4. Add water intake entries whenever you drink water.
5. Track your progress through the dashboard and progress indicator.
6. Open **History** to review past intake records.
7. Visit **Information** to learn more about hydration.

## Architecture

Aquatank follows a straightforward Android architecture:

* `MainActivity` hosts the bottom navigation and fragment container.
* Each main section is implemented as a Fragment.
* ViewModels help separate UI logic from data/state handling.
* View Binding is enabled for safer and cleaner layout access.
* SQLite stores intake summaries, individual logs, and user records locally.

This structure keeps the app easy to understand, maintain, and extend.

## Dependencies

Key libraries used in the project include:

* AndroidX AppCompat
* Material Components
* ConstraintLayout
* AndroidX Lifecycle LiveData
* AndroidX Lifecycle ViewModel
* AndroidX Navigation Fragment
* AndroidX Navigation UI
* RecyclerView
* SQLite
* JUnit and Espresso for testing support

## Roadmap Ideas

Potential improvements for future versions:

* Add charts for weekly and monthly hydration trends.
* Add reminders and notification scheduling.
* Add metric/imperial unit switching.
* Add dark mode refinements.
* Add cloud sync or backup support.
* Add user profile customization.
* Add export/import support for hydration history.
* Add richer analytics for streaks, averages, and goal completion.

## Contributing

Contributions are welcome. To contribute:

1. Fork the repository.
2. Create a new feature branch:

```bash
git checkout -b feature/your-feature-name
```

3. Commit your changes:

```bash
git commit -m "Add your feature"
```

4. Push to your branch:

```bash
git push origin feature/your-feature-name
```

5. Open a pull request.

## License

This project is licensed under the **MIT License**. See the [`LICENSE`](LICENSE) file for details.

## Author

Developed by **Shabbin Hossain**.

GitHub: [@Shabbin](https://github.com/Shabbin)

---

<p align="center">
  Stay hydrated. Track consistently. Build better habits with Aquatank.
</p>
