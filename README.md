# Namma-Santhe Ledger

Namma-Santhe Ledger is a modern offline-first Android khata app built for small village vendors who need a fast way to manage customer credit (`Udari`) without depending on the internet, cloud services, or paid APIs.

## Highlights

- Fully offline with local Room database
- Built with Kotlin, Jetpack Compose, MVVM, Hilt, Coroutines, StateFlow, and Navigation Compose
- Fast customer search, filter, sort, swipe edit, and swipe delete
- Two-step transaction flow with large keypad UI
- Per-customer ledger with running balance
- Daily summary and weekly chart
- WhatsApp reminder intent for pending dues
- Dark mode support
- Seeded sample data for first launch

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM + Clean Architecture
- Room
- Hilt
- Coroutines + StateFlow
- Coil

## Project Structure

```text
app/src/main/java/com/nammasanthe/ledger/
├── data/
│   ├── local/
│   ├── mapper/
│   └── repository/
├── di/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── navigation/
├── presentation/
│   ├── components/
│   ├── customers/
│   ├── home/
│   ├── ledger/
│   ├── splash/
│   ├── summary/
│   └── transactions/
├── ui/theme/
└── utils/
```

## Design System

### Logo idea

The app logo uses a ledger-book inspired icon with earthy green and brown layers to communicate trust, bookkeeping, and local market warmth. The circular badge helps it feel polished and Play Store ready.

### Color palette

- Forest Green: `#5E7A57`
- Clay Brown: `#8C5A3C`
- Sand Beige: `#D8C1A0`
- Warm Cream: `#F8F2E7`
- Sunset Terracotta: `#D17B49`

### Typography

- Strong bold headlines for balance visibility
- Readable 16sp+ body sizes for senior-friendly scanning
- Rounded cards and spacious layout for low-stress usage

## Core Flows

### Dashboard

- Outstanding amount
- Today sales
- Amount collected
- Total customers
- Recent transactions

### Customers

- Add customer
- Edit customer
- Delete customer
- Search, dues filter, and sort options

### Transactions

- Select customer
- Choose credit or payment
- Enter amount using keypad
- Save quickly

### Ledger

- Full history
- Running balance
- Current due
- WhatsApp reminder

### Reports

- Today sales
- Pending dues
- Collected amount
- Weekly finance chart
- Monthly overview

## Sample Data

The database seeds first-launch sample customers and transactions automatically so the app opens with realistic data instead of an empty dashboard.

## Setup

1. Open the project folder in Android Studio.
2. Let Android Studio sync the Gradle files and download required Android dependencies.
3. Make sure an Android SDK for `compileSdk 35` is installed.
4. Run the `app` configuration on an emulator or device with Android 8.0+.

## Notes

- No Firebase
- No cloud backend
- No paid APIs
- No internet dependency for core app use
- WhatsApp reminder uses Android Intent and requires WhatsApp on the device

## Suggested Next Enhancements

- PDF ledger export
- Local backup / restore
- Kannada localization
- Biometric app lock
- Home screen widgets
