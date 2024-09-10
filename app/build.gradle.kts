plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.secureride"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.secureride"
        minSdk = 23
        targetSdk = 34 // Updated to match compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    repositories {
        google()
        mavenCentral() // Removed jcenter()
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Mapping library (ensure only one mapping library is used)
    implementation("org.osmdroid:osmdroid-android:6.1.11")
    implementation("com.squareup.okhttp3:okhttp:4.9.2") // For HTTP requests
    implementation ("com.google.maps.android:android-maps-utils:2.2.3") // For decoding polyline

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
