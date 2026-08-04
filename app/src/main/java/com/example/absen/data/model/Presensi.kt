package com.example.absen.data.model

import com.google.gson.annotations.SerializedName

/**
 * FILE MODEL DATA: Presensi
 * Note: Menyimpan data riwayat absensi sesuai tabel presensis di database.
 */
data class Presensi(
    val id: Long = 0,
    @SerializedName("user_id") val idPengguna: Long = 0,
    val tanggal: String = "",
    @SerializedName("jam_masuk") val jamMasuk: String? = null,
    @SerializedName("jam_pulang") val jamPulang: String? = null,
    @SerializedName("foto_masuk") val fotoMasuk: String? = null,
    @SerializedName("foto_pulang") val fotoPulang: String? = null,
    val status: String? = null,
    val notes: String? = null,
    @SerializedName("lat_masuk") val latMasuk: String? = null,
    @SerializedName("long_masuk") val longMasuk: String? = null,
    @SerializedName("lat_pulang") val latPulang: String? = null,
    @SerializedName("long_pulang") val longPulang: String? = null,
    @SerializedName("location_id") val idLokasi: Long? = null,
    @SerializedName("created_at") val dibuatPada: String? = null,
    @SerializedName("updated_at") val diperbaruiPada: String? = null
)
