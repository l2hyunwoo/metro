// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalUuidApi::class)

package dev.zacsweers.metro.compiler

import org.jetbrains.kotlin.name.Name
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal fun toJavaIdentifier(suggestion: String) = buildString {
  var i = 0
  while (i < suggestion.length) {
    val codePoint = suggestion.codePointAt(i)
    if (
      i == 0 &&
        !Character.isJavaIdentifierStart(codePoint) &&
        Character.isJavaIdentifierPart(codePoint)
    ) {
      append("_")
    }

    val validCodePoint: Int =
      if (Character.isJavaIdentifierPart(codePoint)) {
        codePoint
      } else {
        '_'.code
      }
    appendCodePoint(validCodePoint)
    i += Character.charCount(codePoint)
  }
}

internal fun NameAllocator.newName(suggestion: Name, tag: Any = Uuid.random().toString()): Name {
  return newName(suggestion.asString(), tag).asName()
}
