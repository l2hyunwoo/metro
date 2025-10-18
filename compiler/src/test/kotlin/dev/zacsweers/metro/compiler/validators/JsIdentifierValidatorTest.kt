// Copyright (C) 2015 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler.validators

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class JsIdentifierValidatorTest {

  @Test
  fun `valid JS identifier passes validation`() {
    assertThat(JsIdentifierValidator.isValid("myVariable")).isTrue()
    assertThat(JsIdentifierValidator.isValid("_privateVar")).isTrue()
    assertThat(JsIdentifierValidator.isValid("\$jquery")).isTrue()
    assertThat(JsIdentifierValidator.isValid("variable123")).isTrue()
  }

  @Test
  fun `sanitize returns valid identifiers unchanged`() {
    assertThat(JsIdentifierValidator.sanitize("validName")).isEqualTo("validName")
    assertThat(JsIdentifierValidator.sanitize("_underscore")).isEqualTo("_underscore")
    assertThat(JsIdentifierValidator.sanitize("\$dollar")).isEqualTo("\$dollar")
    assertThat(JsIdentifierValidator.sanitize("camelCase123")).isEqualTo("camelCase123")
  }

  @Test
  fun `sanitize handles reserved ES5 keywords`() {
    // ES5 reserved words
    assertThat(JsIdentifierValidator.sanitize("break")).isEqualTo("break_")
    assertThat(JsIdentifierValidator.sanitize("case")).isEqualTo("case_")
    assertThat(JsIdentifierValidator.sanitize("catch")).isEqualTo("catch_")
    assertThat(JsIdentifierValidator.sanitize("continue")).isEqualTo("continue_")
    assertThat(JsIdentifierValidator.sanitize("debugger")).isEqualTo("debugger_")
    assertThat(JsIdentifierValidator.sanitize("default")).isEqualTo("default_")
    assertThat(JsIdentifierValidator.sanitize("delete")).isEqualTo("delete_")
    assertThat(JsIdentifierValidator.sanitize("do")).isEqualTo("do_")
    assertThat(JsIdentifierValidator.sanitize("else")).isEqualTo("else_")
    assertThat(JsIdentifierValidator.sanitize("finally")).isEqualTo("finally_")
    assertThat(JsIdentifierValidator.sanitize("for")).isEqualTo("for_")
    assertThat(JsIdentifierValidator.sanitize("function")).isEqualTo("function_")
    assertThat(JsIdentifierValidator.sanitize("if")).isEqualTo("if_")
    assertThat(JsIdentifierValidator.sanitize("in")).isEqualTo("in_")
    assertThat(JsIdentifierValidator.sanitize("instanceof")).isEqualTo("instanceof_")
    assertThat(JsIdentifierValidator.sanitize("new")).isEqualTo("new_")
    assertThat(JsIdentifierValidator.sanitize("return")).isEqualTo("return_")
    assertThat(JsIdentifierValidator.sanitize("switch")).isEqualTo("switch_")
    assertThat(JsIdentifierValidator.sanitize("this")).isEqualTo("this_")
    assertThat(JsIdentifierValidator.sanitize("throw")).isEqualTo("throw_")
    assertThat(JsIdentifierValidator.sanitize("try")).isEqualTo("try_")
    assertThat(JsIdentifierValidator.sanitize("typeof")).isEqualTo("typeof_")
    assertThat(JsIdentifierValidator.sanitize("var")).isEqualTo("var_")
    assertThat(JsIdentifierValidator.sanitize("void")).isEqualTo("void_")
    assertThat(JsIdentifierValidator.sanitize("while")).isEqualTo("while_")
    assertThat(JsIdentifierValidator.sanitize("with")).isEqualTo("with_")
  }

  @Test
  fun `sanitize handles ES6 reserved keywords`() {
    assertThat(JsIdentifierValidator.sanitize("class")).isEqualTo("class_")
    assertThat(JsIdentifierValidator.sanitize("const")).isEqualTo("const_")
    assertThat(JsIdentifierValidator.sanitize("enum")).isEqualTo("enum_")
    assertThat(JsIdentifierValidator.sanitize("export")).isEqualTo("export_")
    assertThat(JsIdentifierValidator.sanitize("extends")).isEqualTo("extends_")
    assertThat(JsIdentifierValidator.sanitize("import")).isEqualTo("import_")
    assertThat(JsIdentifierValidator.sanitize("super")).isEqualTo("super_")
  }

  @Test
  fun `sanitize handles future reserved words`() {
    assertThat(JsIdentifierValidator.sanitize("implements")).isEqualTo("implements_")
    assertThat(JsIdentifierValidator.sanitize("interface")).isEqualTo("interface_")
    assertThat(JsIdentifierValidator.sanitize("let")).isEqualTo("let_")
    assertThat(JsIdentifierValidator.sanitize("package")).isEqualTo("package_")
    assertThat(JsIdentifierValidator.sanitize("private")).isEqualTo("private_")
    assertThat(JsIdentifierValidator.sanitize("protected")).isEqualTo("protected_")
    assertThat(JsIdentifierValidator.sanitize("public")).isEqualTo("public_")
    assertThat(JsIdentifierValidator.sanitize("static")).isEqualTo("static_")
    assertThat(JsIdentifierValidator.sanitize("yield")).isEqualTo("yield_")
  }

  @Test
  fun `sanitize handles special identifiers that should not be used`() {
    assertThat(JsIdentifierValidator.sanitize("eval")).isEqualTo("eval_")
    assertThat(JsIdentifierValidator.sanitize("arguments")).isEqualTo("arguments_")
  }

  @Test
  fun `sanitize handles invalid start characters`() {
    // Digits cannot start identifier
    assertThat(JsIdentifierValidator.sanitize("123abc")).isEqualTo("_123abc")
    assertThat(JsIdentifierValidator.sanitize("9variable")).isEqualTo("_9variable")

    // Special characters that cannot start
    assertThat(JsIdentifierValidator.sanitize("-kebab")).isEqualTo("_kebab")
    assertThat(JsIdentifierValidator.sanitize("@annotation")).isEqualTo("_annotation")
  }

  @Test
  fun `sanitize handles invalid middle characters`() {
    assertThat(JsIdentifierValidator.sanitize("kebab-case")).isEqualTo("kebab_case")
    assertThat(JsIdentifierValidator.sanitize("dot.notation")).isEqualTo("dot_notation")
    assertThat(JsIdentifierValidator.sanitize("space name")).isEqualTo("space_name")
    assertThat(JsIdentifierValidator.sanitize("hash#tag")).isEqualTo("hash_tag")
  }

  @Test
  fun `sanitize handles unicode characters`() {
    // Valid unicode letters
    assertThat(JsIdentifierValidator.sanitize("café")).isEqualTo("café")
    assertThat(JsIdentifierValidator.sanitize("münchen")).isEqualTo("münchen")
    assertThat(JsIdentifierValidator.sanitize("日本語")).isEqualTo("日本語")

    // Unicode with invalid characters mixed
    assertThat(JsIdentifierValidator.sanitize("hello-世界")).isEqualTo("hello_世界")
  }

  @Test
  fun `sanitize handles ZWNJ and ZWJ characters`() {
    // Zero Width Non-Joiner and Zero Width Joiner are valid in JS identifiers
    assertThat(JsIdentifierValidator.sanitize("ab\u200Ccd")).isEqualTo("ab\u200Ccd")
    assertThat(JsIdentifierValidator.sanitize("ab\u200Dcd")).isEqualTo("ab\u200Dcd")
  }

  @Test
  fun `sanitize handles surrogate pairs`() {
    // Emoji (surrogate pair) is treated as a single invalid character
    // The entire surrogate pair gets replaced with a single underscore
    val emojiIdentifier = "test🚀data"
    val sanitized = JsIdentifierValidator.sanitize(emojiIdentifier)
    // Surrogate pairs are replaced with single underscore
    assertThat(sanitized).isEqualTo("test_data")
  }

  @Test
  fun `sanitize handles empty and whitespace strings`() {
    assertThat(JsIdentifierValidator.sanitize("")).isEqualTo("_")
    assertThat(JsIdentifierValidator.sanitize("   ")).isEqualTo("_")
    assertThat(JsIdentifierValidator.sanitize("\t")).isEqualTo("_")
    assertThat(JsIdentifierValidator.sanitize("\n")).isEqualTo("_")
  }

  @Test
  fun `sanitize handles multiple consecutive invalid characters`() {
    assertThat(JsIdentifierValidator.sanitize("a---b")).isEqualTo("a___b")
    assertThat(JsIdentifierValidator.sanitize("test...name")).isEqualTo("test___name")
    assertThat(JsIdentifierValidator.sanitize("a  b")).isEqualTo("a__b")
  }

  @Test
  fun `sanitize handles strings that become keywords after sanitization`() {
    // If sanitization results in a keyword, it should append underscore
    assertThat(JsIdentifierValidator.sanitize("for-loop")).isEqualTo("for_loop")
    assertThat(JsIdentifierValidator.sanitize("class-name")).isEqualTo("class_name")
  }

  @Test
  fun `isValid correctly identifies valid identifiers`() {
    assertThat(JsIdentifierValidator.isValid("validName")).isTrue()
    assertThat(JsIdentifierValidator.isValid("_private")).isTrue()
    assertThat(JsIdentifierValidator.isValid("\$jquery")).isTrue()
    assertThat(JsIdentifierValidator.isValid("name123")).isTrue()
    assertThat(JsIdentifierValidator.isValid("camelCase")).isTrue()
  }

  @Test
  fun `isValid correctly identifies invalid identifiers`() {
    // Keywords
    assertThat(JsIdentifierValidator.isValid("for")).isFalse()
    assertThat(JsIdentifierValidator.isValid("class")).isFalse()
    assertThat(JsIdentifierValidator.isValid("eval")).isFalse()

    // Invalid characters
    assertThat(JsIdentifierValidator.isValid("kebab-case")).isFalse()
    assertThat(JsIdentifierValidator.isValid("dot.notation")).isFalse()
    assertThat(JsIdentifierValidator.isValid("123start")).isFalse()

    // Empty
    assertThat(JsIdentifierValidator.isValid("")).isFalse()
  }

  @Test
  fun `needsSanitization correctly identifies when sanitization is needed`() {
    // Valid names don't need sanitization
    assertThat(JsIdentifierValidator.needsSanitization("validName")).isFalse()
    assertThat(JsIdentifierValidator.needsSanitization("_private")).isFalse()

    // Keywords need sanitization
    assertThat(JsIdentifierValidator.needsSanitization("for")).isTrue()
    assertThat(JsIdentifierValidator.needsSanitization("class")).isTrue()
    assertThat(JsIdentifierValidator.needsSanitization("eval")).isTrue()

    // Invalid characters need sanitization
    assertThat(JsIdentifierValidator.needsSanitization("kebab-case")).isTrue()
    assertThat(JsIdentifierValidator.needsSanitization("123start")).isTrue()
    assertThat(JsIdentifierValidator.needsSanitization("")).isTrue()
  }

  @Test
  fun `dollar sign is valid in JS identifiers`() {
    assertThat(JsIdentifierValidator.isValid("\$")).isTrue()
    assertThat(JsIdentifierValidator.isValid("\$var")).isTrue()
    assertThat(JsIdentifierValidator.isValid("my\$var")).isTrue()
    assertThat(JsIdentifierValidator.isValid("var\$")).isTrue()
    assertThat(JsIdentifierValidator.sanitize("\$variable")).isEqualTo("\$variable")
  }

  @Test
  fun `underscore is valid in JS identifiers`() {
    assertThat(JsIdentifierValidator.isValid("_")).isTrue()
    assertThat(JsIdentifierValidator.isValid("_var")).isTrue()
    assertThat(JsIdentifierValidator.isValid("my_var")).isTrue()
    assertThat(JsIdentifierValidator.isValid("var_")).isTrue()
    assertThat(JsIdentifierValidator.sanitize("_private")).isEqualTo("_private")
  }

  @Test
  fun `case sensitivity is preserved`() {
    // JS is case-sensitive, 'For' is not a keyword but 'for' is
    assertThat(JsIdentifierValidator.isValid("For")).isTrue()
    assertThat(JsIdentifierValidator.isValid("for")).isFalse()
    assertThat(JsIdentifierValidator.sanitize("For")).isEqualTo("For")
    assertThat(JsIdentifierValidator.sanitize("for")).isEqualTo("for_")
  }
}
