# Google Antigravity Build Specification — M Designs Khata

## 1. Project Overview

Build a production-ready Android application named **M Designs Khata – Credit Debit**, a modern digital **Udhar Khata / Bahi Khata / Credit-Debit Ledger / Income-Expense Manager** for Indian shopkeepers, traders, wholesalers, freelancers, service providers, and personal users.

The application should use the supplied reference screenshots and the current Google Play listing for **Udhar Khata Book Credit Debit** as functional inspiration. Recreate the underlying workflows and feature coverage, but **do not copy proprietary logos, brand names, screenshots, artwork, source code, or exact visual assets**. Create an original M Designs visual identity and a cleaner modern Android UX.

Reference application:
https://play.google.com/store/apps/details?id=com.pccomputer.udhar_khata_book

The reference currently advertises PDF/Excel reporting, Google Drive backup, income/expense tracking, simple ledger entry, filters for settled/non-settled accounts, opening-balance reporting, app lock, business-card creation, and payment-request visibility controls. The supplied screenshots additionally demonstrate person-wise ledgers, Give/Receive entry flows, entry actions, PDF/Excel tools, reminders, recycle bin, profile/settings, WhatsApp messaging, and customizable business documents.

---

## 2. Company / Branding

### Company

- **Company Name:** M Designs
- **Website:** https://mdesignsrmd.com
- **Product Name:** M Designs Khata
- **Product Subtitle:** Credit • Debit • Udhar • Ledger • Cash Book
- **Developer / Copyright:** © M Designs
- **Country / Market:** India
- **Primary Currency:** Indian Rupee (₹)
- **Primary Language:** English
- **Localization-ready:** English, Tamil, Hindi, Malayalam, Telugu, Kannada, Gujarati, Marathi, Bengali and additional Indian languages.

Use the company name and website in:
- Splash screen
- About screen
- Settings > About
- Help & Support
- PDF/Excel reports
- Business-card/letterhead templates
- Optional footer of generated documents

Do not invent a company address, phone number, email address, GSTIN, or other legal information unless it is explicitly provided later.

---

## 3. Product Vision

Create a fast, offline-first digital khata that lets a user:

1. Add people/customers/suppliers.
2. Record money **Given / Debit / You Give**.
3. Record money **Received / Credit / You Receive**.
4. Maintain a running balance automatically.
5. Track income and expenses.
6. Send payment reminders.
7. Share account statements through WhatsApp, SMS and other Android sharing targets.
8. Generate professional PDF and Excel reports.
9. Backup and restore data using the user's own Google Drive.
10. Protect the application using PIN, Pattern and device biometric authentication.
11. Recover deleted entries through Recycle Bin.
12. Customize messages, letterheads, business profile, logo, QR code and stamp.
13. Provide a modern dashboard with actionable financial summaries.

The app must remain useful without an internet connection. Internet should only be required for services such as Google Drive synchronization, online sharing, optional update checks, and other explicitly network-dependent operations.

---

# 4. Technology Requirements

## Android

Use:

- Kotlin
- Android Studio
- Modern Android SDK
- Jetpack Compose
- Material 3
- Navigation Compose
- Room Database
- Kotlin Coroutines
- Flow / StateFlow
- WorkManager
- Android Biometric API
- Android Storage Access Framework
- Google Drive API for user-owned backup
- Apache POI or another Android-compatible spreadsheet generation approach for Excel/XLSX
- Android PDF generation / PdfDocument or a reliable PDF library
- Coil for image loading
- DataStore for application preferences
- Hilt for dependency injection
- MVVM + Clean Architecture
- Repository pattern

Avoid deprecated Android APIs wherever a current supported alternative exists.

Target current stable Android versions while maintaining reasonable compatibility with older supported Android devices.

---

# 5. Core Design Principles

- Offline-first
- Fast entry
- Minimal typing
- Large touch targets
- One-hand friendly
- Clear Credit vs Debit distinction
- Indian business terminology
- No unnecessary accounting complexity
- Reliable calculations
- No accidental data loss
- Easy WhatsApp sharing
- Professional PDF/Excel output
- Accessibility-friendly
- Dark mode
- Tablet-friendly responsive layouts
- Original M Designs visual identity

---

# 6. Main Navigation

Create a clear navigation structure:

### Bottom Navigation

1. **Home**
2. **Khata**
3. **Reports**
4. **Money**
5. **More**

### Primary Floating Action Button

A prominent `+ Add` button should provide:

- Give / Debit
- Receive / Credit
- Income
- Expense
- Add Person

Use quick actions so a user can record a transaction in seconds.

