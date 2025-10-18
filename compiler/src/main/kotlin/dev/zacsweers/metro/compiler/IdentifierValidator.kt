// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler

/**
 * Interface for platform-specific identifier validation and sanitization.
 *
 * Each platform has different rules for valid identifiers:
 * - JVM: Java identifier rules + dangerous character blocking
 * - JS: ES 5.1 spec + reserved keyword handling
 * - Native: C99 + Objective-C keyword blocking
 * - Common: Conservative union of all rules
 *
 * Implementations should be stateless singletons.
 */
internal interface IdentifierValidator {
  /**
   * Sanitizes an identifier suggestion to be valid for the target platform.
   *
   * @param suggestion Raw identifier suggestion (may contain invalid characters or keywords)
   * @return Sanitized identifier guaranteed to be valid per platform rules
   */
  fun sanitize(suggestion: String): String

  /**
   * Checks if an identifier is valid for the target platform.
   *
   * @param identifier Identifier to validate
   * @return true if valid, false if requires sanitization
   */
  fun isValid(identifier: String): Boolean

  /**
   * Checks if an identifier suggestion would be modified by sanitization.
   *
   * Default implementation compares sanitized output with input.
   *
   * @param suggestion Raw identifier suggestion
   * @return true if sanitization would modify the input
   */
  fun needsSanitization(suggestion: String): Boolean {
    return sanitize(suggestion) != suggestion
  }
}
