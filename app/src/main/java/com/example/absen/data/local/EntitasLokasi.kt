package com.example.absen.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FILE ENTITAS DATABASE: EntitasLokasi
 * Note: Menyimpan data lokasi kantor/titik absensi secara lokal.
 */
@Entity(tableName = "tabel_lokasi")
data class EntitasLokasi(
    @PrimaryKey val id: Long,
    val nama: String,
    val alamat: String?,
    val latitude: Double?,
    val longitude: Double?,
    val radiusMeter: Int,
    val aktif: Int
)