---

# 7. Splash Screen

Create a professional splash screen.

Display:

- M Designs logo
- **M Designs**
- **M Designs Khata**
- Subtitle: `Credit • Debit • Udhar • Ledger`
- Website: `mdesignsrmd.com`

After the splash screen:

- Check database initialization
- Check app-lock state
- Check whether onboarding is completed
- Check backup/restore state
- Navigate to the appropriate screen

Do not keep the splash screen unnecessarily long.

---

# 8. First Launch / Onboarding

Create a short onboarding flow.

### Screen 1
**Your Digital Khata**

Explain:
- Replace paper khata
- Track customers and suppliers
- Know who owes you and whom you owe

### Screen 2
**Give & Receive**

Explain:
- Record Debit/Give
- Record Credit/Receive
- Automatic running balance

### Screen 3
**Reports & Sharing**

Explain:
- PDF
- Excel
- WhatsApp
- SMS
- Payment reminders

### Screen 4
**Private & Safe**

Explain:
- Local-first storage
- Google Drive backup
- PIN / Pattern / Biometric lock

Provide:
- Skip
- Next
- Get Started

---

# 9. Business Profile Setup

On first setup ask for:

- Business / Shop Name
- Owner Name
- Mobile Number
- Address
- Email
- Website
- Optional GSTIN
- Currency
- Default language
- Logo
- QR Code image
- Stamp / Signature image

Allow all optional business fields to be skipped.

The business profile should later be editable under:

`More > Profile & Settings`

---

# 10. Home Dashboard

Create a professional dashboard showing:

### Summary Cards

- Total Receivable / You Collect
- Total Payable / You Give
- Net Balance
- Today's Income
- Today's Expense
- Monthly Income
- Monthly Expense
- Number of Customers
- Number of Suppliers
- Settled Accounts
- Pending Accounts

### Quick Actions

- Give
- Receive
- Income
- Expense
- Add Person
- Reminder
- Reports

### Recent Activity

Show recent transactions with:

- Person
- Description
- Amount
- Credit/Debit
- Date
- Time
- Balance after transaction

### Dashboard Filters

- Today
- This Week
- This Month
- Custom Date Range

---

# 11. Person / Khata Management

Create a searchable customer/person list similar in purpose to the reference screenshots, but with an original M Designs UI.

Each person should display:

- Initial/avatar/photo
- Name
- Mobile number
- Location/address
- Current balance
- Last transaction
- Balance direction:
  - Green = amount to collect
  - Red = amount to give
  - Neutral = settled

### Person List Features

- Search
- Sort
- Filter
- Add person
- Import contacts
- Edit person
- Delete person
- Restore deleted person
- Call
- WhatsApp
- SMS
- Share account
- Payment request
- Mark as settled
- Open ledger

### Filters

- All
- Receivable
- Payable
- Settled / Zero Balance
- Non-settled
- Customers
- Suppliers
- Personal
- Recently Added
- Recently Active

---

# 12. Add Person

Fields:

- Full Name
- Mobile
- Alternate Mobile
- Address
- City
- State
- Pincode
- Email
- Notes
- Category
- Opening Balance
- Opening Balance Type:
  - I Give
  - I Receive
- Profile Photo

Options:

- Save
- Save & Add Transaction
- Cancel

Support contact import through Android Contacts permission.

---

# 13. Person Ledger Screen

When a person is opened, display:

### Header

- Avatar/photo
- Person name
- Mobile
- Address
- Current balance
- Call
- WhatsApp
- More

### Search

Search within the person's transactions.

### Ledger Entries

Each entry should show:

- Date
- Time
- Description
- Category
- Debit/Give
- Credit/Receive
- Balance after transaction
- Entry number

### Bottom Summary

- Total Given
- Total Received
- Current Balance

### Main Actions

- Receive
- Give
- Payment Request
- Share Statement
- PDF
- More

---

# 14. Transaction Entry — Give / Debit

Create a fast modal/bottom-sheet/full-screen entry screen.

Title:

**Debit / Give**

Fields:

- Amount
- Date
- Time
- Description / Remark
- Category
- Payment Mode
- Optional attachment
- Optional due date
- Optional reminder

Payment modes:

- Cash
- UPI
- Bank Transfer
- Cheque
- Card
- Other

Buttons:

- Save
- Save & Share
- Cancel

Validation:

- Amount must be greater than zero.
- Date/time must be valid.
- Person must exist.
- Prevent accidental duplicate submission.

---

# 15. Transaction Entry — Receive / Credit

Create the corresponding:

**Credit / Receive**

