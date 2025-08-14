import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "2.2.0"
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val apiBaseUrl: String = localProperties.getProperty("API_BASE_URL")
    ?: throw GradleException("API_BASE_URL must be defined in local.properties")
val websocketUrl: String =
    localProperties.getProperty("WEBSOCKET_BASE_URL") ?: apiBaseUrl.replace("http", "ws")
val websocketPath: String = localProperties.getProperty("WEBSOCKET_PATH") ?: "/ws"

android {
    namespace = "com.serranoie.app.feature.chat.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "WEBSOCKET_BASE_URL", "\"$websocketUrl\"")
        buildConfigField("String", "WEBSOCKET_PATH", "\"$websocketPath\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        debug {
            buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
            buildConfigField("String", "WEBSOCKET_BASE_URL", "\"$websocketUrl\"")
            buildConfigField("String", "WEBSOCKET_PATH", "\"$websocketPath\"")
        }
    }

    packaging {
        resources {
            pickFirsts += "META-INF/mailcap.default"
            pickFirsts += "META-INF/mimetypes.default"

            excludes += "xsd/catalog.xml"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

configurations.all {
    exclude(group = "com.sun.activation", module = "javax.activation")
}

dependencies {
    implementation(project(":feature:chat:domain"))
    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Kotlin coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // Kotlin serialization
    implementation(libs.kotlinx.serialization.json)

    // Koin for dependency injection
    implementation(libs.koin.android)
    implementation(libs.koin.core)

    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Ktor client with WebSocket support
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Unit test helpers
    testImplementation(libs.mockk)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.turbine)
    testImplementation(kotlin("test"))
}