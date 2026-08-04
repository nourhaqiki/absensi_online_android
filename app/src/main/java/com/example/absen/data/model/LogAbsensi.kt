package com.example.absen.data.model

import java.util.Date

/**
 * FILE MODEL DATA: LogAbsensi
 * Note: Ini adalah struktur data murni yang digunakan untuk menampilkan informasi di layar aplikasi.
 * File ini tidak berhubungan langsung dengan database, hanya sebagai wadah data untuk UI.
 */
data class LogAbsensi(
    val id: String = "",          // ID unik untuk membedakan baris riwayat
    val idPengguna: Long = 0,  // ID karyawan yang sedang login
    val tipe: String = "Datang",  // Menandakan jenis absen ("Datang" atau "Pulang")
    val waktu: Date = Date(),     // Waktu lengkap saat menekan tombol absen
    val lintang: Double = 0.0,    // Koordinat Latitude dari sensor GPS
    val bujur: Double = 0.0,      // Koordinat Longitude dari sensor GPS
    val alamat: String = "-",     // Nama lokasi/jalan yang diterjemahkan dari koordinat
    val jalurFoto: String = "",   // Alamat (path) lokasi file foto selfie disimpan di memori HP
    val status: String = "Tepat Waktu" // Keterangan tambahan mengenai kehadiran
)
