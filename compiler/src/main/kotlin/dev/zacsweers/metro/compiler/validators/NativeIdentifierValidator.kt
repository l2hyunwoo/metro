// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler.validators

import dev.zacsweers.metro.compiler.IdentifierValidator

/**
 * Validates identifiers for Kotlin/Native targets with C interop compatibility.
 *
 * Based on C99 specification and Kotlin's FirNativeIdentifierChecker.
 *
 * Rules:
 * - C99 and Objective-C reserved keywords must be sanitized
 * - First character: letter or `_`
 * - Subsequent characters: letter, digit, or `_`
 * - Prefer ASCII for maximum cross-platform compatibility
 */
internal object NativeIdentifierValidator : IdentifierValidator {

  /**
   * C99 reserved keywords.
   */
  private val cReservedKeywords = setOf(
    "auto", "break", "case", "char", "const", "continue", "default", "do", "double", "else",
    "enum", "extern", "float", "for", "goto", "if", "inline", "int", "long", "register",
    "restrict", "return", "short", "signed", "sizeof", "static", "struct", "switch", "typedef",
    "union", "unsigned", "void", "volatile", "while", "_Bool", "_Complex", "_Imaginary"
  )

  /**
   * Objective-C keywords (for Apple platform support).
   */
  private val objectiveCReservedKeywords = setOf(
    "id", "Class", "SEL", "IMP", "BOOL", "nil", "Nil", "YES", "NO", "self", "super", "_cmd",
    "interface", "implementation", "protocol", "end", "private", "protected", "public",
    "try", "catch", "finally", "throw", "synthesize", "dynamic", "property", "optional", "required"
  )

  private val allReservedWords = cReservedKeywords + objectiveCReservedKeywords

  override fun sanitize(suggestion: String): String {
    if (suggestion.isEmpty() || suggestion.isBlank()) return "_"

    // Check if it's a reserved keyword
    val result = if (allReservedWords.contains(suggestion)) {
      "${suggestion}_"
    } else {
      sanitizeCharacters(suggestion)
    }

    // If sanitization resulted in a keyword, append underscore
    return if (allReservedWords.contains(result)) {
      "${result}_"
    } else {
      result
    }
  }

  private fun sanitizeCharacters(suggestion: String): String {
    return buildString {
      var i = 0
      var charIndex = 0
      while (i < suggestion.length) {
        val codePoint = suggestion.codePointAt(i)
        val char = codePoint.toChar()

        when {
          // First character: letter or _
          charIndex == 0 && !isValidCIdentifierStart(char) -> {
            append('_')
            if (isValidCIdentifierPart(char)) append(char)
          }
          // Subsequent characters: letter, digit, or _
          isValidCIdentifierPart(char) -> {
            append(char)
          }
          // Invalid character (including non-ASCII, surrogate pairs)
          else -> {
            append('_')
          }
        }

        i += Character.charCount(codePoint)
        charIndex++
      }
    }
  }

  private fun isValidCIdentifierStart(char: Char): Boolean {
    // Conservative: ASCII letters and underscore only
    return (char in 'a'..'z') || (char in 'A'..'Z') || char == '_'
  }

  private fun isValidCIdentifierPart(char: Char): Boolean {
    // Conservative: ASCII letters, digits, and underscore only
    return (char in 'a'..'z') || (char in 'A'..'Z') || (char in '0'..'9') || char == '_'
  }

  override fun isValid(identifier: String): Boolean {
    return identifier == sanitize(identifier)
  }
}
