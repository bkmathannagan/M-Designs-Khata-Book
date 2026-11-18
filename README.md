# 📘 M Designs Khata Book — Credit • Debit • Udhar • Ledger • Cash Book

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-00897B)
![Offline First](https://img.shields.io/badge/Mode-100%25%20Offline--First-FF6F00)

**M Designs Khata Book** is a fast, modern, offline-first digital **Udhar Khata / Bahi Khata / Credit-Debit Ledger & Cash Flow Manager** built for Indian shopkeepers, traders, wholesalers, freelancers, service providers, and individuals.

Developed by **[M Designs](https://mdesignsrmd.com)**.

---

## ✨ Key Features

### 📒 1. Smart Party & Udhar Khata Ledger
- **Customer & Supplier Management**: Add parties manually or use the **One-Tap Phone Contacts Picker** to auto-fill names and phone numbers.
- **Give & Receive Tracking**: Record **You Gave (Debit / Udhar)** and **You Got (Credit / Jama)** entries with payment mode (*Cash*, *UPI*, *Bank*), dates, bill references, and notes.
- **Automatic Running Balances**: Real-time calculation of per-party balances, total receivables (*You Will Get*), total payables (*You Will Give*), and net position.
- **Search & Smart Filters**: Filter accounts by *All*, *Receivable*, *Payable*, or *Settled* status with instant search.

### 💰 2. Income & Expense Manager (Cash Book)
- **Daily Cash Flow Tracking**: Log business or personal **+ Income** and **- Expense** entries across customizable categories (*Sales*, *Services*, *Purchase*, *Rent*, *Salary*, *Bills*) and payment modes.
- **PIN-Protected Edit & Delete**: Tap any cash transaction to **Edit**, **Duplicate**, **Share Receipt**, or **Move to Recycle Bin**. If App Lock is enabled, sensitive modifications require **4-digit PIN authentication**.
- **Net Cash Flow Summary**: Live overview of Total Income, Total Expense, and Net Cash Flow.

### 📲 3. Dynamic UPI QR Code & Payment Collection
- **Offline ZXing QR Generator**: Dynamically generates scannable UPI payment QR codes (`upi://pay`) pre-filled with your merchant UPI ID, business name, and exact pending customer balance.
- **Share & Direct Pay**: Share QR code images directly via WhatsApp or launch installed UPI apps (*Google Pay, PhonePe, Paytm, BHIM*).

### 📊 4. Professional PDF & Excel (CSV) Reports
- **Party Ledger Statements**: Export clean, branded PDF statements or Excel-compatible CSV spreadsheets for any customer or supplier.
- **Overall Business Reports**: Generate full account summaries and date-wise transaction reports with one-tap sharing via WhatsApp, Email, or print.

### ☁️ 5. Real Database Backup & Restore
- **Full JSON Snapshot Export**: Serializes your entire business profile, parties, ledger transactions, income/expense records, and categories into a portable `.json` backup file.
- **Cloud & Device Sharing**: Save backups directly to **Google Drive**, WhatsApp, Email, or local storage via the Android system share sheet.
- **Validated Restore**: Pick any backup file to restore or merge records with confirmation protection and an audit history log.

### 🔐 6. Security & Data Recovery
- **App Lock & Biometrics**: Protect your financial data with a SHA-256 hashed 4-digit **Security PIN** and device **Fingerprint / Biometric authentication**.
- **3-Tab Recycle Bin**: Soft-deleted *Parties*, *Ledger Transactions*, and *Cash Flow Records* are safely held in the Recycle Bin for 1-tap restoration or permanent deletion.

### 🧰 7. Business Utility Suite
- **Digital Business Visiting Card**: Live visiting card preview embedded with your dynamic merchant UPI QR code, ready to share with customers.
- **Custom Letterhead & Profile**: Configure your Business Name, Proprietor Name, Phone, Email, Address, GSTIN, Bank Details, and UPI ID.
- **Cash Denomination Calculator**: Quickly count physical Indian currency notes (`₹500`, `₹200`, `₹100`, `₹50`, `₹20`, `₹10`, coins) with instant grand totals.
- **Payment Reminders & Bank Holidays**: Schedule due-date notifications and check Indian bank holidays.
- **Multi-Language Support**: Localization-ready resources for **English, Tamil (தமிழ்), Hindi (हिन्दी), Malayalam (മലയാളം), Telugu (తెలుగు), Kannada (ಕನ್ನಡ), Gujarati (ગુજરાતી), Marathi (मराठी), and Bengali (বাংলা)**.

---

## 🛠️ Tech Stack & Architecture

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin 1.9+ |
| **UI Framework** | Jetpack Compose + Material Design 3 |
| **Architecture** | MVVM + Clean Repository Pattern |
| **Dependency Injection** | Dagger Hilt |
| **Local Database** | Room SQLite Database (`Flow` & `Coroutines`) |
| **Preferences** | Jetpack DataStore |
| **Background Jobs** | Android WorkManager (Payment Reminders) |
| **Security** | AndroidX Biometric API + SHA-256 PIN Hashing |
| **QR Code Engine** | ZXing Core (`com.google.zxing:core`) |
| **Document Export** | Android `PdfDocument` API & CSV/Excel Generator |

---

## 📂 Project Structure

```text
com.mdesigns.khata
├── core/
│   ├── database/       # Room Database & TypeConverters
│   ├── datastore/      # UserPreferencesDataStore (App Lock, Profile, Theme, Language)
│   ├── di/             # Hilt Dependency Injection Modules
│   ├── excel/          # CSV / Excel Report Generator
│   ├── pdf/            # Branded PDF Statement & Report Generator
│   ├── reminders/      # WorkManager ReminderWorker & NotificationHelper
│   ├── security/       # AppLockManager, BiometricHelper, CryptoUtils (SHA-256)
│   ├── sharing/        # WhatsApp, SMS & FileProvider ShareManager
│   ├── theme/          # Material 3 Color Palette, Typography & Shapes
│   └── utils/          # CurrencyFormatter, DateUtils, UpiHelper, LocaleHelper
├── data/
│   ├── dao/            # Room DAOs (LedgerPerson, Transaction, Money, Profile, Backup)
│   ├── entity/         # Room Database Entities
│   └── repository/     # KhataRepository, MoneyRepository, ProfileRepository, BackupRepository
├── feature/
│   ├── auth/           # PIN & Biometric LockScreen
│   ├── components/     # Reusable Compose UI, PinVerificationDialog, UpiPaymentQrDialog
│   ├── dashboard/      # HomeScreen & Financial Summary Cards
│   ├── khata/          # Party List, Search & Filters
│   ├── ledger/         # PersonLedgerScreen, Give/Receive Entry & Statement Export
│   ├── money/          # Income & Expense Manager with PIN-Secured Edit/Delete
│   ├── more/           # Profile, Backup/Restore, Business Card, Denomination, Recycle Bin, Settings
│   ├── onboarding/     # First-time Business Setup
│   ├── person/         # AddEditPersonScreen with Phone Contact Picker
│   ├── reports/        # Comprehensive PDF/Excel Reports Screen
│   └── splash/         # Branded Splash Screen
└── navigation/         # BottomNavigationBar & Compose NavGraph
```

---

## 🚀 Getting Started / Building the APK

### Prerequisites
- **Android Studio** Ladybug / Iguana or newer
- **JDK 17**
- **Android SDK 35** (Min SDK 24 / Target SDK 35)

### Build via Command Line

1. **Clone the repository**:
   ```bash
   git clone https://github.com/bkmathannagan/M-Designs-Khata-Book.git
   cd M-Designs-Khata-Book
   ```

2. **Compile Debug APK**:
   - **Windows (PowerShell)**:
     ```powershell
     .\gradlew.bat assembleDebug
     ```
   - **macOS / Linux**:
     ```bash
     ./gradlew assembleDebug
     ```

3. **Locate the generated APK**:
   ```text
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🌐 Branding & Credits

- **Company**: [M Designs](https://mdesignsrmd.com)
- **Product**: M Designs Khata Book
- **Copyright**: © M Designs. All rights reserved.
