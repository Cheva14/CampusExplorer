// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.secrets.gradle.plugin)
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.android.library") version "8.3.1" apply false
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin) apply false
}