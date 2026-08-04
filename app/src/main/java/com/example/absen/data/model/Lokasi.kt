package com.example.absen.data.model

import com.google.gson.annotations.SerializedName

/**
 * FILE MODEL DATA: Lokasi
 * Note: Menyimpan data lokasi untuk absensi sesuai tabel locations di database.
 */
data class Lokasi(
    val id: Long = 0,
    @SerializedName("name") val nama: String = "",
    @SerializedName("address") val alamat: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("radius_meter") val radiusMeter: Int = 100,
    @SerializedName("active") val aktif: Int = 1,
    @SerializedName("created_at") val dibuatPada: String? = null,
    @SerializedName("updated_at") val diperbaruiPada: String? = null
)
