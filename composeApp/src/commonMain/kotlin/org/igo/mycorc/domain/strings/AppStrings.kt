package org.igo.mycorc.domain.strings

interface AppStrings {
    // Auth Screen
    val loginTitle: String
    val authCardTitle: String
    val emailLabel: String
    val passwordLabel: String
    val loginButton: String
    val noAccountText: String
    val registerLink: String

    // Dashboard Screen
    val dashboardTitle: String
    val syncWithServer: String
    val addButtonTooltip: String
    val noRecordsMessage: String
    val weightLabel: String
    val coalLabel: String
    val fillAllFieldsWarning: String
    val sendToRegistration: String
    val sentToRegistration: String
    val approved: String
    val rejected: String

    // Create Note Screen
    val readOnlyTitle: String
    val editTitle: String
    val createNewTitle: String
    val biomassWeightLabel: String
    val coalWeightLabel: String
    val descriptionSection: String
    val enterDescription: String
    val photoSection: String
    val noPhotoPlaceholder: String
    val saveChanges: String
    val saveNote: String
    val photoSaved: String
    val cannotEditError: String
    val alreadySentError: String
    val alreadySentConflictError: String

    // Settings Screen
    val settingsTitle: String
    val themeSection: String
    val systemTheme: String
    val lightTheme: String
    val darkTheme: String
    val languageSection: String
    val languageEn: String
    val languageRu: String
    val languageDe: String

    // Profile Screen
    val profileTitle: String
    val loggedInAs: String
    val loading: String
    val logoutButton: String

    // Navigation
    val packagesNav: String
    val facilitiesNav: String
    val settingsNav: String
    val profileNav: String

    // Common
    val backButtonTooltip: String
    val facilitiesSection: String
}