Fields and behavior should mirror Give/Debit.

Allow:

- Amount
- Date
- Time
- Description
- Category
- Payment Mode
- Attachment
- Due date
- Reminder

Buttons:

- Save
- Save & Share
- Cancel

---

# 16. Income & Expense Tracker

Provide a separate money manager.

### Income

Examples:

- Sales
- Service Income
- Commission
- Other Income

### Expense

Examples:

- Purchase
- Rent
- Salary
- Electricity
- Transport
- Food
- Maintenance
- Other Expense

Support:

- Category
- Amount
- Date
- Description
- Payment mode
- Attachment
- Recurring transaction

Dashboard:

- Income
- Expense
- Net Cash Flow

---

# 17. Transaction More Menu

Every transaction should provide:

1. Edit Entry
2. Share Entry
3. Delete Entry
4. PDF Invoice / Receipt
5. Make Duplicate Entry
6. Convert to Credit
7. Convert to Debit
8. Move to Another Person
9. Copy to Another Person
10. Add / Edit Attachment
11. Add Reminder
12. View Audit Details

Deletion should move the entry to Recycle Bin rather than immediately destroying it.

---

# 18. Recycle Bin

Create:

`More > Recycle Bin`

Show:

- Deleted people
- Deleted transactions

Actions:

- Restore
- Permanently Delete
- Empty Recycle Bin

Ask for confirmation before permanent deletion.

Store deletion timestamp.

---

# 19. Payment Reminder System

Create a reminder engine.

Support:

- One-time reminder
- Daily
- Weekly
- Monthly
- Custom date
- Due-date reminder

Reminder data:

- Person
- Amount
- Due date
- Message
- Reminder date/time
- Repeat rule
- Status

Notification actions:

- Remind Later
- Mark Paid
- Send WhatsApp
- Send SMS
- Open Khata

Use WorkManager for reliable scheduled background work.

---

# 20. WhatsApp / SMS Sharing

Provide direct communication actions.

### WhatsApp

Generate a professional message containing:

- Customer name
- Business name
- Transaction amount
- Current balance
- Payment direction
- Due date
- Optional payment request
- Website / business contact

Support:

- WhatsApp
- WhatsApp Business

### SMS

Allow sending a concise statement/reminder through Android SMS intent where supported.

Do not silently send SMS. Always use the appropriate Android user-confirmed communication flow.

### Custom Message

Allow users to customize:

- Reminder message
- Payment received message
- Payment due message
- Statement message

Provide placeholders such as:

`{NAME}`
`{AMOUNT}`
`{BALANCE}`
`{BUSINESS}`
`{DATE}`
`{DUE_DATE}`

---

# 21. Payment Request

Add a Payment Request feature.

Allow:

- Request full balance
- Request selected amount
- Optional message
- Show/hide total credit/debit amount
- Share through WhatsApp/SMS/other apps

The app should not pretend to process a payment unless a real payment gateway/UPI integration is explicitly configured.

For UPI:

- Support generating a UPI payment URI when valid payer/payee details are available.
- Display a clear confirmation before launching an external payment application.

---

# 22. PDF Reports

Provide professional PDF generation.

### PDF Types

1. Full Ledger
2. Short Statement
3. Person Statement
4. Credit Report
5. Debit Report
6. Income Report
7. Expense Report
8. Daily Report
9. Monthly Report
10. Date Range Report
11. Payment Receipt
12. Invoice/Receipt
13. Balance Summary

### PDF Content

Header:

- M Designs
- Business name
- Logo
- Address
- Mobile
- Email/website if configured
- Report title
- Date range

Person report:

- Person name
- Contact
- Opening balance
- Transactions
- Total Debit
- Total Credit
- Closing balance

Include opening balance in the PDF.

Footer:

- Generated date/time
- Page number
- Optional website
- Optional stamp/signature

Allow:

- Preview
- Share
- Print
- Save
- Open with another application

---

# 23. Excel Reports

Provide Excel export.

Generate `.xlsx` files.

Reports:

- All People
- All Transactions
- Person Ledger
- Credit Report
- Debit Report
- Income
- Expense
- Daily Summary
- Monthly Summary
- Custom Date Range

Columns should include:

- Date
- Time
- Person
- Description
- Category
- Debit
- Credit
- Payment Mode
- Balance
- Reference/Entry ID

Allow:

- Preview if supported
- Save
- Share
- Email
- WhatsApp via file sharing

---

# 24. Weekly Excel Email

Provide an optional setting:

**Send Excel Data Sheet Weekly to Gmail**

Settings:

- Enable/Disable
- Recipient email
- Day
- Time
- Report type

