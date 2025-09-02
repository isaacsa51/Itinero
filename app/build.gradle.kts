import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
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

        val props = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) file.inputStream().use(::load)
        }
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] =
            props.getProperty("GOOGLE_MAPS_API_KEY") ?: ""
    }

    flavorDimensions += "version"
    productFlavors {
        create("alpha") {
            dimension = "version"
            applicationIdSuffix = ".alpha"
            versionNameSuffix = "-alpha"

            buildConfigField("String", "ENVIRONMENT", "\"alpha\"")
            buildConfigField("String", "BASE_URL", "\"${getLocalProperty("API_BASE_URL") ?: "https://alpha-api.itinero.com"}\"")

            resValue("string", "app_name", "Itinero Debug")
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
    implementation(project(":feature:chat:data"))
    implementation(project(":feature:chat:domain"))
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
    implementation(libs.androidx.core.splashscreen)

    // Camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.accompanist.permissions)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)

    // Compose navigation
    implementation(libs.androidx.navigation.compose)

    // Compose animation
    implementation(libs.androidx.animation)

    // Compose Foundation
    implementation(libs.androidx.foundation)

    // Material Icons Extended
    implementation(libs.androidx.material.icons.extended.android)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.androidx.room.testing)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // Coil image loader
    implementation(libs.coil.compose)

    // Lifecycle viewmodel
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // DataStore preferences
    implementation(libs.androidx.datastore.preferences)

    // ML Kit and QR generator
    implementation(libs.barcode.scanning)
    implementation(libs.core)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Import the Firebase BoM (see: https://firebase.google.com/docs/android/learn-more#bom)
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))

    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-installations:19.0.0")
}

kapt {
    correctErrorTypes = true
}
