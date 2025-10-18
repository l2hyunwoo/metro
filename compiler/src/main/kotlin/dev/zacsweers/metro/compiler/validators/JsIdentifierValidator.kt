// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler.validators

import dev.zacsweers.metro.compiler.IdentifierValidator

/**
 * Validates identifiers for JavaScript and WebAssembly targets per ES 5.1 specification.
 *
 * Based on ECMAScript 5.1 section 7.6 and Kotlin's FirJsExportDeclarationChecker.
 *
 * Rules:
 * - Reserved keywords must be sanitized (appended with underscore)
 * - First character: Unicode letter, `$`, or `_`
 * - Subsequent characters: Unicode letter, digit, `$`, `_`, zero-width joiner/non-joiner
 * - Case-sensitive keyword matching
 */
internal object JsIdentifierValidator : IdentifierValidator {

  /**
   * ES 5.1 reserved keywords.
   */
  private val reservedKeywords = setOf(
    "break", "case", "catch", "continue", "debugger", "default", "delete", "do", "else",
    "finally", "for", "function", "if", "in", "instanceof", "new", "return", "switch",
    "this", "throw", "try", "typeof", "var", "void", "while", "with"
  )

  /**
   * ES 5.1 future reserved keywords (including strict mode).
   */
  private val futureReservedKeywords = setOf(
    "class", "const", "enum", "export", "extends", "import", "super",
    "implements", "interface", "let", "package", "private", "protected", "public", "static", "yield"
  )

  /**
   * Special identifiers that should be avoided (not strictly reserved but problematic).
   */
  private val specialIdentifiers = setOf("eval", "arguments")

  private val allReservedWords = reservedKeywords + futureReservedKeywords + specialIdentifiers

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
          // First character: letter, $, or _
          charIndex == 0 && !isValidJsIdentifierStart(char) -> {
            append('_')
            if (isValidJsIdentifierPart(char)) append(char)
          }
          // Subsequent characters: letter, digit, $, _
          isValidJsIdentifierPart(char) -> {
            append(char)
          }
          // Invalid character (including surrogate pairs)
          else -> {
            append('_')
          }
        }

        i += Character.charCount(codePoint)
        charIndex++
      }
    }
  }

  private fun isValidJsIdentifierStart(char: Char): Boolean {
    return char.isLetter() || char == '$' || char == '_'
  }

  private fun isValidJsIdentifierPart(char: Char): Boolean {
    return char.isLetterOrDigit() ||
           char == '$' ||
           char == '_' ||
           char == '\u200C' ||  // Zero Width Non-Joiner (ZWNJ)
           char == '\u200D'     // Zero Width Joiner (ZWJ)
  }

  override fun isValid(identifier: String): Boolean {
    return identifier == sanitize(identifier)
  }
}
