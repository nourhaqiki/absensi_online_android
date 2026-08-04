package com.example.absen.data.api

import android.content.Context
import android.content.SharedPreferences

/**
 * MANAGER SESI: Mengelola penyimpanan Token JWT di SharedPreferences.
 */
class ManagerSesi(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sesi_absen", Context.MODE_PRIVATE)

    fun simpanToken(token: String) {
        prefs.edit().putString("token_jwt", token).apply()
    }

    fun ambilToken(): String? {
        return prefs.getString("token_jwt", null)
    }

    fun hapusSesi() {
        prefs.edit().clear().apply()
    }
}
