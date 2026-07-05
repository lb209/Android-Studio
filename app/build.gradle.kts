plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.kotlin"

    // Yahan hum ne compileSdk ko badal kar 37 kar diya hai
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.kotlin"
        minSdk = 24
        targetSdk = 36 // Isay abhi 36 hi rehne dein, koi masla nahi hai
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}