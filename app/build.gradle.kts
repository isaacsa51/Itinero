plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.serranoie.app.itinero"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.serranoie.app.itinero"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Project modules
    implementation(project(":designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))
    implementation(project(":di"))
    implementation(project(":feature:home"))
    implementation(project(":feature:onboard"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:itinerary"))
    implementation(project(":feature:expenses"))
    implementation(project(":feature:chat"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.accompanist.permissions)

    // Hilt
    implementation("com.google.dagger:hilt-android:2.55")
    annotationProcessor("com.google.dagger:hilt-compiler:2.55")
        // For instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.55")
    androidTestAnnotationProcessor("com.google.dagger:hilt-compiler:2.55")
        // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:2.55")
    testAnnotationProcessor("com.google.dagger:hilt-compiler:2.55")

    // Compose navigation
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // Compose animation
    implementation("androidx.compose.animation:animation:1.8.1")

    // Compose Foundation
    implementation("androidx.compose.foundation:foundation:1.8.1")

    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    testImplementation("androidx.room:room-testing:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.7")

    // Coil image loader
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Lifecycle viewmodel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")

    // ML Kit and QR generator
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.google.zxing:core:3.5.1")
}

kapt {
    correctErrorTypes = true
}