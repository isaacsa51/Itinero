// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    id("com.google.gms.google-services") version "4.4.3" apply false
}

// Global configuration to handle kotlinx-metadata-jvm version compatibility
allprojects {
    configurations.all {
        resolutionStrategy {
            // Force kotlinx-metadata-jvm to version 2.0.0 to fix lint compatibility issues
            force("org.jetbrains.kotlinx:kotlinx-metadata-jvm:2.0.0")
        }
    }
}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}