Do not store Gmail passwords.

Prefer Android's supported account/email mechanisms or an explicit user-controlled email/share flow. Never collect a user's Gmail password inside the app.

---

# 25. Google Drive Backup & Restore

Implement user-owned Google Drive backup.

Requirements:

- Google Sign-In
- Select Google account
- Create app backup folder
- Backup local database
- Backup images/attachments
- Restore database
- Restore attachments
- Show last backup date/time
- Show backup status
- Manual backup
- Automatic backup
- Export backup file
- Import backup file

Important:

- Do not upload user ledger data to an M Designs server by default.
- User data should remain local unless the user explicitly chooses Google Drive backup.
- Explain backup status clearly.
- Before restore, show a confirmation and warn about overwrite/merge behavior.
- Provide conflict handling.
- Provide backup integrity validation.

Support a safe backup architecture that can be extended later for multi-device synchronization.

---

# 26. Local Data Protection

Use:

- Room database
- Encrypted sensitive preferences
- Secure key storage
- Android Keystore where appropriate
- Database backup validation
- Transaction-safe writes

Do not store:

- Plain-text passwords
- Gmail passwords
- Sensitive authentication secrets

---

# 27. App Lock

Implement:

### PIN Lock

- Create PIN
- Confirm PIN
- Change PIN
- Forgot PIN recovery flow

### Pattern Lock

- Create pattern
- Confirm pattern
- Change pattern

### Biometric

- Fingerprint
- Face authentication where supported by Android biometric hardware

Settings:

- Lock immediately
- Lock after 1 minute
- Lock after 5 minutes
- Lock when app goes to background
- Disable lock

Protect:

- App startup
- Sensitive reports
- Backup/restore settings
- Business profile
- Optional individual profile

---

# 28. Multi-Profile Support

Improve on the reference application's single-profile limitation.

Allow:

- Multiple business profiles
- Personal profile
- Shop 1
- Shop 2
- Branch profiles

Each profile should have isolated:

- People
- Transactions
- Reports
- Business profile
- Settings
- Backup namespace

Provide:

`Switch Profile`

and:

`Add New Profile`

Allow optional profile-specific app lock.

---

# 29. Business Card Maker

Create a simple business-card generator.

Fields:

- Business name
- Owner name
- Phone
- WhatsApp
- Address
- Email
- Website
- GSTIN
- QR code
- Logo

Templates:

- Classic
- Modern
- Minimal
- Retail
- Professional

Actions:

- Preview
- Save as image
- Share
- Print

Do not use copyrighted template assets.

---

# 30. Letterhead

Create:

`More > Make Your Letterhead`

Allow:

- Logo
- Business name
- Address
- Contact
- Website
- GSTIN
- QR code
- Stamp
- Footer
- Custom text

Use the letterhead in generated PDFs.

---

# 31. Profile & Settings

Create a comprehensive settings page inspired by the supplied reference screenshot, but with a modern M Designs layout.

Sections:

### Business

- Business profile
- Logo
- QR code
- Stamp
- Letterhead
- Business card

### Communication

- Custom WhatsApp message
- WhatsApp Business / Regular preference
- SMS message
- Payment reminder

### Reports

- PDF settings
- Excel settings
- Weekly Excel email
- Report logo
- Show/hide totals
- Date format
- Currency format

### Backup

- Google Drive account
- Backup now
- Restore
- Export backup
- Import backup
- Last backup status

### Security

- App Lock
- PIN
- Pattern
- Biometrics

### Appearance

- Light
- Dark
- System default
- Compact / Comfortable list density
- Font size where practical

### Audio / Haptics

- Sound On/Off
- Vibration On/Off

### Data

- Recycle Bin
- Export Data
- Restore Data
- Clear local data
- Demo/sample data

### General

- Language
- Currency
- Date format
- First day of week
- Notifications
- About
- Help & Support
- Privacy Policy
- Terms

---

# 32. Denomination Helper

Include an optional quick cash-denomination helper inspired by the reference application.

Allow denomination preferences such as:

- ₹5
- ₹10
- ₹20
- ₹50
- ₹100
- ₹200
- ₹500

Allow users to enable/disable denominations.

This feature must be optional and must not interfere with normal transaction entry.

---

# 33. Bank Holidays

Create an optional bank-holiday calendar.

Purpose:

- Reminder scheduling
- Due-date awareness
- Business planning

Keep holiday data updateable rather than hard-coded permanently.

---

# 34. Search & Filtering

Global search:

- Person name
- Phone
- Address
- Description
- Category
- Entry ID

Transaction filters:

