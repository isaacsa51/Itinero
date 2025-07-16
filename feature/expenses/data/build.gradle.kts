plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    id("kotlin-kapt")
    kotlin("plugin.serialization") version "2.1.21"
}

android {
    namespace = "com.serranoie.app.feature.expenses.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "11"
    }

    lint {
        lintConfig = file("lint.xml")

        disable.add("FlowOperatorInvokedInComposition")

        disable.add("ComposableFlowOperator")
        disable.add("FlowOperatorInvokedInComposition")
        disable.add("ComposableNaming")
        disable.add("ComposableModifierFactory")
        disable.add("ModifierFactoryExtensionFunction")

        abortOnError = false
        checkReleaseBuilds = false

        warningsAsErrors = false

        checkDependencies = false

        ignoreTestSources = true
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:expenses:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.android.tools.build:gradle:8.1.4")

    // Koin
    implementation("io.insert-koin:koin-android:4.0.3")
    implementation("io.insert-koin:koin-core:4.0.3")
    implementation("io.insert-koin:koin-androidx-compose:4.0.3")

    // Ktor
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.core)
    implementation("io.ktor:ktor-client-resources:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")

    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Datastore preferences
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.1.7")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)
}