
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gbenga.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gbenga.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    flavorDimensions += "edition"

    productFlavors {
        create("free") {
            dimension = "edition"
            applicationIdSuffix = ".free"
            versionNameSuffix = "-free"

            buildConfigField(
                "boolean",
                "IS_PRO",
                "false"
            )

            resValue(
                "string",
                "app_name",
                "GBENGA Free"
            )
        }

        create("pro") {
            dimension = "edition"
            applicationIdSuffix = ".pro"
            versionNameSuffix = "-pro"

            buildConfigField(
                "boolean",
                "IS_PRO",
                "true"
            )

            resValue(
                "string",
                "app_name",
                "GBENGA Pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }
}
