import os

def w(path, content):
    p = os.path.normpath(path)
    os.makedirs(os.path.dirname(p) if os.path.dirname(p) else '.', exist_ok=True)
    with open(p, 'w', encoding='utf-8') as f:
        f.write(content.strip() + '\n')
    print(f'Wrote: {path}')

# 1. gradle.properties
w('gradle.properties', '''
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=false
android.useAndroidX=true
android.enableJetifier=false
android.nonTransitiveRClass=true
kotlin.code.style=official
org.gradle.java.home=C:/Program Files/Java/jdk-17
''')

# 2. local.properties
w('local.properties', '''
sdk.dir=C:\\\\Users\\\\USER\\\\AppData\\\\Local\\\\Android\\\\Sdk
''')

# 3. settings.gradle.kts
w('settings.gradle.kts', '''
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\\\.android.*")
                includeGroupByRegex("com\\\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MDesignsKhata"
include(":app")
''')

# 4. gradle/libs.versions.toml
w('gradle/libs.versions.toml', '''
[versions]
agp = "8.3.2"
kotlin = "1.9.23"
coreKtx = "1.13.1"
lifecycleRuntimeKtx = "2.8.4"
activityCompose = "1.9.1"
composeBom = "2024.06.00"
navigationCompose = "2.7.7"
room = "2.6.1"
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"
datastore = "1.1.1"
coroutines = "1.8.1"
biometric = "1.2.0-alpha05"
workManager = "2.9.1"
coil = "2.6.0"
materialIconsExtended = "1.6.8"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
androidx-hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
androidx-biometric = { group = "androidx.biometric", name = "biometric", version.ref = "biometric" }
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workManager" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
''')

# 5. build.gradle.kts (root)
w('build.gradle.kts', '''
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
}
''')

# 6. app/build.gradle.kts
w('app/build.gradle.kts', '''
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.mdesigns.khata"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mdesigns.khata"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes.add("/META-INF/**")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // DataStore & Security & WorkManager & Coil
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
}

kapt {
    correctErrorTypes = true
}
''')

# 7. app/proguard-rules.pro
w('app/proguard-rules.pro', '''
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @com.google.dagger.* <methods>;
}
-keep class com.mdesigns.khata.** { *; }
''')

# 8. AndroidManifest.xml
w('app/src/main/AndroidManifest.xml', '''<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />

    <application
        android:name=".KhataApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MDesignsKhata"
        tools:targetApi="35">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize"
            android:theme="@style/Theme.MDesignsKhata">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="com.mdesigns.khata.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>

</manifest>
''')

# 9. Resources
w('app/src/main/res/values/strings.xml', '''<resources>
    <string name="app_name">M Designs Khata</string>
    <string name="app_subtitle">Credit • Debit • Udhar • Ledger • Cash Book</string>
    <string name="company_name">M Designs</string>
    <string name="company_website">https://mdesignsrmd.com</string>
    <string name="currency_symbol">₹</string>

    <string name="nav_home">Home</string>
    <string name="nav_khata">Khata</string>
    <string name="nav_reports">Reports</string>
    <string name="nav_money">Money</string>
    <string name="nav_more">More</string>

    <string name="you_collect">You Collect</string>
    <string name="you_give">You Give</string>
    <string name="net_balance">Net Balance</string>
    <string name="settled">Settled</string>
    <string name="credit_receive">Credit / Receive</string>
    <string name="debit_give">Debit / Give</string>
    <string name="income">Income</string>
    <string name="expense">Expense</string>
    <string name="opening_balance">Opening Balance</string>

    <string name="add_person">Add Person</string>
    <string name="add_transaction">Add Entry</string>
    <string name="save">Save</string>
    <string name="cancel">Cancel</string>
    <string name="delete">Delete</string>
    <string name="restore">Restore</string>
    <string name="share">Share</string>
    <string name="payment_request">Payment Request</string>
    <string name="recycle_bin">Recycle Bin</string>
</resources>
''')

w('app/src/main/res/values/colors.xml', '''<resources>
    <color name="primary_teal">#006A60</color>
    <color name="primary_teal_dark">#53DBC9</color>
    <color name="credit_green">#2E7D32</color>
    <color name="debit_red">#D32F2F</color>
    <color name="neutral_gray">#757575</color>
    <color name="surface_light">#FAFDFB</color>
    <color name="surface_dark">#191C1B</color>
</resources>
''')

w('app/src/main/res/values/themes.xml', '''<resources>
    <style name="Theme.MDesignsKhata" parent="android:Theme.Material.Light.NoActionBar">
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
    </style>
</resources>
''')

w('app/src/main/res/xml/file_paths.xml', '''<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <files-path name="khata_docs" path="documents/" />
    <cache-path name="khata_cache" path="reports/" />
    <external-files-path name="khata_external" path="exports/" />
</paths>
''')

w('app/src/main/res/xml/backup_rules.xml', '''<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
    <include domain="database" path="khata_database.db" />
    <include domain="sharedpref" path="user_preferences" />
</full-backup-content>
''')

w('app/src/main/res/xml/data_extraction_rules.xml', '''<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
    <cloud-backup>
        <include domain="database" path="khata_database.db" />
    </cloud-backup>
    <device-transfer>
        <include domain="database" path="khata_database.db" />
    </device-transfer>
</data-extraction-rules>
''')

w('app/src/main/res/drawable/ic_launcher_foreground.xml', '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#006A60"
        android:pathData="M24,20h60c2.2,0 4,1.8 4,4v60c0,2.2 -1.8,4 -4,4H24c-2.2,0 -4,-1.8 -4,-4V24C20,21.8 21.8,20 24,20z" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M34,36h40v6H34zM34,48h40v6H34zM34,60h26v6H34z" />
</vector>
''')

w('app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml', '''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/primary_teal" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
''')

w('app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml', '''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/primary_teal" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
''')

print("Step 1 Completed!")
