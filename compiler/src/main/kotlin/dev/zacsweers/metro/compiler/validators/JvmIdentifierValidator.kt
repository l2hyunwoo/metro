// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler.validators

import dev.zacsweers.metro.compiler.IdentifierValidator

/**
 * Validates identifiers for JVM targets (JVM, AndroidJVM) per Java identifier rules.
 *
 * Based on Kotlin's FirJvmInvalidAndDangerousCharactersChecker and Java Language Specification.
 *
 * Rules:
 * - First character: Must be valid Java identifier start (Character.isJavaIdentifierStart)
 * - Subsequent characters: Must be valid Java identifier part (Character.isJavaIdentifierPart)
 * - Dangerous characters blocked: `.`, `;`, `/`, `<`, `>`, `[`, `]`
 * - Unicode surrogate pairs handled via code point iteration
 */
internal object JvmIdentifierValidator : IdentifierValidator {

  /**
   * Dangerous characters that are technically valid in Java identifiers but cause issues
   * in bytecode or reflection contexts.
   *
   * Referenced from Kotlin's FirJvmInvalidAndDangerousCharactersChecker.
   */
  private val dangerousChars = setOf('.', ';', '/', '<', '>', '[', ']')

  override fun sanitize(suggestion: String): String {
    if (suggestion.isEmpty() || suggestion.isBlank()) return "_"

    return buildString {
      var i = 0
      while (i < suggestion.length) {
        val codePoint = suggestion.codePointAt(i)

        when {
          // Dangerous characters always replaced
          dangerousChars.contains(codePoint.toChar()) -> {
            append('_')
          }
          // First character must be a valid Java identifier start
          i == 0 && !Character.isJavaIdentifierStart(codePoint) -> {
            append('_')
            if (Character.isJavaIdentifierPart(codePoint)) {
              appendCodePoint(codePoint)
            }
          }
          // Valid Java identifier part
          Character.isJavaIdentifierPart(codePoint) -> {
            appendCodePoint(codePoint)
          }
          // Invalid character
          else -> {
            append('_')
          }
        }

        i += Character.charCount(codePoint)
      }
    }
  }

  override fun isValid(identifier: String): Boolean {
    return identifier == sanitize(identifier)
  }
}
