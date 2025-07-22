import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt")
}

// Function to get current Git branch
fun getGitBranch(): String {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
            .directory(rootDir)
            .start()
        process.inputStream.bufferedReader().use { it.readText().trim() }
    } catch (e: Exception) {
        "unknown"
    }
}

fun getFlavorFromBranch(): String {
    val branch = getGitBranch()
    return when {
        branch == "master" || branch == "main" -> "production"
        branch == "develop" -> "beta"
        else -> "alpha"
    }
}

fun getVersionSuffix(flavor: String): String {
    return when (flavor) {
        "alpha" -> "-alpha"
        "beta" -> "-beta"
        else -> ""
    }
}

fun getLocalProperty(key: String): String? {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }
    return properties.getProperty(key)
}

android {
    namespace = "com.serranoie.app.itinero"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.serranoie.app.itinero"
        minSdk = 26
        targetSdk = 35
        versionCode = 5
        versionName = "0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Define product flavors
    flavorDimensions += "version"
    productFlavors {
        create("alpha") {
            dimension = "version"
            applicationIdSuffix = ".alpha"
            versionNameSuffix = "-alpha"

            buildConfigField("String", "ENVIRONMENT", "\"alpha\"")
            buildConfigField("String", "BASE_URL", "\"${getLocalProperty("API_BASE_URL") ?: "https://alpha-api.itinero.com"}\"")

            resValue("string", "app_name", "Itinero Testing")
        }

        create("beta") {
            dimension = "version"
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta"

            buildConfigField("String", "ENVIRONMENT", "\"beta\"")
            buildConfigField("String", "BASE_URL", "\"https://beta-api.itinero.com\"")

            resValue("string", "app_name", "Itinero Beta Testing")
        }

        create("production") {
            dimension = "version"

            // TODO: Add production-specific configurations
            buildConfigField("String", "ENVIRONMENT", "\"production\"")
            buildConfigField("String", "BASE_URL", "\"https://api.itinero.com\"")

            resValue("string", "app_name", "Itinero")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            buildConfigField("String", "ENVIRONMENT", "\"debug\"") // TODO: Using debug signing for now

            applicationVariants.all {
                outputs
                    .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
                    .all { output ->
                        val flavorDisplayName = when (flavorName) {
                            "alpha" -> "Alpha"
                            "beta" -> "Beta"
                            "production" -> ""
                            else -> flavorName.replaceFirstChar { it.uppercase() }
                        }
                        val appName =
                            if (flavorDisplayName.isEmpty()) "Itinero" else "Itinero $flavorDisplayName"
                        output.outputFileName = "$appName-v${defaultConfig.versionName}.apk"
                        false
                    }
            }
        }

        release {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug") // TODO: Using debug signing for now

            applicationVariants.all {
                outputs
                    .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
                    .all { output ->
                        val flavorDisplayName = when (flavorName) {
                            "alpha" -> "Alpha"
                            "beta" -> "Beta"
                            "production" -> ""
                            else -> flavorName.replaceFirstChar { it.uppercase() }
                        }
                        val appName =
                            if (flavorDisplayName.isEmpty()) "Itinero" else "Itinero $flavorDisplayName"
                        output.outputFileName = "$appName-v${defaultConfig.versionName}.apk"
                        false
                    }
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/*"
            excludes += "xsd/catalog.xml"
        }
    }

    configurations {
        implementation {
            exclude(group = "com.sun.activation", module = "javax.activation")
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
        buildConfig = true
    }
}

dependencies {

    // Project modules
    implementation(project(":designsystem-lib"))
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
    implementation(project(":core:settings"))
    implementation(project(":di"))
    implementation(project(":feature:home"))
    implementation(project(":feature:onboard"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:itinerary"))
    implementation(project(":feature:expenses"))
    implementation(project(":feature:expenses:data"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:settings"))

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

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.0")

    // Camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.accompanist.permissions)

    // Koin
    implementation("io.insert-koin:koin-android:4.0.3")
    implementation("io.insert-koin:koin-core:4.0.3")
    implementation("io.insert-koin:koin-androidx-compose:4.0.3")

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

    // DataStore preferences
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Material Kolor
    implementation(libs.materialKolor)

    // ML Kit and QR generator
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.google.zxing:core:3.5.1")
}

kapt {
    correctErrorTypes = true
}
