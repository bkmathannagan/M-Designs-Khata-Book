package com.mdesigns.khata.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Lock : Screen("lock")
    object Home : Screen("home")
    object Khata : Screen("khata")
    object Reports : Screen("reports")
    object Money : Screen("money")
    object More : Screen("more")
    object AddPerson : Screen("add_person?personId={personId}") {
        fun createRoute(personId: Long? = null) = if (personId != null) "add_person?personId=$personId" else "add_person"
    }
    object PersonLedger : Screen("person_ledger/{personId}") {
        fun createRoute(personId: Long) = "person_ledger/$personId"
    }
    object BusinessProfile : Screen("business_profile")
    object BusinessCard : Screen("business_card")
    object Letterhead : Screen("letterhead")
    object RecycleBin : Screen("recycle_bin")
    object Reminders : Screen("reminders")
    object Settings : Screen("settings")
    object BackupRestore : Screen("backup_restore")
    object Denomination : Screen("denomination")
    object BankHolidays : Screen("bank_holidays")
    object About : Screen("about")
}
