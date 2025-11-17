package com.brianmoler.borderlandsshiftcodes

import org.junit.Test
import org.junit.Assert.*

/**
 * Example unit tests demonstrating basic testing patterns
 * 
 * This test class serves as a template and reference for writing unit tests
 * in the Borderlands SHiFT Codes application. It demonstrates:
 * - Basic assertion usage with JUnit
 * - Simple mathematical operations for testing
 * - Proper test method naming conventions
 * - Test structure and organization patterns
 * 
 * While these tests are simple examples, they provide a foundation for
 * more complex testing scenarios throughout the application.
 * 
 * Note: These tests are primarily for demonstration purposes and do not
 * test actual application functionality. Real application tests should
 * be written in dedicated test classes for specific components.
 */
class ExampleUnitTest {

    /**
     * Tests basic addition operation
     * 
     * This simple test demonstrates the basic structure of a unit test:
     * - Clear test method name using backtick notation
     * - Simple assertion to verify expected behavior
     * - Minimal setup and teardown requirements
     * 
     * This pattern can be extended for testing actual application logic.
     */
    @Test
    fun `addition is correct`() {
        // Given
        val a = 2
        val b = 3
        val expected = 5

        // When
        val result = a + b

        // Then
        assertEquals(expected, result)
    }

    /**
     * Tests basic multiplication operation
     * 
     * Demonstrates testing mathematical operations and verifying
     * that basic arithmetic functions work as expected.
     * 
     * This test shows how to structure assertions for simple
     * mathematical operations that might be used in the application.
     */
    @Test
    fun `multiplication is correct`() {
        // Given
        val a = 4
        val b = 5
        val expected = 20

        // When
        val result = a * b

        // Then
        assertEquals(expected, result)
    }

    /**
     * Tests string concatenation
     * 
     * Demonstrates testing string operations, which are commonly
     * used in applications for building messages, formatting text,
     * and constructing user interface content.
     * 
     * This test shows how to verify string manipulation logic.
     */
    @Test
    fun `string concatenation works`() {
        // Given
        val firstName = "John"
        val lastName = "Doe"
        val expected = "John Doe"

        // When
        val result = "$firstName $lastName"

        // Then
        assertEquals(expected, result)
    }

    /**
     * Tests boolean logic operations
     * 
     * Demonstrates testing boolean expressions and logical operations
     * that are commonly used in conditional statements throughout
     * applications for decision-making and flow control.
     * 
     * This test shows how to verify logical operations work correctly.
     */
    @Test
    fun `boolean logic is correct`() {
        // Given
        val a = true
        val b = false

        // When & Then
        assertTrue(a && a) // true && true = true
        assertFalse(a && b) // true && false = false
        assertTrue(a || b) // true || false = true
        assertFalse(b || b) // false || false = false
        assertTrue(!b) // !false = true
        assertFalse(!a) // !true = false
    }

    /**
     * Tests null safety operations
     * 
     * Demonstrates testing null safety features that are important
     * in Kotlin applications to prevent null pointer exceptions
     * and ensure robust error handling.
     * 
     * This test shows how to verify null safety mechanisms work correctly.
     */
    @Test
    fun `null safety operations work`() {
        // Given
        val nullableString: String? = null
        val nonNullString: String = "Hello"

        // When & Then
        // Safe call operator
        assertNull(nullableString?.length)
        assertEquals(5, nonNullString?.length)

        // Elvis operator
        val length1 = nullableString?.length ?: 0
        val length2 = nonNullString?.length ?: 0
        
        assertEquals(0, length1)
        assertEquals(5, length2)

        // Safe cast
        val safeCast1 = nullableString as? String
        val safeCast2 = nonNullString as? String
        
        assertNull(safeCast1)
        assertEquals("Hello", safeCast2)
    }

    /**
     * Tests collection operations
     * 
     * Demonstrates testing collection operations that are commonly
     * used in applications for managing lists, filtering data,
     * and transforming collections of objects.
     * 
     * This test shows how to verify collection manipulation logic.
     */
    @Test
    fun `collection operations work`() {
        // Given
        val numbers = listOf(1, 2, 3, 4, 5)

        // When & Then
        // Basic operations
        assertEquals(5, numbers.size)
        assertEquals(1, numbers.first())
        assertEquals(5, numbers.last())
        assertTrue(numbers.contains(3))
        assertFalse(numbers.contains(6))

        // Filtering
        val evenNumbers = numbers.filter { it % 2 == 0 }
        assertEquals(2, evenNumbers.size)
        assertTrue(evenNumbers.all { it % 2 == 0 })

        // Mapping
        val doubledNumbers = numbers.map { it * 2 }
        assertEquals(listOf(2, 4, 6, 8, 10), doubledNumbers)

        // Reduction
        val sum = numbers.reduce { acc, num -> acc + num }
        assertEquals(15, sum)
    }

    /**
     * Tests exception handling
     * 
     * Demonstrates testing exception scenarios and verifying that
     * exceptions are thrown when expected, which is important for
     * testing error conditions and edge cases in applications.
     * 
     * This test shows how to verify exception handling works correctly.
     */
    @Test
    fun `exception handling works`() {
        // Given
        val numbers = listOf(1, 2, 3)

        // When & Then
        // Test that accessing invalid index throws exception
        assertThrows(IndexOutOfBoundsException::class.java) {
            numbers[5] // This should throw IndexOutOfBoundsException
        }

        // Test that division by zero throws exception
        assertThrows(ArithmeticException::class.java) {
            val result = 10 / 0 // This should throw ArithmeticException
        }
    }
}