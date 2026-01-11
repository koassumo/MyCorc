import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // 👇 1. ВКЛЮЧАЕМ DESKTOP (JVM)
    jvm("desktop")


    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            //
            implementation(libs.koin.android)
            // implementation(libs.koin.compose)
            implementation(libs.sqldelight.android)
            implementation(libs.peekaboo.image.picker)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            //
            implementation(compose.materialIconsExtended)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.multiplatform.settings)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.kotlinx.serialization.json)

        }

        // 👇 2. ЗАВИСИМОСТИ ДЛЯ DESKTOP
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                // Библиотека для работы корутин в оконном интерфейсе Java (Swing)
                // Если будет гореть красным - скажи, добавим в toml файл.
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.sqldelight.jvm)
                // здесь нет peekaboo для desktop
            }
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native)
            implementation(libs.peekaboo.image.picker)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            //
            implementation(libs.koin.test)
        }
    }
}

// 👇 НАСТРОЙКА ГЕНЕРАЦИИ БАЗЫ
sqldelight {
    databases {
        create("AppDatabase") {
            // Пакет, где появится сгенерированный класс AppDatabase
            packageName.set("org.igo.mycorc.db")
        }
    }
}

android {
    namespace = "org.igo.mycorc"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.igo.mycorc"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}


// 👇 3. НАСТРОЙКИ ЗАПУСКА ПРИЛОЖЕНИЯ НА ПК
compose.desktop {
    application {
        // Указываем точку входа (MainKt - это файл main.kt, который мы создадим)
        mainClass = "org.igo.mycorc.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MyCorc"
            packageVersion = "1.0.0"
        }
    }
}
