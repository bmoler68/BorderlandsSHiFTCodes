/**
 * Project settings and configuration for the Borderlands SHiFT Codes project
 * 
 * This file configures the project structure, plugin management, and dependency
 * resolution settings. It serves as the central configuration point for:
 * - Plugin repository configuration and management
 * - Dependency repository configuration
 * - Project module structure and naming
 * - Build system configuration
 * 
 * The settings ensure that all build tools and dependencies are properly
 * resolved from trusted repositories and that the project structure is
 * correctly defined for the build system.
 */

// Plugin management configuration for build tools and plugins
pluginManagement {
    repositories {
        // Google's Maven repository for Android and Google plugins
        google {
            content {
                // Include only Android and Google-related plugin groups
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // Maven Central for general Java/Kotlin plugins
        mavenCentral()
        // Gradle Plugin Portal for Gradle-specific plugins
        gradlePluginPortal()
    }
}

// Dependency resolution management for project dependencies
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    // Enforce centralized dependency management - fail if projects define their own repos
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        // Google's Maven repository for Android libraries
        google()
        // Maven Central for general Java/Kotlin libraries
        mavenCentral()
    }
}

// Project name displayed in IDEs and build output
rootProject.name = "Borderlands SHiFT Codes"
// Include the main application module
include(":app")
