plugins {
    id("com.android.application") // ← GEEN versie hier!
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.wiconic.domoticzapp"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.wiconic.domoticzapp"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

    // Devices
    buildConfigField("String", "DEVICE_1", "\"${project.findProperty("DEVICE_1") ?: "GateBell"}\"")
    buildConfigField("String", "DEVICE_2", "\"${project.findProperty("DEVICE_2") ?: "DoorBell"}\"")
    buildConfigField("String", "DEVICE_3", "\"${project.findProperty("DEVICE_3") ?: "FireAlarm"}\"")
    buildConfigField("String", "DEVICE_4", "\"${project.findProperty("DEVICE_4") ?: "LeakDetection"}\"")
    buildConfigField("String", "DEVICE_5", "\"${project.findProperty("DEVICE_5") ?: "IntrusionAlarm"}\"")
    buildConfigField("String", "DEVICE_6", "\"${project.findProperty("DEVICE_6") ?: "GateState"}\"")

    // Messages
    buildConfigField("String", "DEVICE_1_MESSAGE", "\"${project.findProperty("DEVICE_1_MESSAGE") ?: "GATE Bell has been pressed. Who is at the gate?"}\"")
    buildConfigField("String", "DEVICE_2_MESSAGE", "\"${project.findProperty("DEVICE_2_MESSAGE") ?: "DOOR Bell has been pressed. Who is at the door?"}\"")
    buildConfigField("String", "DEVICE_3_MESSAGE", "\"${project.findProperty("DEVICE_3_MESSAGE") ?: "The FIRE alarm has detected FIRE and/or SMOKE !!!"}\"")
    buildConfigField("String", "DEVICE_4_MESSAGE", "\"${project.findProperty("DEVICE_4_MESSAGE") ?: "The WATER detector in the Business Entry has detected water leakage !!!"}\"")
    buildConfigField("String", "DEVICE_5_MESSAGE", "\"${project.findProperty("DEVICE_5_MESSAGE") ?: "The INTRUSION alarm has detected a tripped sensor, which set off the alarm !!!"}\"")
    buildConfigField("String", "DEVICE_6_MESSAGE", "\"${project.findProperty("DEVICE_6_MESSAGE") ?: "The gate state has changed."}\"")

    // Max Cameras
    buildConfigField("int", "MAX_CAMERAS", "${project.findProperty("MAX_CAMERAS") ?: 8}")
}

    buildTypes {
//        release {
        debug {
            isDebuggable = true
            //isMinifyEnabled = false
            //proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // UI Components
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // Architecture Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    
    // Room for local storage
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.picasso:picasso:2.8")
    
    // Location
    implementation("com.google.android.gms:play-services-location:21.1.0")
    
    // Preferences
    implementation("androidx.preference:preference-ktx:1.2.1")
}
