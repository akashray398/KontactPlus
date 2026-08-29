package com.akash.kontactplus.core.telecom

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the Dialer role and Telecom service availability.
 */
@Singleton
class TelecomRoleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val roleManager: RoleManager? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.getSystemService(RoleManager::class.java)
    } else {
        null
    }

    private val telecomManager: TelecomManager? = context.getSystemService(TelecomManager::class.java)

    /**
     * Checks whether Telecom is supported on this device.
     */
    fun isTelecomSupported(): Boolean {
        return telecomManager != null
    }

    /**
     * Checks the current state of the Dialer role.
     */
    fun getDialerRoleState(): DialerRoleState {
        if (!isTelecomSupported()) return DialerRoleState.Unsupported
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val isHeld = roleManager?.isRoleHeld(RoleManager.ROLE_DIALER) == true
            if (isHeld) DialerRoleState.Held else DialerRoleState.NotHeld
        } else {
            // For pre-Q devices, we check if we are the default dialer through TelecomManager
            val defaultDialer = telecomManager?.defaultDialerPackage
            if (defaultDialer == context.packageName) DialerRoleState.Held else DialerRoleState.NotHeld
        }
    }

    /**
     * Creates an intent to request the Dialer role.
     */
    fun createRoleRequestIntent(): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            roleManager?.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        } else {
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
            }
        }
    }
}
