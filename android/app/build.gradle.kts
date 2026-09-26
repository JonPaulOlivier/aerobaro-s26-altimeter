plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.aerobaro.s26"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aerobaro.s26"
        minSdk = 29
        targetSdk = 35
        versionCode = 100
        versionName = "1.0.0-S26"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = file("aerobaro-release.keystore")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = "aerobaro2026"
                keyAlias = "aerobaro_s26"
                keyPassword = "aerobaro2026"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            val keystoreFile = file("aerobaro-release.keystore")
            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.webkit:webkit:1.12.1")
    implementation("androidx.activity:activity-ktx:1.9.3")
}
