package com.example.absen

import android.app.Application
import com.example.absen.data.local.BasisDataAbsen
import com.example.absen.data.api.RetrofitClient
import com.example.absen.data.api.LayananApi

/**
 * FILE APLIKASI UTAMA: AbsenApp
 * Note: Menyediakan instance Database dan API untuk seluruh aplikasi.
 */
class AbsenApp : Application() {
    // Database Lokal
    val basisData by lazy { BasisDataAbsen.dapatkanDatabase(this) }
    
    // Client API Laravel
    val layananApi: LayananApi by lazy { RetrofitClient.buatLayanan(this) }
}
