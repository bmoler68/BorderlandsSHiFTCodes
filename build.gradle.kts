/**
 * Root-level build configuration for the Borderlands SHiFT Codes project
 * 
 * This file configures build plugins and settings that apply to all
 * modules in the project. It serves as the central configuration point
 * for:
 * - Plugin version management through version catalogs
 * - Common build settings across all modules
 * - Project-wide plugin configuration
 * 
 * The plugins are applied with 'apply false' to prevent them from
 * being applied to the root project itself, allowing them to be
 * selectively applied to individual modules as needed.
 */

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android application plugin - provides Android-specific build capabilities
    alias(libs.plugins.android.application) apply false
    // Kotlin Android plugin - provides Kotlin language support for Android
    alias(libs.plugins.kotlin.android) apply false
    // Kotlin Compose plugin - provides Compose compiler support and optimizations
    alias(libs.plugins.kotlin.compose) apply false
}