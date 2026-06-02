import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.livestockguardian"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.livestockguardian"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) {
            localFile.inputStream().use { stream -> localProperties.load(stream) }
        }
        val geminiKey = localProperties.getProperty("GEMINI_API_KEY", "") ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"${geminiKey.replace("\"", "\\\"")}\"")

        val biometricUrl = localProperties.getProperty(
                "BIOMETRIC_BASE_URL",
                "https://livestock-guardian-biometric.onrender.com/"
        ) ?: "https://livestock-guardian-biometric.onrender.com/"
        val biometricKey = localProperties.getProperty(
                "BIOMETRIC_API_KEY",
                "LG_3ca24a88955994ccbcc8b4285626fcb22f3f01bcad3f07cc51d077b19b9f75c8"
        ) ?: ""
        buildConfigField("String", "BIOMETRIC_BASE_URL", "\"${biometricUrl.replace("\"", "\\\"")}\"")
        buildConfigField("String", "BIOMETRIC_API_KEY", "\"${biometricKey.replace("\"", "\\\"")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Correct location for java toolchain configuration
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.swiperefresh)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // CameraX
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}