- Credit
- Debit
- Income
- Expense
- Payment mode
- Category
- Date range
- Amount range
- Person
- Settled/non-settled

Search must be fast and work offline.

---

# 35. Categories

Allow users to:

- Add category
- Edit category
- Delete category
- Reorder category
- Restore category

Default categories should be sensible for Indian small businesses, but all categories must be customizable.

---

# 36. Attachments

Allow attaching:

- Photo
- Camera image
- PDF
- Document

Attachments can be associated with:

- Person
- Transaction

Provide:

- Preview
- Rename
- Share
- Delete
- Restore

Store files safely in app-specific storage.

---

# 37. Reports Dashboard

Create a visual analytics screen.

Charts:

- Credit vs Debit
- Income vs Expense
- Monthly cash flow
- Top receivables
- Top payables
- Outstanding aging
- Category-wise expense

Filters:

- Today
- Week
- Month
- Quarter
- Year
- Custom

Use accessible chart labels and provide a list/table alternative.

---

# 38. Accounting Rules

Define one consistent internal model.

For each person:

`Opening Balance + Debit Transactions - Credit Transactions = Net Balance`

The UI terminology should clearly explain direction so users do not confuse debit/credit.

Recommended display:

- **You Collect** = money the person owes the business/user.
- **You Give** = money the business/user owes the person.

Every transaction must be immutable in accounting history except through a controlled edit/reversal mechanism.

When editing a transaction:

- Preserve audit metadata.
- Recalculate balances.
- Update reports.
- Never create duplicate balance effects.

---

# 39. Data Model

Create Room entities similar to:

### BusinessProfile

- id
- name
- ownerName
- mobile
- alternateMobile
- address
- city
- state
- pincode
- email
- website
- gstin
- logoUri
- qrUri
- stampUri
- createdAt
- updatedAt

### LedgerPerson

- id
- profileId
- name
- mobile
- alternateMobile
- address
- city
- state
- pincode
- email
- category
- openingBalance
- openingBalanceType
- notes
- photoUri
- isSettled
- isDeleted
- createdAt
- updatedAt

### Transaction

- id
- profileId
- personId
- type
- amount
- date
- time
- description
- categoryId
- paymentMode
- dueDate
- reminderId
- attachmentUri
- referenceNumber
- isDeleted
- createdAt
- updatedAt

### MoneyTransaction

For independent income/expense records.

### Category

- id
- profileId
- name
- type
- icon
- sortOrder
- isActive

### Reminder

- id
- personId
- transactionId
- amount
- dueDate
- reminderDateTime
- recurrence
- message
- status

### Attachment

- id
- transactionId
- personId
- uri
- name
- mimeType
- size
- createdAt

### BackupRecord

- id
- profileId
- driveFileId
- backupDate
- status
- checksum
- version

### AppSettings

Store non-sensitive preferences using DataStore.

---

# 40. UI / UX Direction

Do not reproduce the old reference application's dated interface exactly.

Create an original modern design:

- Material 3
- Clean cards
- Rounded but not excessive corners
- Strong typography hierarchy
- Large amount typography
- Clear green/red financial semantics
- White/light surfaces
- Dark mode
- M Designs brand identity

Suggested palette:

- Primary: Deep Teal / Emerald
- Success: Green
- Debit: Red
- Credit: Green
- Warning: Amber
- Background: Very Light Neutral
- Dark Background: Near Black
- Accent: M Designs brand color

Do not hard-code colors throughout the app. Centralize them in the theme.

---

# 41. Accessibility

Support:

- TalkBack labels
- Minimum touch target sizes
- High contrast
- Scalable text
- Meaningful content descriptions
- Non-color-only indicators
- Keyboard navigation where applicable
- Accessible error messages

---

# 42. Notifications

Notification types:

- Payment due
- Reminder
- Backup failed
- Backup completed
- Restore completed
- Weekly report ready
- Security event where useful

Allow per-category notification controls.

---

# 43. Security & Privacy

Implement privacy by design.

Rules:

- No unnecessary server-side storage.
- Do not upload ledger data without user consent.
- Google Drive backup belongs to the user's Google account.
- Explain permissions before requesting them.
- Minimize permissions.
- Protect local sensitive information.
- Never expose private ledger data in notification text unless the user enables it.
- Provide data export.
- Provide local data deletion.
- Provide backup deletion guidance.
- Add a clear Privacy Policy URL configuration point.

---

# 44. Permissions

Request permissions only when required.

Potential permissions:

- Notifications
- Contacts
- Camera
- Storage/media as required by Android version
- Biometrics
- Internet

