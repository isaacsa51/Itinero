package com.serranoie.app.feature.chat.util

import org.junit.Assert.*
import org.junit.Test

class MessageFormatterTest {

    @Test
    fun `symbolPattern should match @username mentions`() {
        // Given
        val text = "Hello @john how are you?"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("@john", matches[0].value)
        assertEquals(6, matches[0].range.first)
        assertEquals(10, matches[0].range.last)
    }

    @Test
    fun `symbolPattern should match bold text with asterisks`() {
        // Given
        val text = "This is *bold* text"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("*bold*", matches[0].value)
    }

    @Test
    fun `symbolPattern should match italic text with underscores`() {
        // Given
        val text = "This is _italic_ text"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("_italic_", matches[0].value)
    }

    @Test
    fun `symbolPattern should match strikethrough text with tildes`() {
        // Given
        val text = "This is ~strikethrough~ text"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("~strikethrough~", matches[0].value)
    }

    @Test
    fun `symbolPattern should match inline code with backticks`() {
        // Given
        val text = "Use the `MyClass.method()` function"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("`MyClass.method()`", matches[0].value)
    }

    @Test
    fun `symbolPattern should match HTTP URLs`() {
        // Given
        val text = "Visit http://example.com for more info"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("http://example.com", matches[0].value)
    }

    @Test
    fun `symbolPattern should match HTTPS URLs`() {
        // Given
        val text = "Visit https://example.com for more info"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("https://example.com", matches[0].value)
    }

    @Test
    fun `symbolPattern should match multiple patterns in same text`() {
        // Given
        val text = "Hello @john, check out https://example.com and use `MyClass.method()`"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(3, matches.size)
        assertEquals("@john", matches[0].value)
        assertEquals("https://example.com", matches[1].value)
        assertEquals("`MyClass.method()`", matches[2].value)
    }

    @Test
    fun `symbolPattern should match complex URLs with parameters`() {
        // Given
        val text = "API endpoint: https://api.example.com/v1/users?id=123&format=json"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("https://api.example.com/v1/users?id=123&format=json", matches[0].value)
    }

    @Test
    fun `symbolPattern should match username with numbers`() {
        // Given
        val text = "Hello @user123 and @test_user"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(2, matches.size)
        assertEquals("@user123", matches[0].value)
        assertEquals("@test_user", matches[1].value)
    }

    @Test
    fun `symbolPattern should match nested markdown styles`() {
        // Given
        val text = "This has *bold* and _italic_ and ~strikethrough~ styles"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(3, matches.size)
        assertEquals("*bold*", matches[0].value)
        assertEquals("_italic_", matches[1].value)
        assertEquals("~strikethrough~", matches[2].value)
    }

    @Test
    fun `symbolPattern should not match incomplete patterns`() {
        // Given
        val text = "This has incomplete *bold and _italic and ~strikethrough"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(0, matches.size)
    }

    @Test
    fun `symbolPattern should not match empty patterns`() {
        // Given
        val text = "Empty patterns: ** __ ~~ ``"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(0, matches.size)
    }

    @Test
    fun `symbolPattern should match code with special characters`() {
        // Given
        val text = "Use `ArrayList<String>()` constructor"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(1, matches.size)
        assertEquals("`ArrayList<String>()`", matches[0].value)
    }

    @Test
    fun `symbolPattern should handle text without any patterns`() {
        // Given
        val text = "This is just plain text without any special formatting"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(0, matches.size)
    }

    @Test
    fun `symbolPattern should match patterns at start and end of text`() {
        // Given
        val text = "@username sends message to https://example.com"

        // When
        val matches = symbolPattern.findAll(text).toList()

        // Then
        assertEquals(2, matches.size)
        assertEquals("@username", matches[0].value)
        assertEquals("https://example.com", matches[1].value)
        assertEquals(0, matches[0].range.first)
        assertEquals(8, matches[0].range.last)
    }

    @Test
    fun `SymbolAnnotationType enum should have correct values`() {
        // Then
        assertEquals("PERSON", SymbolAnnotationType.PERSON.name)
        assertEquals("LINK", SymbolAnnotationType.LINK.name)
    }
}