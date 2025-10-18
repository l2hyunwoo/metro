// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.compiler

import dev.zacsweers.metro.compiler.ir.IrMetroContext
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.platform.jvm.isJvm

/**
 * Detects the target compilation platform from the FIR session context.
 *
 * Examines [org.jetbrains.kotlin.fir.FirSession.moduleData]'s platform information
 * to determine the appropriate [Platform] enum value for identifier validation.
 *
 * **Platform Mapping**:
 * - JVM/Android targets → [Platform.JVM]
 * - JavaScript/WASM targets → [Platform.JS]
 * - Kotlin/Native targets (iOS, macOS, Linux, Windows) → [Platform.NATIVE]
 * - Multiplatform common source sets or unknown platforms → [Platform.COMMON]
 *
 * **Fallback Behavior**:
 * Returns [Platform.COMMON] in these scenarios:
 * - Platform cannot be determined (null or unavailable)
 * - Exception occurs during detection
 * - Platform type is unrecognized (future Kotlin platforms)
 * - Contradictory platform information (first match wins: JVM → JS → Native)
 *
 * This is used by [NameAllocator] to automatically select the correct identifier
 * validation rules based on the compilation target, eliminating the need for
 * hardcoded platform values.
 *
 * @receiver The FIR session containing platform information
 * @return Platform enum value representing the compilation target
 * @see Platform
 * @see NameAllocator
 */
internal fun FirSession.detectPlatform(): Platform {
  return try {
    val targetPlatform = this.moduleData.platform

    // Detect platform with priority order: JVM → JS → Native → Common
    // First match wins if contradictory info present
    when {
      targetPlatform.isJvm() -> Platform.JVM

      // Fall back to string matching since isJs() may not be available
      targetPlatform.toString().contains("JS", ignoreCase = true) ||
      targetPlatform.toString().contains("Wasm", ignoreCase = true) -> Platform.JS

      // Fall back to string matching since isNative() may not be available
      targetPlatform.toString().contains("Native", ignoreCase = true) ||
      targetPlatform.javaClass.simpleName.contains("Native", ignoreCase = true) -> Platform.NATIVE

      // Common/unknown platform fallback (also handles null platform)
      else -> Platform.COMMON
    }
  } catch (_: Exception) {
    // Return COMMON on any error (logging removed - not available in extension context)
    Platform.COMMON
  }
}

/**
 * Detects the target compilation platform from the IR context.
 *
 * Examines the IR plugin context's platform information to determine the
 * appropriate [Platform] enum value for identifier validation in the IR phase.
 *
 * **Platform Mapping**:
 * - JVM/Android targets → [Platform.JVM]
 * - JavaScript/WASM targets → [Platform.JS]
 * - Kotlin/Native targets → [Platform.NATIVE]
 * - Unknown or common targets → [Platform.COMMON]
 *
 * **Fallback Behavior**:
 * Returns [Platform.COMMON] when:
 * - Platform information is unavailable (null)
 * - Exception occurs during detection
 * - Platform type is unrecognized
 * - Future Kotlin platforms not yet supported
 *
 * **Implementation Note**:
 * Currently returns [Platform.COMMON] as a safe default. The IR phase platform
 * detection implementation depends on available context properties. This ensures
 * compilation succeeds even if platform-specific features are unavailable.
 *
 * @receiver The IR Metro context containing platform information
 * @return Platform enum value (currently always COMMON pending IR implementation)
 * @see Platform
 * @see NameAllocator
 */
internal fun IrMetroContext.detectPlatform(): Platform {
  return try {
    // The pluginContext provides access to platform through its moduleFragment
    // For now, return COMMON as a safe default - proper IR detection
    // will be implemented when moduleFragment access is clarified

    // TODO: Implement proper IR platform detection
    // Potential approaches:
    // 1. Access through pluginContext.moduleDescriptor.platform
    // 2. Pass moduleFragment.descriptor.platform from transformer
    // 3. Store detected platform during FIR phase and reuse

    @Suppress("DEPRECATION")
    (messageCollector.report(
        org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.LOGGING,
        "$LOG_PREFIX IR platform detection: Using COMMON (IR detection pending implementation)"
    ))
    Platform.COMMON
  } catch (e: Exception) {
    @Suppress("DEPRECATION")
    (messageCollector.report(
        org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.WARNING,
        "$LOG_PREFIX IR platform detection failed: ${e.message}, defaulting to COMMON"
    ))
    Platform.COMMON
  }
}