Do not request unnecessary permissions.

For Android versions where scoped storage applies, follow modern storage APIs.

---

# 45. Error Handling

Every important operation must have:

- Loading state
- Empty state
- Success state
- Failure state
- Retry action
- User-friendly error message

Important scenarios:

- Database failure
- Backup failure
- Restore failure
- No Google account
- Drive unavailable
- File generation failure
- WhatsApp unavailable
- SMS unavailable
- Permission denied
- Invalid transaction
- Duplicate transaction attempt

Never silently fail.

---

# 46. Performance

The app should remain fast with:

- 10,000+ people
- 100,000+ transactions
- Large PDF exports
- Large Excel exports

Use:

- Room indexes
- Paging where appropriate
- Background report generation
- Coroutines
- WorkManager
- Lazy lists
- Efficient image handling

Never perform large database/export operations on the main UI thread.

---

# 47. Testing Requirements

Create:

### Unit Tests

- Balance calculation
- Opening balance
- Debit/Credit conversion
- Income/expense totals
- Date filtering
- Report totals
- Reminder scheduling rules

### Database Tests

- Insert
- Update
- Delete
- Restore
- Recycle Bin
- Backup/restore consistency

### UI Tests

- Add person
- Add debit
- Add credit
- Edit transaction
- Delete/restore
- Search
- Filter
- Generate PDF
- Generate Excel
- App lock
- Profile switching

### Regression Tests

Verify that every feature remains functional after navigation and configuration changes.

---

# 48. Important Functional Acceptance Tests

The application is not complete until all of the following work:

- [ ] Add a person
- [ ] Import a contact
- [ ] Set opening balance
- [ ] Add Give/Debit transaction
- [ ] Add Receive/Credit transaction
- [ ] Running balance updates correctly
- [ ] Edit transaction
- [ ] Share transaction
- [ ] Delete transaction
- [ ] Restore transaction
- [ ] Duplicate transaction
- [ ] Move transaction
- [ ] Copy transaction
- [ ] Convert Debit/Credit
- [ ] Add reminder
- [ ] Send WhatsApp message
- [ ] Send SMS intent
- [ ] Generate payment request
- [ ] Generate PDF
- [ ] Generate Excel
- [ ] Share PDF
- [ ] Share Excel
- [ ] Add income
- [ ] Add expense
- [ ] View dashboard
- [ ] Search people
- [ ] Filter settled accounts
- [ ] Filter non-settled accounts
- [ ] View opening balance in PDF
- [ ] Use Recycle Bin
- [ ] Configure business profile
- [ ] Upload logo
- [ ] Upload QR code
- [ ] Upload stamp
- [ ] Create business card
- [ ] Create letterhead
- [ ] Customize messages
- [ ] Configure sound
- [ ] Configure vibration
- [ ] Configure WhatsApp preference
- [ ] Configure weekly Excel report
- [ ] Backup to Google Drive
- [ ] Restore from Google Drive
- [ ] Export local backup
- [ ] Import local backup
- [ ] PIN lock
- [ ] Pattern lock
- [ ] Biometric lock
- [ ] Switch profiles
- [ ] Dark mode
- [ ] Localization-ready UI

---

# 49. Navigation Map

Implement approximately:

```text
Splash
  |
Onboarding
  |
Business Setup
  |
App Lock Setup (optional)
  |
Home
  |
  +-- Khata
  |     +-- Person List
  |     |     +-- Add Person
  |     |     +-- Person Ledger
  |     |           +-- Give
  |     |           +-- Receive
  |     |           +-- Transaction Details
  |     |           +-- PDF
  |     |           +-- Share
  |     |           +-- Reminder
  |     |
  |     +-- Search / Filter
  |
  +-- Money
  |     +-- Income
  |     +-- Expense
  |     +-- Cash Flow
  |
  +-- Reports
  |     +-- PDF
  |     +-- Excel
  |     +-- Analytics
  |
  +-- More
        +-- Reminders
        +-- Recycle Bin
        +-- Business Card
        +-- Letterhead
        +-- Backup & Restore
        +-- Profile & Settings
        +-- App Lock
        +-- Categories
        +-- Bank Holidays
        +-- Help & Support
        +-- About M Designs
```

---

# 50. Suggested Package Structure

Use a maintainable package structure:

