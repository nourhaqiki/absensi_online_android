package com.example.absen.data.model

import com.google.gson.annotations.SerializedName

/**
 * FILE MODEL DATA: Pengguna
 * Note: Menyimpan informasi profil karyawan sesuai tabel users di database.
 */
data class Pengguna(
    val id: Long = 0,
    @SerializedName("name") val nama: String = "",
    val email: String = "",
    val nip: String? = null,
    @SerializedName("position") val jabatan: String? = null,
    @SerializedName("phone") val noHp: String? = null,
    @SerializedName("active") val aktif: Int = 1,
    @SerializedName("photo") val foto: String? = null,
    @SerializedName("email_verified_at") val emailDiverifikasiPada: String? = null,
    @SerializedName("created_at") val dibuatPada: String? = null,
    @SerializedName("updated_at") val diperbaruiPada: String? = null
)
