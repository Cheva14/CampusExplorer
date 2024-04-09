plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.cheva.campusexplorer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cheva.campusexplorer"
        minSdk = 28
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
    buildFeatures {
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
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

    implementation(libs.androidx.activity.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.recyclerview)
    implementation(libs.kotlin.faker)
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries
    // Firebase Authentication and Firestore
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-auth-ktx")
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Lifecycle & coroutines
    implementation(libs.androidx.lifecycle.livedata.ktx)

// These two are mandatory
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    // Only when you need to debug the HTTP traffic
//    implementation ("com.squareup.retrofit2:logging-interceptor:2.9.0")

    implementation(libs.logging.interceptor)
    implementation(libs.picasso)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.play.services.maps)
}

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}