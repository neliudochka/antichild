# AntiChild - Android App

Child control app.
Application is divided into two different apps: for parent and for child. The parent application features a button that activates listening to notifications about child's activity. The child's application includes a motion detection alarm. When activated, this alarm detects device motion, triggers an alarm and simultaneously sends notification to the parent's app. The alarm can be deactivated by entering a specific password either in the parent's application or directly in the child's application.

## Features
- Motion detection alarm

## Screenshots

## Technologies & Tools Used
- Language: Kotlin
- IDE: Android Studio
- Other:
  - Gradle
  - Fragment
  - SensorManager
  - View Binding
  - Service
  - MediaPlayer
  - AudioManager
  - NotificationChannel & NotificationManager & NotificationCompat
  - PendingIntent
  - AudioAttributes
  - BroadcastReceiver
  - Firebase
  - FCM

## Installation

Clone this repository
```bash
git clone git@github.com:neliudochka/antichild.git
```

## Build

- For **Android Studio**:  
  Open directory in the **Android Studio**.  
  Choose Build -> Make Project

- For **Gradle Wrapper**:

Move to the repo
```bash
// from the directory where you cloned repo  
cd AntiChild  
```

Build the project using Gradle Wrapper
 ```gradle
./gradlew build
``` 

- Download the APK from the release page(will be available later)

## Run
Connect a device or use some of the emulators.  
Download apk file to the device.

or

- For **Android Studio**:  
  Choose Run -> Run 'app'


## Project Status
Project is: _in early development_

## Contact
Created by [Liudmyla Gorbunova](https://github.com/neliudochka) and [Svitlana Barytska](https://github.com/svitbar)