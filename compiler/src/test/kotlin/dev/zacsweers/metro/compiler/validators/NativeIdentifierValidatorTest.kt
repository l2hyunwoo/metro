// Copyright (C) 2015 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler.validators

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NativeIdentifierValidatorTest {

  @Test
  fun `valid C99 identifier passes validation`() {
    assertThat(NativeIdentifierValidator.isValid("myVariable")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("_privateVar")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("variable123")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("CONSTANT_VALUE")).isTrue()
  }

  @Test
  fun `sanitize returns valid identifiers unchanged`() {
    assertThat(NativeIdentifierValidator.sanitize("validName")).isEqualTo("validName")
    assertThat(NativeIdentifierValidator.sanitize("_underscore")).isEqualTo("_underscore")
    assertThat(NativeIdentifierValidator.sanitize("camelCase123")).isEqualTo("camelCase123")
    assertThat(NativeIdentifierValidator.sanitize("SCREAMING_SNAKE")).isEqualTo("SCREAMING_SNAKE")
  }

  @Test
  fun `sanitize handles C99 keywords`() {
    assertThat(NativeIdentifierValidator.sanitize("auto")).isEqualTo("auto_")
    assertThat(NativeIdentifierValidator.sanitize("break")).isEqualTo("break_")
    assertThat(NativeIdentifierValidator.sanitize("case")).isEqualTo("case_")
    assertThat(NativeIdentifierValidator.sanitize("char")).isEqualTo("char_")
    assertThat(NativeIdentifierValidator.sanitize("const")).isEqualTo("const_")
    assertThat(NativeIdentifierValidator.sanitize("continue")).isEqualTo("continue_")
    assertThat(NativeIdentifierValidator.sanitize("default")).isEqualTo("default_")
    assertThat(NativeIdentifierValidator.sanitize("do")).isEqualTo("do_")
    assertThat(NativeIdentifierValidator.sanitize("double")).isEqualTo("double_")
    assertThat(NativeIdentifierValidator.sanitize("else")).isEqualTo("else_")
    assertThat(NativeIdentifierValidator.sanitize("enum")).isEqualTo("enum_")
    assertThat(NativeIdentifierValidator.sanitize("extern")).isEqualTo("extern_")
    assertThat(NativeIdentifierValidator.sanitize("float")).isEqualTo("float_")
    assertThat(NativeIdentifierValidator.sanitize("for")).isEqualTo("for_")
    assertThat(NativeIdentifierValidator.sanitize("goto")).isEqualTo("goto_")
    assertThat(NativeIdentifierValidator.sanitize("if")).isEqualTo("if_")
    assertThat(NativeIdentifierValidator.sanitize("inline")).isEqualTo("inline_")
    assertThat(NativeIdentifierValidator.sanitize("int")).isEqualTo("int_")
    assertThat(NativeIdentifierValidator.sanitize("long")).isEqualTo("long_")
    assertThat(NativeIdentifierValidator.sanitize("register")).isEqualTo("register_")
    assertThat(NativeIdentifierValidator.sanitize("restrict")).isEqualTo("restrict_")
    assertThat(NativeIdentifierValidator.sanitize("return")).isEqualTo("return_")
    assertThat(NativeIdentifierValidator.sanitize("short")).isEqualTo("short_")
    assertThat(NativeIdentifierValidator.sanitize("signed")).isEqualTo("signed_")
    assertThat(NativeIdentifierValidator.sanitize("sizeof")).isEqualTo("sizeof_")
    assertThat(NativeIdentifierValidator.sanitize("static")).isEqualTo("static_")
    assertThat(NativeIdentifierValidator.sanitize("struct")).isEqualTo("struct_")
    assertThat(NativeIdentifierValidator.sanitize("switch")).isEqualTo("switch_")
    assertThat(NativeIdentifierValidator.sanitize("typedef")).isEqualTo("typedef_")
    assertThat(NativeIdentifierValidator.sanitize("union")).isEqualTo("union_")
    assertThat(NativeIdentifierValidator.sanitize("unsigned")).isEqualTo("unsigned_")
    assertThat(NativeIdentifierValidator.sanitize("void")).isEqualTo("void_")
    assertThat(NativeIdentifierValidator.sanitize("volatile")).isEqualTo("volatile_")
    assertThat(NativeIdentifierValidator.sanitize("while")).isEqualTo("while_")
    assertThat(NativeIdentifierValidator.sanitize("_Bool")).isEqualTo("_Bool_")
    assertThat(NativeIdentifierValidator.sanitize("_Complex")).isEqualTo("_Complex_")
    assertThat(NativeIdentifierValidator.sanitize("_Imaginary")).isEqualTo("_Imaginary_")
  }

  @Test
  fun `sanitize handles Objective-C keywords`() {
    assertThat(NativeIdentifierValidator.sanitize("id")).isEqualTo("id_")
    assertThat(NativeIdentifierValidator.sanitize("Class")).isEqualTo("Class_")
    assertThat(NativeIdentifierValidator.sanitize("SEL")).isEqualTo("SEL_")
    assertThat(NativeIdentifierValidator.sanitize("IMP")).isEqualTo("IMP_")
    assertThat(NativeIdentifierValidator.sanitize("BOOL")).isEqualTo("BOOL_")
    assertThat(NativeIdentifierValidator.sanitize("nil")).isEqualTo("nil_")
    assertThat(NativeIdentifierValidator.sanitize("Nil")).isEqualTo("Nil_")
    assertThat(NativeIdentifierValidator.sanitize("YES")).isEqualTo("YES_")
    assertThat(NativeIdentifierValidator.sanitize("NO")).isEqualTo("NO_")
    assertThat(NativeIdentifierValidator.sanitize("self")).isEqualTo("self_")
    assertThat(NativeIdentifierValidator.sanitize("_cmd")).isEqualTo("_cmd_")
    assertThat(NativeIdentifierValidator.sanitize("implementation")).isEqualTo("implementation_")
    assertThat(NativeIdentifierValidator.sanitize("protocol")).isEqualTo("protocol_")
    assertThat(NativeIdentifierValidator.sanitize("end")).isEqualTo("end_")
    assertThat(NativeIdentifierValidator.sanitize("synthesize")).isEqualTo("synthesize_")
    assertThat(NativeIdentifierValidator.sanitize("dynamic")).isEqualTo("dynamic_")
    assertThat(NativeIdentifierValidator.sanitize("property")).isEqualTo("property_")
    assertThat(NativeIdentifierValidator.sanitize("optional")).isEqualTo("optional_")
    assertThat(NativeIdentifierValidator.sanitize("required")).isEqualTo("required_")
  }

  @Test
  fun `sanitize handles invalid start characters`() {
    // Digits cannot start identifier
    assertThat(NativeIdentifierValidator.sanitize("123abc")).isEqualTo("_123abc")
    assertThat(NativeIdentifierValidator.sanitize("9variable")).isEqualTo("_9variable")

    // Special characters that cannot start
    assertThat(NativeIdentifierValidator.sanitize("-kebab")).isEqualTo("_kebab")
    assertThat(NativeIdentifierValidator.sanitize("@annotation")).isEqualTo("_annotation")
  }

  @Test
  fun `sanitize handles invalid middle characters`() {
    assertThat(NativeIdentifierValidator.sanitize("kebab-case")).isEqualTo("kebab_case")
    assertThat(NativeIdentifierValidator.sanitize("dot.notation")).isEqualTo("dot_notation")
    assertThat(NativeIdentifierValidator.sanitize("space name")).isEqualTo("space_name")
    assertThat(NativeIdentifierValidator.sanitize("hash#tag")).isEqualTo("hash_tag")
  }

  @Test
  fun `sanitize handles non-ASCII characters`() {
    // C99 only allows ASCII characters, non-ASCII should be replaced
    assertThat(NativeIdentifierValidator.sanitize("café")).isEqualTo("caf_")
    assertThat(NativeIdentifierValidator.sanitize("münchen")).isEqualTo("m_nchen")
    assertThat(NativeIdentifierValidator.sanitize("日本語")).isEqualTo("___")
  }

  @Test
  fun `sanitize handles surrogate pairs`() {
    // Emoji (surrogate pair) should be replaced with underscore
    assertThat(NativeIdentifierValidator.sanitize("test🚀data")).isEqualTo("test_data")
    assertThat(NativeIdentifierValidator.sanitize("a🍺b")).isEqualTo("a_b")
  }

  @Test
  fun `sanitize handles empty and whitespace strings`() {
    assertThat(NativeIdentifierValidator.sanitize("")).isEqualTo("_")
    assertThat(NativeIdentifierValidator.sanitize("   ")).isEqualTo("_")
    assertThat(NativeIdentifierValidator.sanitize("\t")).isEqualTo("_")
    assertThat(NativeIdentifierValidator.sanitize("\n")).isEqualTo("_")
  }

  @Test
  fun `sanitize handles multiple consecutive invalid characters`() {
    assertThat(NativeIdentifierValidator.sanitize("a---b")).isEqualTo("a___b")
    assertThat(NativeIdentifierValidator.sanitize("test...name")).isEqualTo("test___name")
    assertThat(NativeIdentifierValidator.sanitize("a  b")).isEqualTo("a__b")
  }

  @Test
  fun `sanitize handles strings that become keywords after sanitization`() {
    // If sanitization results in a keyword, it should append underscore
    assertThat(NativeIdentifierValidator.sanitize("for-loop")).isEqualTo("for_loop")
    assertThat(NativeIdentifierValidator.sanitize("int-value")).isEqualTo("int_value")
  }

  @Test
  fun `isValid correctly identifies valid identifiers`() {
    assertThat(NativeIdentifierValidator.isValid("validName")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("_private")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("name123")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("camelCase")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("SNAKE_CASE")).isTrue()
  }

  @Test
  fun `isValid correctly identifies invalid identifiers`() {
    // Keywords
    assertThat(NativeIdentifierValidator.isValid("int")).isFalse()
    assertThat(NativeIdentifierValidator.isValid("struct")).isFalse()
    assertThat(NativeIdentifierValidator.isValid("id")).isFalse()
    assertThat(NativeIdentifierValidator.isValid("nil")).isFalse()

    // Invalid characters
    assertThat(NativeIdentifierValidator.isValid("kebab-case")).isFalse()
    assertThat(NativeIdentifierValidator.isValid("dot.notation")).isFalse()
    assertThat(NativeIdentifierValidator.isValid("123start")).isFalse()

    // Non-ASCII
    assertThat(NativeIdentifierValidator.isValid("café")).isFalse()

    // Empty
    assertThat(NativeIdentifierValidator.isValid("")).isFalse()
  }

  @Test
  fun `needsSanitization correctly identifies when sanitization is needed`() {
    // Valid names don't need sanitization
    assertThat(NativeIdentifierValidator.needsSanitization("validName")).isFalse()
    assertThat(NativeIdentifierValidator.needsSanitization("_private")).isFalse()

    // Keywords need sanitization
    assertThat(NativeIdentifierValidator.needsSanitization("int")).isTrue()
    assertThat(NativeIdentifierValidator.needsSanitization("struct")).isTrue()
    assertThat(NativeIdentifierValidator.needsSanitization("nil")).isTrue()

    // Invalid characters need sanitization
    assertThat(NativeIdentifierValidator.needsSanitization("kebab-case")).isTrue()
    assertThat(NativeIdentifierValidator.needsSanitization("123start")).isTrue()
    assertThat(NativeIdentifierValidator.needsSanitization("café")).isTrue()
    assertThat(NativeIdentifierValidator.needsSanitization("")).isTrue()
  }

  @Test
  fun `underscore is valid in C identifiers`() {
    assertThat(NativeIdentifierValidator.isValid("_")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("_var")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("my_var")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("var_")).isTrue()
    assertThat(NativeIdentifierValidator.sanitize("_private")).isEqualTo("_private")
  }

  @Test
  fun `case sensitivity is preserved`() {
    // C is case-sensitive
    assertThat(NativeIdentifierValidator.isValid("Int")).isTrue()
    assertThat(NativeIdentifierValidator.isValid("int")).isFalse()
    assertThat(NativeIdentifierValidator.sanitize("Int")).isEqualTo("Int")
    assertThat(NativeIdentifierValidator.sanitize("int")).isEqualTo("int_")
  }

  @Test
  fun `dollar sign is invalid in C identifiers`() {
    // Unlike JS, $ is not valid in C identifiers
    assertThat(NativeIdentifierValidator.isValid("\$var")).isFalse()
    assertThat(NativeIdentifierValidator.sanitize("\$var")).isEqualTo("_var")
    assertThat(NativeIdentifierValidator.sanitize("my\$var")).isEqualTo("my_var")
  }

  @Test
  fun `double underscore prefix is technically valid but special`() {
    // Names starting with __ are reserved for implementation, but syntactically valid
    assertThat(NativeIdentifierValidator.isValid("__internal")).isTrue()
    assertThat(NativeIdentifierValidator.sanitize("__internal")).isEqualTo("__internal")
  }
}
