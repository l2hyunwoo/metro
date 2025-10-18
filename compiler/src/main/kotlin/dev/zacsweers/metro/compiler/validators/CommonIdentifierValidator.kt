// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler.validators

import dev.zacsweers.metro.compiler.IdentifierValidator

/**
 * Provides conservative fallback validation for platform-agnostic or unknown targets.
 *
 * Applies the union of all platform restrictions to ensure identifiers are safe
 * for JVM, JavaScript, and Native platforms.
 *
 * Rules:
 * - Blocks all reserved keywords from JS, C, and Objective-C
 * - Blocks JVM dangerous characters
 * - Uses most conservative character validation (ASCII preferred)
 */
internal object CommonIdentifierValidator : IdentifierValidator {

  /**
   * Dangerous characters from JVM validator.
   */
  private val dangerousChars = setOf('.', ';', '/', '<', '>', '[', ']')

  /**
   * All reserved keywords from all platforms (union).
   */
  private val allReservedKeywords = setOf(
    // JS keywords
    "break", "case", "catch", "continue", "debugger", "default", "delete", "do", "else",
    "finally", "for", "function", "if", "in", "instanceof", "new", "return", "switch",
    "this", "throw", "try", "typeof", "var", "void", "while", "with",
    "class", "const", "enum", "export", "extends", "import", "super",
    "implements", "interface", "let", "package", "private", "protected", "public", "static", "yield",
    "eval", "arguments",
    // C keywords
    "auto", "char", "double", "extern", "float", "goto", "inline", "int", "long", "register",
    "restrict", "short", "signed", "sizeof", "struct", "typedef", "union", "unsigned", "volatile",
    "_Bool", "_Complex", "_Imaginary",
    // Objective-C keywords
    "id", "Class", "SEL", "IMP", "BOOL", "nil", "Nil", "YES", "NO", "self", "_cmd",
    "implementation", "protocol", "end", "synthesize", "dynamic", "property", "optional", "required"
  )

  override fun sanitize(suggestion: String): String {
    if (suggestion.isEmpty() || suggestion.isBlank()) return "_"

    // Check if it's a reserved keyword
    val result = if (allReservedKeywords.contains(suggestion)) {
      "${suggestion}_"
    } else {
      sanitizeCharacters(suggestion)
    }

    // If sanitization resulted in a keyword, append underscore
    return if (allReservedKeywords.contains(result)) {
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
          // Dangerous characters always replaced (from JVM)
          dangerousChars.contains(char) -> {
            append('_')
          }
          // First character: ASCII letter or underscore only (most conservative)
          charIndex == 0 && !isValidCommonStart(char) -> {
            append('_')
            if (isValidCommonPart(char)) append(char)
          }
          // Subsequent characters: ASCII alphanumeric or underscore only (most conservative)
          isValidCommonPart(char) -> {
            append(char)
          }
          // Invalid character (including non-ASCII, surrogate pairs, etc.)
          else -> {
            append('_')
          }
        }

        i += Character.charCount(codePoint)
        charIndex++
      }
    }
  }

  private fun isValidCommonStart(char: Char): Boolean {
    // Most conservative: ASCII letters and underscore only
    return (char in 'a'..'z') || (char in 'A'..'Z') || char == '_'
  }

  private fun isValidCommonPart(char: Char): Boolean {
    // Most conservative: ASCII letters, digits, and underscore only
    return (char in 'a'..'z') || (char in 'A'..'Z') || (char in '0'..'9') || char == '_'
  }

  override fun isValid(identifier: String): Boolean {
    return identifier == sanitize(identifier)
  }
}
