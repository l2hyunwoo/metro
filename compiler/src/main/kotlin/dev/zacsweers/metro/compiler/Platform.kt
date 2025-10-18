// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler

import dev.zacsweers.metro.compiler.validators.CommonIdentifierValidator
import dev.zacsweers.metro.compiler.validators.JsIdentifierValidator
import dev.zacsweers.metro.compiler.validators.JvmIdentifierValidator
import dev.zacsweers.metro.compiler.validators.NativeIdentifierValidator

/**
 * Specifies the target platform for identifier validation in [NameAllocator].
 *
 * Each platform has different identifier rules that determine which names are valid,
 * which keywords are reserved, and which characters are allowed.
 *
 * Maps to Kotlin's KotlinPlatformType from the Kotlin Gradle Plugin:
 * - JVM: Java Virtual Machine targets (jvm, androidJvm)
 * - JS: JavaScript and WebAssembly targets (js, wasmJs, wasmWasi)
 * - NATIVE: Kotlin/Native targets (all native platforms)
 * - COMMON: Platform-agnostic or unknown targets (conservative fallback)
 *
 * @see NameAllocator
 */
internal enum class Platform {
  /**
   * Java Virtual Machine (JVM) platform.
   *
   * Uses Java identifier rules:
   * - Allows full Unicode characters (letters, digits)
   * - Blocks dangerous characters: `.`, `;`, `/`, `<`, `>`, `[`, `]`
   * - First character must be a Java identifier start (letter, `$`, `_`)
   * - Subsequent characters can be letters, digits, `$`, or `_`
   *
   * Does NOT block C keywords like "int" or "struct" since they are not reserved in Java.
   */
  JVM,

  /**
   * JavaScript (JS) platform.
   *
   * Uses ECMAScript 5.1 identifier rules:
   * - Allows full Unicode letters and digits
   * - Allows `$` and `_` anywhere in the identifier
   * - Blocks ES5 reserved words: `break`, `case`, `for`, `function`, `var`, etc.
   * - Blocks ES6 keywords: `class`, `const`, `let`, `import`, etc.
   * - Blocks special identifiers: `eval`, `arguments`
   * - Supports ZWNJ (U+200C) and ZWJ (U+200D) characters
   *
   * Does NOT block C keywords like "int" or Java-specific characters.
   */
  JS,

  /**
   * Native platform (iOS, macOS, Linux, Windows native targets).
   *
   * Uses C99 and Objective-C identifier rules:
   * - ASCII-only (conservative for C interop)
   * - Blocks C keywords: `int`, `struct`, `void`, `char`, etc.
   * - Blocks Objective-C keywords: `id`, `nil`, `self`, `protocol`, etc.
   * - First character must be ASCII letter or `_`
   * - Subsequent characters can be ASCII letters, digits, or `_`
   *
   * This is the most restrictive platform due to C's limited identifier support.
   */
  NATIVE,

  /**
   * Common platform (cross-platform safe).
   *
   * Uses the conservative union of all platform restrictions:
   * - ASCII-only (most restrictive character set)
   * - Blocks ALL reserved keywords from JS, C, Objective-C, and Java
   * - Blocks JVM dangerous characters: `.`, `;`, `/`, `<`, `>`, `[`, `]`
   * - First character must be ASCII letter or `_`
   * - Subsequent characters can be ASCII letters, digits, or `_`
   *
   * This is the default platform for [NameAllocator] to ensure generated names
   * work correctly on all Kotlin multiplatform targets.
   *
   * **Use COMMON when:**
   * - Generating code for multiplatform projects
   * - You want maximum safety across all platforms
   * - Platform-specific compilation context is unavailable
   *
   * **Use platform-specific values when:**
   * - You know the specific target platform at code generation time
   * - You want to allow platform-specific identifier features
   * - You need less conservative name sanitization
   */
  COMMON,
}

/**
 * Extension function to create the appropriate validator for a platform.
 *
 * @return Platform-specific validator instance
 */
internal fun Platform.createValidator(): IdentifierValidator = when (this) {
  Platform.JVM -> JvmIdentifierValidator
  Platform.JS -> JsIdentifierValidator
  Platform.NATIVE -> NativeIdentifierValidator
  Platform.COMMON -> CommonIdentifierValidator
}
