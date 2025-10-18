/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:OptIn(ExperimentalUuidApi::class)

package dev.zacsweers.metro.compiler

import org.jetbrains.kotlin.renderer.KeywordStringsGenerated.KEYWORDS
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Assigns Kotlin identifier names to avoid collisions, keywords, and invalid characters. To use,
 * first create an instance and allocate all of the names that you need. Typically this is a mix of
 * user-supplied names and constants:
 * ```kotlin
 * val nameAllocator = NameAllocator()
 * for (property in properties) {
 *   nameAllocator.newName(property.name, property)
 * }
 * nameAllocator.newName("sb", "string builder")
 * ```
 *
 * Pass a unique tag object to each allocation. The tag scopes the name, and can be used to look up
 * the allocated name later. Typically the tag is the object that is being named. In the above
 * example we use `property` for the user-supplied property names, and `"string builder"` for our
 * constant string builder.
 *
 * Once we've allocated names we can use them when generating code:
 * ```kotlin
 * val builder = FunSpec.builder("toString")
 *     .addModifiers(KModifier.OVERRIDE)
 *     .returns(String::class)
 *
 * builder.addStatement("val %N = %T()",
 *     nameAllocator.get("string builder"), StringBuilder::class)
 *
 * for (property in properties) {
 *   builder.addStatement("%N.append(%N)",
 *       nameAllocator.get("string builder"), nameAllocator.get(property))
 * }
 * builder.addStatement("return %N.toString()", nameAllocator.get("string builder"))
 * return builder.build()
 * ```
 *
 * The above code generates unique names if presented with conflicts. Given user-supplied properties
 * with names `ab` and `sb` this generates the following:
 * ```kotlin
 * override fun toString(): kotlin.String {
 *   val sb_ = java.lang.StringBuilder()
 *   sb_.append(ab)
 *   sb_.append(sb)
 *   return sb_.toString()
 * }
 * ```
 *
 * The underscore is appended to `sb` to avoid conflicting with the user-supplied `sb` property.
 * Underscores are also prefixed for names that start with a digit, and used to replace name-unsafe
 * characters like space or dash.
 *
 * When dealing with multiple independent inner scopes, use a [copy][NameAllocator.copy] of the
 * NameAllocator used for the outer scope to further refine name allocation for a specific inner
 * scope.
 *
 * Changes from upstream: added [Mode] support for use with member inject parameters.
 */
// TODO change to Name?
internal class NameAllocator
private constructor(
  private val allocatedNames: MutableSet<String>,
  private val tagToName: MutableMap<Any, String>,
  private val mode: Mode,
  private val validator: IdentifierValidator,
) {
  /**
   * @param preallocateKeywords If true, all Kotlin keywords will be preallocated. Requested names
   *   which collide with keywords will be suffixed with underscores to avoid being used as
   *   identifiers:
   * ```kotlin
   * val nameAllocator = NameAllocator(preallocateKeywords = true)
   * println(nameAllocator.newName("when")) // prints "when_"
   * ```
   *
   * If false, keywords will not get any special treatment:
   * ```kotlin
   * val nameAllocator = NameAllocator(preallocateKeywords = false)
   * println(nameAllocator.newName("when")) // prints "when"
   * ```
   *
   * Note that you can use the `%N` placeholder when emitting a name produced by [NameAllocator] to
   * ensure it's properly escaped for use as an identifier:
   * ```kotlin
   * val nameAllocator = NameAllocator(preallocateKeywords = false)
   * println(CodeBlock.of("%N", nameAllocator.newName("when"))) // prints "`when`"
   * ```
   *
   * The default behaviour of [NameAllocator] is to preallocate keywords - this is the behaviour
   * you'll get when using the no-arg constructor.
   *
   * @param mode The collision resolution mode to use. [Mode.UNDERSCORE] appends underscores to
   *   conflicting names, while [Mode.COUNT] appends a numeric suffix.
   *
   * @param platform The target platform for name validation. Determines which platform-specific
   *   keywords and identifier rules to apply:
   *   - [Platform.JVM]: Java identifier rules (allows Unicode, blocks dangerous chars like `.`, `;`)
   *   - [Platform.JS]: JavaScript/ECMAScript 5.1 rules (allows Unicode, `$`, blocks JS keywords)
   *   - [Platform.NATIVE]: C99/Objective-C rules (ASCII-only, blocks C and Objective-C keywords)
   *   - [Platform.COMMON]: Conservative union of all platform restrictions (ASCII-only, blocks all
   *     platform keywords). This is the default and ensures generated names work on all platforms.
   *
   * ```kotlin
   * // Default: conservative cross-platform validation
   * val nameAllocator = NameAllocator()
   * println(nameAllocator.newName("int")) // prints "int_" (C keyword blocked)
   *
   * // JVM-specific: allows "int" since it's not a Java keyword
   * val jvmAllocator = NameAllocator(platform = Platform.JVM)
   * println(jvmAllocator.newName("int")) // prints "int"
   *
   * // JS-specific: allows "$jquery" syntax
   * val jsAllocator = NameAllocator(platform = Platform.JS)
   * println(jsAllocator.newName("$myVar")) // prints "$myVar"
   * ```
   */
  constructor(
    preallocateKeywords: Boolean = true,
    mode: Mode = Mode.UNDERSCORE,
    platform: Platform = Platform.COMMON,
  ) : this(
    allocatedNames = if (preallocateKeywords) KEYWORDS.toMutableSet() else mutableSetOf(),
    tagToName = mutableMapOf(),
    mode = mode,
    validator = platform.createValidator(),
  )

  /**
   * Return a new name using [suggestion] that will not be a Java identifier or clash with other
   * names. The returned value can be queried multiple times by passing `tag` to
   * [NameAllocator.get].
   */
  fun newName(suggestion: String, tag: Any = Uuid.random().toString()): String {
    val cleanedSuggestion = validator.sanitize(suggestion)
    val result = buildString {
      append(cleanedSuggestion)
      var count = 1
      while (!allocatedNames.add(toString())) {
        when (mode) {
          Mode.UNDERSCORE -> append('_')
          Mode.COUNT -> {
            deleteRange(cleanedSuggestion.length, length)
            append(++count)
          }
        }
      }
    }

    val replaced = tagToName.put(tag, result)
    if (replaced != null) {
      tagToName[tag] = replaced // Put things back as they were!
      throw IllegalArgumentException("tag $tag cannot be used for both '$replaced' and '$result'")
    }

    return result
  }

  /** Retrieve a name created with [NameAllocator.newName]. */
  operator fun get(tag: Any): String = requireNotNull(tagToName[tag]) { "unknown tag: $tag" }

  /**
   * Create a deep copy of this NameAllocator. Useful to create multiple independent refinements of
   * a NameAllocator to be used in the respective definition of multiples, independently-scoped,
   * inner code blocks.
   *
   * @return A deep copy of this NameAllocator.
   */
  fun copy(): NameAllocator {
    return NameAllocator(allocatedNames.toMutableSet(), tagToName.toMutableMap(), mode = mode, validator = validator)
  }

  internal enum class Mode {
    UNDERSCORE,
    COUNT,
  }
}
