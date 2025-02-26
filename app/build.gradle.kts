plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")

}

android {
    namespace = "com.example.attendease"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.attendease"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
buildscript {
    repositories {
        google()
    }
    dependencies {
        val nav_version = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}
dependencies {
    // AndroidX Core library
    implementation(libs.androidx.core.ktx)

    // AppCompat library
    implementation(libs.androidx.appcompat)

    // Material Design library
    implementation(libs.material)

    // AndroidX Activity library
    implementation(libs.androidx.activity)

    // ConstraintLayout library
    implementation(libs.androidx.constraintlayout)

    // Navigation Component libraries
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Firebase Firestore KTX library
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.1")

    // JUnit for unit tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}