```text
com.mdesigns.khata

├── core
│   ├── database
│   ├── datastore
│   ├── security
│   ├── backup
│   ├── pdf
│   ├── excel
│   ├── sharing
│   └── utilities
│
├── data
│   ├── entity
│   ├── dao
│   ├── repository
│   └── datasource
│
├── domain
│   ├── model
│   └── usecase
│
├── feature
│   ├── splash
│   ├── onboarding
│   ├── dashboard
│   ├── person
│   ├── ledger
│   ├── transaction
│   ├── incomeexpense
│   ├── reports
│   ├── reminders
│   ├── backup
│   ├── profile
│   ├── settings
│   ├── security
│   └── businesscard
│
└── navigation
```

---

# 51. Google Antigravity Implementation Instructions

You are acting as the senior Android engineer and product engineer.

### Phase 1 — Project Foundation

- Create the Android project.
- Configure Kotlin.
- Configure Compose and Material 3.
- Configure Hilt.
- Configure Room.
- Configure DataStore.
- Configure navigation.
- Configure app theme.
- Configure build variants.
- Create the M Designs branding foundation.

### Phase 2 — Database

- Implement all required entities.
- Implement DAOs.
- Add indexes.
- Add migrations.
- Implement repositories.
- Implement balance calculations.
- Add database tests.

### Phase 3 — Core Ledger

Implement:

- People
- Opening balance
- Debit/Give
- Credit/Receive
- Running balance
- Search
- Filters
- Transaction actions
- Recycle Bin

### Phase 4 — Money Manager

Implement:

- Income
- Expense
- Categories
- Cash flow

### Phase 5 — Communication

Implement:

- WhatsApp
- WhatsApp Business
- SMS
- Share
- Payment request
- Custom messages
- Reminders

### Phase 6 — Documents

Implement:

- PDF
- Excel
- Receipt
- Invoice
- Letterhead
- Business card

### Phase 7 — Backup & Security

Implement:

- Google Drive backup
- Restore
- Export/import
- PIN
- Pattern
- Biometrics
- Secure storage

### Phase 8 — Dashboard & Analytics

Implement:

- Financial summaries
- Charts
- Reports
- Filters

### Phase 9 — Multi-Profile

Implement:

- Profile creation
- Profile switching
- Isolated profile data
- Profile-specific settings

### Phase 10 — QA

Run:

- Unit tests
- Database tests
- UI tests
- Build tests
- Navigation tests
- Backup/restore tests
- PDF/Excel validation

Fix all errors before declaring completion.

---

# 52. Critical Business Logic

Do not use UI-calculated balances as the source of truth.

The database must be authoritative.

Create a single reusable balance calculation service.

Example:

```text
Opening Balance = 5,000 You Collect

Debit/Give = 2,000
Credit/Receive = 1,000

Closing Balance = 5,000 + 2,000 - 1,000
                = 6,000 You Collect
```

However, make the exact sign convention configurable internally and cover it with tests. The UI must always show the correct user-facing meaning.

Never duplicate balance formulas across screens.

---

# 53. Data Integrity

Use database transactions for:

- Adding transaction + recalculation
- Editing transaction
- Deleting transaction
- Restoring transaction
- Moving transaction
- Copying transaction
- Converting transaction
- Backup snapshot creation

Every balance-changing operation must be atomic.

---

# 54. Reference-Screenshot Functional Coverage

Use the supplied screenshots as functional UX references for:

- Person list with balances
- Search
- Filter
- Message shortcut
- PDF shortcut
- Transfer/share actions
- Person ledger
- Give/Receive buttons
- Transaction popup menu
- PDF/Excel tools
- Reminder
- Recycle Bin
- Profile & Settings
- Business logo
- QR code
- Stamp
- Denomination options
- Sound/vibration
- WhatsApp settings
- Weekly Excel email
- PDF and Excel report styles

Do not reproduce the exact screenshot pixel-for-pixel. Translate the functionality into a modern M Designs design system.

---

# 55. Reference Feature Checklist

The implementation should cover the functional capabilities represented by the reference application, including:

- Digital Udhar Khata
- Credit/Debit ledger
- Cash book
- Daily accounts
- Income/Expense
- Customer/person ledger
- Opening balance
- Running balance
- Settled account filtering
- Non-settled account filtering
- PDF export
- Excel export
- PDF short report
- PDF full report
- Opening balance in reports
- WhatsApp sharing
- WhatsApp Business support
- SMS sharing
- Payment reminders
- Payment request
- Google Drive backup
- Restore
- Export backup
- Import backup
- Recycle Bin
- Edit entry
- Share entry
- Delete entry
- PDF receipt/invoice
- Duplicate entry
- Convert Credit/Debit
- Move entry
- Copy entry
- Custom messages
- Letterhead
- Business card
- Logo
- QR code
- Stamp
- Sound
- Vibration
- Auto-send message preference
- Weekly Excel report
- Bank holidays
- Categories
- Settings
- Profile
- App lock
- Fingerprint/Face unlock
- PIN
- Pattern
- Zero-balance filter
- Business profile
- Report customization

