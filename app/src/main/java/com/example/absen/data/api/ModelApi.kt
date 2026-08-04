package com.example.absen.data.api

import com.example.absen.data.model.Pengguna
import com.example.absen.data.model.Lokasi
import com.example.absen.data.model.Presensi
import com.google.gson.annotations.SerializedName

/**
 * FILE MODEL API: ModelApi
 * Note: Mendefinisikan struktur JSON untuk integrasi dengan Backend.
 */

// Response saat Login Berhasil
data class ResponLogin(
    @SerializedName("status") val status: String,
    @SerializedName("message") val pesan: String,
    @SerializedName("data") val data: DataLogin?
)

data class DataLogin(
    @SerializedName("token") val token: String?,
    @SerializedName("user") val dataUser: Pengguna?
)

// Response Profil User
data class ResponProfil(
    @SerializedName("status") val status: String,
    @SerializedName("message") val pesan: String,
    @SerializedName("data") val dataUser: Pengguna?
)

// Response Update Profil
data class ResponUpdateProfil(
    @SerializedName("status") val status: String,
    @SerializedName("message") val pesan: String,
    @SerializedName("data") val dataUser: Pengguna?
)

// Response saat Kirim Absen
data class ResponAbsen(
    @SerializedName("status") val status: String,
    @SerializedName("message") val pesan: String
)

// Response Daftar Lokasi
data class ResponLokasi(
    @SerializedName("status") val status: String,
    @SerializedName("message") val pesan: String,
    @SerializedName("data") val daftarLokasi: List<Lokasi>?
)

// Response Daftar Riwayat Absensi
data class ResponRiwayat(
    @SerializedName("status") val status: String,
    @SerializedName("message") val pesan: String,
    @SerializedName("data") val data: List<ItemRiwayatApi>?
)

// Item Riwayat dari API (Sekarang mencakup data Masuk & Pulang)
data class ItemRiwayatApi(
    val id: Long,
    val tanggal: String,
    val jam_masuk: String?,
    val jam_pulang: String?,
    val foto_masuk_url: String?,
    val foto_pulang_url: String?,
    val lat_masuk: String?,
    val long_masuk: String?,
    val lat_pulang: String?,
    val long_pulang: String?,
    val created_at: String
)
