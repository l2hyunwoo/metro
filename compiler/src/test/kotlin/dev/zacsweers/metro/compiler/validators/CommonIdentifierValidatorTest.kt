// Copyright (C) 2015 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler.validators

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CommonIdentifierValidatorTest {

  @Test
  fun `valid ASCII identifier passes validation`() {
    assertThat(CommonIdentifierValidator.isValid("myVariable")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("_privateVar")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("variable123")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("CONSTANT_VALUE")).isTrue()
  }

  @Test
  fun `sanitize returns valid identifiers unchanged`() {
    assertThat(CommonIdentifierValidator.sanitize("validName")).isEqualTo("validName")
    assertThat(CommonIdentifierValidator.sanitize("_underscore")).isEqualTo("_underscore")
    assertThat(CommonIdentifierValidator.sanitize("camelCase123")).isEqualTo("camelCase123")
    assertThat(CommonIdentifierValidator.sanitize("SCREAMING_SNAKE")).isEqualTo("SCREAMING_SNAKE")
  }

  @Test
  fun `sanitize handles all JS keywords`() {
    // ES5 keywords
    assertThat(CommonIdentifierValidator.sanitize("break")).isEqualTo("break_")
    assertThat(CommonIdentifierValidator.sanitize("case")).isEqualTo("case_")
    assertThat(CommonIdentifierValidator.sanitize("catch")).isEqualTo("catch_")
    assertThat(CommonIdentifierValidator.sanitize("continue")).isEqualTo("continue_")
    assertThat(CommonIdentifierValidator.sanitize("debugger")).isEqualTo("debugger_")
    assertThat(CommonIdentifierValidator.sanitize("default")).isEqualTo("default_")
    assertThat(CommonIdentifierValidator.sanitize("delete")).isEqualTo("delete_")
    assertThat(CommonIdentifierValidator.sanitize("do")).isEqualTo("do_")
    assertThat(CommonIdentifierValidator.sanitize("else")).isEqualTo("else_")
    assertThat(CommonIdentifierValidator.sanitize("finally")).isEqualTo("finally_")
    assertThat(CommonIdentifierValidator.sanitize("for")).isEqualTo("for_")
    assertThat(CommonIdentifierValidator.sanitize("function")).isEqualTo("function_")
    assertThat(CommonIdentifierValidator.sanitize("if")).isEqualTo("if_")
    assertThat(CommonIdentifierValidator.sanitize("in")).isEqualTo("in_")
    assertThat(CommonIdentifierValidator.sanitize("instanceof")).isEqualTo("instanceof_")
    assertThat(CommonIdentifierValidator.sanitize("new")).isEqualTo("new_")
    assertThat(CommonIdentifierValidator.sanitize("return")).isEqualTo("return_")
    assertThat(CommonIdentifierValidator.sanitize("switch")).isEqualTo("switch_")
    assertThat(CommonIdentifierValidator.sanitize("this")).isEqualTo("this_")
    assertThat(CommonIdentifierValidator.sanitize("throw")).isEqualTo("throw_")
    assertThat(CommonIdentifierValidator.sanitize("try")).isEqualTo("try_")
    assertThat(CommonIdentifierValidator.sanitize("typeof")).isEqualTo("typeof_")
    assertThat(CommonIdentifierValidator.sanitize("var")).isEqualTo("var_")
    assertThat(CommonIdentifierValidator.sanitize("void")).isEqualTo("void_")
    assertThat(CommonIdentifierValidator.sanitize("while")).isEqualTo("while_")
    assertThat(CommonIdentifierValidator.sanitize("with")).isEqualTo("with_")

    // ES6 keywords
    assertThat(CommonIdentifierValidator.sanitize("class")).isEqualTo("class_")
    assertThat(CommonIdentifierValidator.sanitize("const")).isEqualTo("const_")
    assertThat(CommonIdentifierValidator.sanitize("enum")).isEqualTo("enum_")
    assertThat(CommonIdentifierValidator.sanitize("export")).isEqualTo("export_")
    assertThat(CommonIdentifierValidator.sanitize("extends")).isEqualTo("extends_")
    assertThat(CommonIdentifierValidator.sanitize("import")).isEqualTo("import_")
    assertThat(CommonIdentifierValidator.sanitize("super")).isEqualTo("super_")

    // Future reserved words
    assertThat(CommonIdentifierValidator.sanitize("implements")).isEqualTo("implements_")
    assertThat(CommonIdentifierValidator.sanitize("interface")).isEqualTo("interface_")
    assertThat(CommonIdentifierValidator.sanitize("let")).isEqualTo("let_")
    assertThat(CommonIdentifierValidator.sanitize("package")).isEqualTo("package_")
    assertThat(CommonIdentifierValidator.sanitize("private")).isEqualTo("private_")
    assertThat(CommonIdentifierValidator.sanitize("protected")).isEqualTo("protected_")
    assertThat(CommonIdentifierValidator.sanitize("public")).isEqualTo("public_")
    assertThat(CommonIdentifierValidator.sanitize("static")).isEqualTo("static_")
    assertThat(CommonIdentifierValidator.sanitize("yield")).isEqualTo("yield_")

    // Special identifiers
    assertThat(CommonIdentifierValidator.sanitize("eval")).isEqualTo("eval_")
    assertThat(CommonIdentifierValidator.sanitize("arguments")).isEqualTo("arguments_")
  }

  @Test
  fun `sanitize handles all C keywords`() {
    assertThat(CommonIdentifierValidator.sanitize("auto")).isEqualTo("auto_")
    assertThat(CommonIdentifierValidator.sanitize("char")).isEqualTo("char_")
    assertThat(CommonIdentifierValidator.sanitize("double")).isEqualTo("double_")
    assertThat(CommonIdentifierValidator.sanitize("extern")).isEqualTo("extern_")
    assertThat(CommonIdentifierValidator.sanitize("float")).isEqualTo("float_")
    assertThat(CommonIdentifierValidator.sanitize("goto")).isEqualTo("goto_")
    assertThat(CommonIdentifierValidator.sanitize("inline")).isEqualTo("inline_")
    assertThat(CommonIdentifierValidator.sanitize("int")).isEqualTo("int_")
    assertThat(CommonIdentifierValidator.sanitize("long")).isEqualTo("long_")
    assertThat(CommonIdentifierValidator.sanitize("register")).isEqualTo("register_")
    assertThat(CommonIdentifierValidator.sanitize("restrict")).isEqualTo("restrict_")
    assertThat(CommonIdentifierValidator.sanitize("short")).isEqualTo("short_")
    assertThat(CommonIdentifierValidator.sanitize("signed")).isEqualTo("signed_")
    assertThat(CommonIdentifierValidator.sanitize("sizeof")).isEqualTo("sizeof_")
    assertThat(CommonIdentifierValidator.sanitize("struct")).isEqualTo("struct_")
    assertThat(CommonIdentifierValidator.sanitize("typedef")).isEqualTo("typedef_")
    assertThat(CommonIdentifierValidator.sanitize("union")).isEqualTo("union_")
    assertThat(CommonIdentifierValidator.sanitize("unsigned")).isEqualTo("unsigned_")
    assertThat(CommonIdentifierValidator.sanitize("volatile")).isEqualTo("volatile_")
    assertThat(CommonIdentifierValidator.sanitize("_Bool")).isEqualTo("_Bool_")
    assertThat(CommonIdentifierValidator.sanitize("_Complex")).isEqualTo("_Complex_")
    assertThat(CommonIdentifierValidator.sanitize("_Imaginary")).isEqualTo("_Imaginary_")
  }

  @Test
  fun `sanitize handles all Objective-C keywords`() {
    assertThat(CommonIdentifierValidator.sanitize("id")).isEqualTo("id_")
    assertThat(CommonIdentifierValidator.sanitize("Class")).isEqualTo("Class_")
    assertThat(CommonIdentifierValidator.sanitize("SEL")).isEqualTo("SEL_")
    assertThat(CommonIdentifierValidator.sanitize("IMP")).isEqualTo("IMP_")
    assertThat(CommonIdentifierValidator.sanitize("BOOL")).isEqualTo("BOOL_")
    assertThat(CommonIdentifierValidator.sanitize("nil")).isEqualTo("nil_")
    assertThat(CommonIdentifierValidator.sanitize("Nil")).isEqualTo("Nil_")
    assertThat(CommonIdentifierValidator.sanitize("YES")).isEqualTo("YES_")
    assertThat(CommonIdentifierValidator.sanitize("NO")).isEqualTo("NO_")
    assertThat(CommonIdentifierValidator.sanitize("self")).isEqualTo("self_")
    assertThat(CommonIdentifierValidator.sanitize("_cmd")).isEqualTo("_cmd_")
    assertThat(CommonIdentifierValidator.sanitize("implementation")).isEqualTo("implementation_")
    assertThat(CommonIdentifierValidator.sanitize("protocol")).isEqualTo("protocol_")
    assertThat(CommonIdentifierValidator.sanitize("end")).isEqualTo("end_")
    assertThat(CommonIdentifierValidator.sanitize("synthesize")).isEqualTo("synthesize_")
    assertThat(CommonIdentifierValidator.sanitize("dynamic")).isEqualTo("dynamic_")
    assertThat(CommonIdentifierValidator.sanitize("property")).isEqualTo("property_")
    assertThat(CommonIdentifierValidator.sanitize("optional")).isEqualTo("optional_")
    assertThat(CommonIdentifierValidator.sanitize("required")).isEqualTo("required_")
  }

  @Test
  fun `sanitize handles JVM dangerous characters`() {
    assertThat(CommonIdentifierValidator.sanitize("a.b")).isEqualTo("a_b")
    assertThat(CommonIdentifierValidator.sanitize("a;b")).isEqualTo("a_b")
    assertThat(CommonIdentifierValidator.sanitize("a/b")).isEqualTo("a_b")
    assertThat(CommonIdentifierValidator.sanitize("a<b")).isEqualTo("a_b")
    assertThat(CommonIdentifierValidator.sanitize("a>b")).isEqualTo("a_b")
    assertThat(CommonIdentifierValidator.sanitize("a[b")).isEqualTo("a_b")
    assertThat(CommonIdentifierValidator.sanitize("a]b")).isEqualTo("a_b")
  }

  @Test
  fun `sanitize handles invalid start characters`() {
    // Digits cannot start identifier
    assertThat(CommonIdentifierValidator.sanitize("123abc")).isEqualTo("_123abc")
    assertThat(CommonIdentifierValidator.sanitize("9variable")).isEqualTo("_9variable")

    // Special characters that cannot start
    assertThat(CommonIdentifierValidator.sanitize("-kebab")).isEqualTo("_kebab")
    assertThat(CommonIdentifierValidator.sanitize("@annotation")).isEqualTo("_annotation")
    assertThat(CommonIdentifierValidator.sanitize("\$dollar")).isEqualTo("_dollar")
  }

  @Test
  fun `sanitize handles invalid middle characters`() {
    assertThat(CommonIdentifierValidator.sanitize("kebab-case")).isEqualTo("kebab_case")
    assertThat(CommonIdentifierValidator.sanitize("dot.notation")).isEqualTo("dot_notation")
    assertThat(CommonIdentifierValidator.sanitize("space name")).isEqualTo("space_name")
    assertThat(CommonIdentifierValidator.sanitize("hash#tag")).isEqualTo("hash_tag")
  }

  @Test
  fun `sanitize handles non-ASCII characters conservatively`() {
    // COMMON validator is most conservative - ASCII only
    assertThat(CommonIdentifierValidator.sanitize("café")).isEqualTo("caf_")
    assertThat(CommonIdentifierValidator.sanitize("münchen")).isEqualTo("m_nchen")
    assertThat(CommonIdentifierValidator.sanitize("日本語")).isEqualTo("___")
    assertThat(CommonIdentifierValidator.sanitize("Москва")).isEqualTo("______")
  }

  @Test
  fun `sanitize handles surrogate pairs`() {
    // Emoji (surrogate pair) should be replaced with single underscore
    assertThat(CommonIdentifierValidator.sanitize("test🚀data")).isEqualTo("test_data")
    assertThat(CommonIdentifierValidator.sanitize("a🍺b")).isEqualTo("a_b")
  }

  @Test
  fun `sanitize handles empty and whitespace strings`() {
    assertThat(CommonIdentifierValidator.sanitize("")).isEqualTo("_")
    assertThat(CommonIdentifierValidator.sanitize("   ")).isEqualTo("_")
    assertThat(CommonIdentifierValidator.sanitize("\t")).isEqualTo("_")
    assertThat(CommonIdentifierValidator.sanitize("\n")).isEqualTo("_")
  }

  @Test
  fun `sanitize handles multiple consecutive invalid characters`() {
    assertThat(CommonIdentifierValidator.sanitize("a---b")).isEqualTo("a___b")
    assertThat(CommonIdentifierValidator.sanitize("test...name")).isEqualTo("test___name")
    assertThat(CommonIdentifierValidator.sanitize("a  b")).isEqualTo("a__b")
  }

  @Test
  fun `isValid correctly identifies valid identifiers`() {
    assertThat(CommonIdentifierValidator.isValid("validName")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("_private")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("name123")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("camelCase")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("SNAKE_CASE")).isTrue()
  }

  @Test
  fun `isValid correctly identifies invalid identifiers`() {
    // Any platform keywords
    assertThat(CommonIdentifierValidator.isValid("for")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("int")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("eval")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("nil")).isFalse()

    // Invalid characters
    assertThat(CommonIdentifierValidator.isValid("kebab-case")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("dot.notation")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("123start")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("\$dollar")).isFalse()

    // Non-ASCII
    assertThat(CommonIdentifierValidator.isValid("café")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("日本語")).isFalse()

    // Empty
    assertThat(CommonIdentifierValidator.isValid("")).isFalse()
  }

  @Test
  fun `needsSanitization correctly identifies when sanitization is needed`() {
    // Valid names don't need sanitization
    assertThat(CommonIdentifierValidator.needsSanitization("validName")).isFalse()
    assertThat(CommonIdentifierValidator.needsSanitization("_private")).isFalse()

    // Any platform keywords need sanitization
    assertThat(CommonIdentifierValidator.needsSanitization("for")).isTrue()
    assertThat(CommonIdentifierValidator.needsSanitization("int")).isTrue()
    assertThat(CommonIdentifierValidator.needsSanitization("eval")).isTrue()
    assertThat(CommonIdentifierValidator.needsSanitization("nil")).isTrue()

    // Invalid characters need sanitization
    assertThat(CommonIdentifierValidator.needsSanitization("kebab-case")).isTrue()
    assertThat(CommonIdentifierValidator.needsSanitization("123start")).isTrue()
    assertThat(CommonIdentifierValidator.needsSanitization("\$dollar")).isTrue()
    assertThat(CommonIdentifierValidator.needsSanitization("café")).isTrue()
    assertThat(CommonIdentifierValidator.needsSanitization("")).isTrue()
  }

  @Test
  fun `underscore is valid in common identifiers`() {
    assertThat(CommonIdentifierValidator.isValid("_")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("_var")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("my_var")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("var_")).isTrue()
    assertThat(CommonIdentifierValidator.sanitize("_private")).isEqualTo("_private")
  }

  @Test
  fun `case sensitivity is preserved`() {
    assertThat(CommonIdentifierValidator.isValid("For")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("for")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("Int")).isTrue()
    assertThat(CommonIdentifierValidator.isValid("int")).isFalse()
    assertThat(CommonIdentifierValidator.sanitize("For")).isEqualTo("For")
    assertThat(CommonIdentifierValidator.sanitize("for")).isEqualTo("for_")
  }

  @Test
  fun `dollar sign is invalid in common identifiers`() {
    // $ is valid in JS but not in C/Native, so COMMON blocks it
    assertThat(CommonIdentifierValidator.isValid("\$")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("\$var")).isFalse()
    assertThat(CommonIdentifierValidator.sanitize("\$var")).isEqualTo("_var")
    assertThat(CommonIdentifierValidator.sanitize("my\$var")).isEqualTo("my_var")
  }

  @Test
  fun `most conservative validator blocks more than platform-specific validators`() {
    // These would be valid in JS but not in COMMON
    assertThat(CommonIdentifierValidator.isValid("café")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("\$jquery")).isFalse()
    assertThat(CommonIdentifierValidator.isValid("日本語")).isFalse()

    // This would be valid in JVM but not in COMMON (C keyword)
    assertThat(CommonIdentifierValidator.isValid("myInt")).isTrue() // 'myInt' is fine
    assertThat(CommonIdentifierValidator.isValid("int")).isFalse() // but 'int' alone is blocked
  }

  @Test
  fun `COMMON validator provides cross-platform safety`() {
    // A name valid in COMMON should be valid in all platforms
    val crossPlatformSafeName = "myVariable123"
    assertThat(CommonIdentifierValidator.isValid(crossPlatformSafeName)).isTrue()
    assertThat(JvmIdentifierValidator.isValid(crossPlatformSafeName)).isTrue()
    assertThat(JsIdentifierValidator.isValid(crossPlatformSafeName)).isTrue()
    assertThat(NativeIdentifierValidator.isValid(crossPlatformSafeName)).isTrue()
  }
}
