/**
 * Build configuration for the Borderlands SHiFT Codes Android application
 * 
 * This file configures the build process, dependencies, and build variants
 * for the Android application. It includes:
 * - Plugin configuration for Android and Kotlin
 * - Build configuration for different Android versions
 * - Dependency management for all required libraries
 * - Build type configuration for debug and release builds
 * - Testing framework configuration
 * 
 * The configuration is optimized for modern Android development with
 * Jetpack Compose and follows current best practices.
 */

plugins {
    // Android application plugin for building APKs
    alias(libs.plugins.android.application)
    // Kotlin Android plugin for Kotlin language support
    alias(libs.plugins.kotlin.android)
    // Kotlin Compose plugin for Compose compiler support
    alias(libs.plugins.kotlin.compose)
    // Kotlin annotation processing plugin for Room
    kotlin("kapt")
}

android {
    // Application namespace for the package
    namespace = "com.brianmoler.borderlandsshiftcodes"
    // Target Android SDK version for compilation
    compileSdk = 36

    defaultConfig {
        // Unique application identifier for the Play Store
        applicationId = "com.brianmoler.borderlandsshiftcodes"
        // Minimum Android version supported (Android 7.0)
        minSdk = 24
        // Target Android version for optimization
        targetSdk = 36
        // Internal version number for Play Store updates
        versionCode = 10600
        // User-visible version name
        versionName = "1.6.0"

        // Test runner for instrumented tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Enable vector drawables for scalable graphics
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        // Release build configuration for production
        release {
            // Enable code minification for smaller APK size
            isMinifyEnabled = true
            // Enable resource shrinking for smaller APK size
            isShrinkResources = true
            // ProGuard rules for code obfuscation and optimization
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        // Debug build configuration for development
        debug {
            // Enable debugging capabilities
            isDebuggable = true
            // Add debug suffix to package name for side-by-side installation
            applicationIdSuffix = ".debug"
        }
    }
    
    compileOptions {
        // Java version compatibility
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // Enable core library desugaring for Java 8+ APIs on older Android versions
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlinOptions {
        // JVM target version for Kotlin compilation
        jvmTarget = "11"
    }
    
    // KAPT configuration
    kapt {
        // Correct arguments for Room
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
        // Use correct annotation processor
        correctErrorTypes = true
        // Map specific annotation types to avoid conflicts
        mapDiagnosticLocations = true
    }
    
    buildFeatures {
        // Enable Jetpack Compose build features
        compose = true
    }
    
    // Custom APK naming configuration
    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "${rootProject.name}-${variant.buildType.name}-v${variant.versionName}.apk"
                output.outputFileName = outputFileName
            }
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)                    // Kotlin extensions for Android core
    implementation(libs.androidx.lifecycle.runtime.ktx)       // Lifecycle management
    implementation(libs.androidx.activity.compose)            // Compose Activity integration
    
    // Jetpack Compose dependencies
    implementation(platform(libs.androidx.compose.bom))       // Compose BOM for version management
    implementation(libs.androidx.ui)                          // Compose UI foundation
    implementation(libs.androidx.ui.graphics)                 // Compose graphics and drawing
    implementation(libs.androidx.material3)                   // Material Design 3 components
    implementation(libs.androidx.compose.material.icons.extended)  // Extended Material Icons
    
    // Network and data processing dependencies
    implementation(libs.okhttp)                               // HTTP client for network requests
    implementation(libs.kotlinx.coroutines.android)           // Coroutines for asynchronous operations
    
    // Room database dependencies
    implementation("androidx.room:room-runtime:2.8.3")        // Room database runtime
    implementation("androidx.room:room-ktx:2.8.3")           // Room Kotlin extensions
    kapt("androidx.room:room-compiler:2.8.3")                // Room annotation processor
    
    // WorkManager for background sync
    implementation("androidx.work:work-runtime-ktx:2.11.0")     // WorkManager for background tasks
    
    // Core library desugaring for Java 8+ APIs on older Android versions
    // This allows using modern Java APIs on devices running Android 7.0+
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    
    // Testing dependencies
    testImplementation(libs.junit)                           // JUnit for unit testing
    testImplementation(libs.robolectric)                     // Robolectric for Android framework testing
    testImplementation("org.mockito:mockito-core:5.20.0")     // Mockito for mocking in tests
    testImplementation("org.mockito:mockito-inline:5.2.0")   // Mockito inline for final classes
    
    // Android instrumented testing dependencies
    androidTestImplementation(libs.androidx.junit)           // Android JUnit extensions
    androidTestImplementation(platform(libs.androidx.compose.bom))  // Compose BOM for tests
    // Add main module dependencies that androidTest needs
    androidTestImplementation(libs.androidx.core.ktx)        // Core KTX for tests
    androidTestImplementation(libs.androidx.activity.compose) // Compose Activity for tests
    androidTestImplementation(libs.androidx.material3)       // Material 3 for tests
    
    // Debug-only dependencies for development tools
    debugImplementation(libs.androidx.ui.tooling)            // Compose UI tooling
    debugImplementation(libs.androidx.ui.test.manifest)      // Test manifest for Compose
}