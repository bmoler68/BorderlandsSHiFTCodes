package com.brianmoler.borderlandsshiftcodes

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Example instrumented tests for Android platform testing
 * 
 * This test class demonstrates how to write instrumented tests that run
 * on actual Android devices or emulators. Instrumented tests are useful for:
 * - Testing Android-specific functionality and APIs
 * - Validating behavior that depends on the Android runtime
 * - Testing integration with system services
 * - Verifying platform-specific behavior
 * 
 * These tests run on the device/emulator and have access to the full
 * Android framework, making them suitable for testing components that
 * cannot be easily mocked in unit tests.
 * 
 * Note: These tests are primarily for demonstration purposes and do not
 * test actual application functionality. Real application instrumented
 * tests should be written in dedicated test classes for specific components.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    /**
     * Tests that the application package name is correct
     * 
     * This test verifies that the application context can be retrieved
     * and that the package name matches the expected value. This is
     * important for ensuring that the test is running in the correct
     * application context.
     * 
     * The test demonstrates how to access the InstrumentationRegistry
     * to get the target application context for testing purposes.
     */
    @Test
    fun useAppContext() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When & Then
        assertEquals("com.brianmoler.borderlandsshiftcodes", appContext.packageName)
    }

    /**
     * Tests that the application context is not null
     * 
     * This test verifies that the InstrumentationRegistry can successfully
     * provide access to the target application context. This is a basic
     * sanity check to ensure the testing environment is properly set up.
     * 
     * The test demonstrates basic null checking and context validation.
     */
    @Test
    fun appContextIsNotNull() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When & Then
        assertNotNull("Application context should not be null", appContext)
    }

    /**
     * Tests that the application context has the correct type
     * 
     * This test verifies that the retrieved context is of the expected
     * type (android.content.Context). This ensures that the testing
     * framework is providing the correct type of context object.
     * 
     * The test demonstrates type checking and validation of test dependencies.
     */
    @Test
    fun appContextIsCorrectType() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When & Then
        assertTrue("Application context should be an instance of Context", 
                  appContext is android.content.Context)
    }

    /**
     * Tests that the instrumentation registry is accessible
     * 
     * This test verifies that the InstrumentationRegistry can be accessed
     * and provides the expected instrumentation object. This is important
     * for ensuring that the testing framework is properly initialized.
     * 
     * The test demonstrates basic framework validation.
     */
    @Test
    fun instrumentationRegistryIsAccessible() {
        // Given
        val instrumentation = InstrumentationRegistry.getInstrumentation()

        // When & Then
        assertNotNull("Instrumentation should not be null", instrumentation)
        assertTrue("Instrumentation should be an instance of Instrumentation", 
                  instrumentation is android.app.Instrumentation)
    }

    /**
     * Tests that the target context package name is not empty
     * 
     * This test verifies that the target application context has a valid,
     * non-empty package name. This is important for ensuring that the
     * application is properly configured and identifiable.
     * 
     * The test demonstrates validation of application metadata.
     */
    @Test
    fun targetContextPackageNameIsNotEmpty() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val packageName = appContext.packageName

        // When & Then
        assertNotNull("Package name should not be null", packageName)
        assertTrue("Package name should not be empty", packageName.isNotEmpty())
        assertTrue("Package name should contain at least one dot", packageName.contains("."))
    }

    /**
     * Tests that the application context can access resources
     * 
     * This test verifies that the application context can successfully
     * access application resources. This is important for ensuring that
     * the testing environment has proper access to application assets.
     * 
     * The test demonstrates resource access validation.
     */
    @Test
    fun appContextCanAccessResources() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When & Then
        assertNotNull("Resources should not be null", appContext.resources)
        assertNotNull("Assets should not be null", appContext.assets)
    }

    /**
     * Tests that the application context has the correct application info
     * 
     * This test verifies that the application context can provide
     * application information and that the package name in the application
     * info matches the context package name.
     * 
     * The test demonstrates application metadata validation.
     */
    @Test
    fun appContextHasCorrectApplicationInfo() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val applicationInfo = appContext.applicationInfo

        // When & Then
        assertNotNull("Application info should not be null", applicationInfo)
        assertEquals("Package name should match application info package name",
                    appContext.packageName, applicationInfo.packageName)
    }
}