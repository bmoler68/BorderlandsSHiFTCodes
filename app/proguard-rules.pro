# ProGuard rules for the Borderlands SHiFT Codes Android application
# 
# This file contains ProGuard configuration rules that control code obfuscation,
# optimization, and shrinking during the release build process. These rules ensure
# that critical application classes and methods are preserved while optimizing
# the final APK size and obfuscating code for security.
# 
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# =============================================================================
# DEBUGGING AND DEVELOPMENT SUPPORT
# =============================================================================

# Keep source file and line number information for debugging stack traces
# This allows developers to see meaningful stack traces in crash reports
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name for security
# This prevents exposing internal file structure in the APK
-renamesourcefileattribute SourceFile

# =============================================================================
# APPLICATION-SPECIFIC CLASS PRESERVATION
# =============================================================================

# Keep WebView JavaScript interface classes
# These classes are used for web content integration and must be preserved
-keepclassmembers class com.brianmoler.borderlandsshiftcodes.ui.components.** {
    public *;
}

# Keep data classes
# ShiftCode data model classes must be preserved for data serialization
-keep class com.brianmoler.borderlandsshiftcodes.data.ShiftCode {
    *;
}

# Keep Room database classes
# Room database and entity classes must be preserved for database operations
-keep class com.brianmoler.borderlandsshiftcodes.data.ShiftCodeDatabase {
    *;
}

# Keep Room entity classes
-keep class com.brianmoler.borderlandsshiftcodes.data.ShiftCodeEntity {
    *;
}

# Keep Room DAO classes
-keep class com.brianmoler.borderlandsshiftcodes.data.ShiftCodeDao {
    *;
}

# Keep configuration classes
# AppConfig contains application configuration that must be accessible
-keep class com.brianmoler.borderlandsshiftcodes.config.AppConfig {
    *;
}

# Keep ViewModel classes
# ViewModels are essential for UI state management and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.ui.ShiftCodeViewModel {
    *;
}

# Keep repository classes
# Repository classes handle data operations and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.data.ShiftCodeRepository {
    *;
}

# Keep local repository classes
-keep class com.brianmoler.borderlandsshiftcodes.data.LocalShiftCodeRepository {
    *;
}

# Keep remote repository classes
-keep class com.brianmoler.borderlandsshiftcodes.data.RemoteShiftCodeRepository {
    *;
}

# Keep utility classes
# Utility classes provide essential functionality and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.util.ClipboardUtil {
    *;
}

# Keep sync service classes
# SyncService handles centralized sync operations and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.sync.SyncService {
    *;
}

# Keep notification classes
# NotificationManager handles notification display and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.notification.NotificationManager {
    *;
}

# Keep WorkManager classes
# WorkManager classes handle background sync and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.work.SyncWorker {
    *;
}

-keep class com.brianmoler.borderlandsshiftcodes.work.SyncScheduler {
    *;
}

# Keep MainActivity
# MainActivity is the entry point of the application and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.MainActivity {
    *;
}

# Keep Application class
# Application class handles app initialization and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.BorderlandsShiftCodesApplication {
    *;
}

# Keep Compose UI classes
# All UI-related classes must be preserved for Compose to function properly
-keep class com.brianmoler.borderlandsshiftcodes.ui.** {
    *;
}

# Keep UI components
-keep class com.brianmoler.borderlandsshiftcodes.ui.components.** {
    *;
}

# Keep UI constants
-keep class com.brianmoler.borderlandsshiftcodes.ui.constants.** {
    *;
}

# Keep UI screen classes
# Note: ShiftCodeScreen is a Composable function, not a class, so no keep rule needed

# Keep theme classes
# Theme classes define the application's visual appearance and must be preserved
-keep class com.brianmoler.borderlandsshiftcodes.ui.theme.** {
    *;
}

# =============================================================================
# THIRD-PARTY LIBRARY RULES
# =============================================================================

# OkHttp network library rules
# Suppress warnings for OkHttp and related libraries to avoid build noise
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Kotlin coroutines preservation
# Keep essential coroutine classes for asynchronous operations
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Room database preservation
# Keep Room database classes and annotations
-keep class androidx.room.** { *; }
-keepclassmembers class androidx.room.** { *; }

# Keep Room entity classes and their fields
-keep @androidx.room.Entity class * {
    *;
}

# Keep Room DAO classes and their methods
-keep @androidx.room.Dao class * {
    *;
}

# Keep Room database classes
-keep @androidx.room.Database class * {
    *;
}

# =============================================================================
# JETPACK COMPOSE RULES
# =============================================================================

# Compose framework preservation
# All Compose classes must be preserved for the UI to function
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Material 3 design system preservation
# Material 3 components must be preserved for the design system to work
-keep class androidx.compose.material3.** { *; }
-keepclassmembers class androidx.compose.material3.** { *; }

# =============================================================================
# BUILD OPTIMIZATION RULES
# =============================================================================

# Remove logging in release builds
# Log statements are removed to improve performance and reduce APK size
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Code optimization settings
# Enable aggressive optimization while preserving essential functionality
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Code removal settings
# Remove unused code and optimize class naming for smaller APK size
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# =============================================================================
# ANDROID FRAMEWORK PRESERVATION
# =============================================================================

# Keep native methods
# Native methods must be preserved for platform integration
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enum values
# Enum values are used for type safety and must be preserved
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
# Parcelable classes are used for data serialization and must be preserved
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
# Serializable classes are used for data persistence and must be preserved
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# =============================================================================
# RESOURCE PRESERVATION
# =============================================================================

# Keep R classes
# R classes contain resource references and must be preserved
-keep class **.R$* {
    public static <fields>;
}

# Keep resource names
# Resource names are used for dynamic resource access and must be preserved
-keepclassmembers class **.R$* {
    public static <fields>;
}