---

# 56. Improvements Over the Reference

M Designs Khata should improve the experience with:

1. Multi-business profiles.
2. Better modern Material 3 UI.
3. Strong offline-first architecture.
4. Reliable database transactions.
5. Better search and filtering.
6. Better report customization.
7. Attachment support.
8. Payment-mode tracking.
9. Income/expense analytics.
10. Data integrity checks.
11. Safer backup/restore.
12. Profile-specific backups.
13. Dark mode.
14. Accessibility.
15. Better tablet support.
16. Clear audit metadata.
17. Duplicate transaction protection.
18. Better empty/error/loading states.
19. More flexible categories.
20. Modern notification controls.

---

# 57. Ads / Monetization Architecture

Keep monetization modular.

The initial architecture may support:

- Free version
- Premium version
- Optional ad module
- Feature flags

Potential premium features:

- Multiple profiles
- Advanced reports
- Advanced backup
- Business-card templates
- Letterhead templates
- Advanced analytics
- No ads

Do not allow advertisements to interrupt transaction entry or obscure financial data.

Keep the app fully usable for essential ledger operations.

---

# 58. About Screen

Create:

**M Designs Khata**

`A simple digital Khata for Credit, Debit, Income, Expense and Business Records.`

Display:

- M Designs
- Website: mdesignsrmd.com
- App version
- Build number
- Copyright © M Designs
- Privacy Policy
- Terms
- Help & Support

---

# 59. Final Deliverables

Google Antigravity must produce:

1. Complete Android Studio project.
2. Compiling debug APK.
3. Release-ready AAB configuration.
4. Room database implementation.
5. All Compose screens.
6. Navigation.
7. PDF generator.
8. Excel generator.
9. Google Drive backup/restore.
10. App lock.
11. Reminder engine.
12. WhatsApp/SMS sharing.
13. Multi-profile support.
14. Business card generator.
15. Letterhead generator.
16. Unit tests.
17. UI tests for critical flows.
18. README.
19. Privacy/security documentation.
20. Database migration documentation.

---

# 60. Definition of Done

Do not declare the project complete merely because the screens exist.

The project is complete only when:

- All major buttons work.
- All menus work.
- All CRUD operations work.
- Balances are mathematically correct.
- PDF totals match the database.
- Excel totals match the database.
- Backup and restore have been tested.
- Deleted data can be restored.
- App lock works.
- Notifications work.
- WhatsApp/SMS sharing works through supported Android intents.
- No network is required for ordinary ledger operations.
- No placeholder buttons remain.
- No fake success messages remain.
- No TODO implementation remains in critical features.
- No crashes occur during normal workflows.
- App survives rotation/configuration changes.
- App handles empty databases gracefully.
- App handles large transaction datasets.
- Accessibility labels exist for critical controls.
- Dark mode works.
- Multi-profile data is isolated.
- Generated documents contain the configured M Designs business information.
- Build succeeds without compilation errors.

---

# 61. Development Priority

If implementation must be staged, use this priority:

### P0 — Must Have

- Splash
- Onboarding
- Business profile
- Person management
- Opening balance
- Give/Debit
- Receive/Credit
- Running balance
- Search
- Filters
- Edit/Delete/Restore
- PDF
- Excel
- WhatsApp/SMS sharing
- Recycle Bin
- Local database

### P1 — High Priority

- Reminders
- Google Drive backup/restore
- App lock
- Income/Expense
- Dashboard
- Custom messages
- Payment request
- Business card
- Letterhead

### P2 — Enhancement

- Multi-profile
- Advanced analytics
- Bank holidays
- Advanced attachment handling
- Premium/ads feature flags
- Advanced customization

---

# 62. Final Antigravity Instruction

Build this as a **real, production-quality Android application**, not a mockup or static prototype.

Start with the database and core business logic, then build the UI on top of tested repositories/use cases.

Every visible action must have a working implementation.

Use the supplied screenshots and reference Play Store application for **feature discovery and workflow understanding only**. The final application must be branded entirely as **M Designs Khata** and use original UI components, colors, icons, copy, layouts, and assets.

Do not copy the reference application's proprietary branding or assets.

The final result should feel like a polished Indian digital ledger product suitable for real shopkeepers and small businesses.

**Company:** M Designs  
**Website:** https://mdesignsrmd.com  
**Product:** M Designs Khata – Credit Debit
