package com.example.absen.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FILE ENTITAS DATABASE: EntitasAbsensi
 * Note: File ini mendefinisikan struktur TABEL di dalam database SQLite lokal handphone.
 * Room menggunakan file ini untuk membuat tabel bernama "tabel_absensi".
 */
@Entity(tableName = "tabel_absensi")
data class EntitasAbsensi(
    @PrimaryKey val id: String, // Kunci utama unik agar data tidak tertukar (Primary Key)
    val idPengguna: Long,      // Menyimpan ID siapa yang absen
    val tipe: String,            // Menyimpan tulisan "Datang" atau "Pulang"
    val waktuMillis: Long,       // Waktu disimpan dalam angka (milidetik) agar lebih cepat diolah database
    val lintang: Double,         // Angka koordinat Latitude
    val bujur: Double,           // Angka koordinat Longitude
    val alamat: String,          // Teks alamat hasil pencarian lokasi
    val jalurFoto: String,       // Lokasi path file foto selfie
    val status: String           // Status seperti "Tepat Waktu"
)
