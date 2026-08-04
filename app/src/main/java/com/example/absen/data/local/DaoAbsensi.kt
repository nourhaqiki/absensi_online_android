package com.example.absen.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * FILE DAO (Data Access Object): DaoAbsensi
 * Note: Ini adalah kumpulan PERINTAH untuk mengakses database. 
 * Di sini kita menulis perintah SQL dalam bentuk fungsi Kotlin yang mudah dibaca.
 */
@Dao
interface DaoAbsensi {
    /**
     * PERINTAH: Mengambil semua data dari tabel_absensi.
     * ORDER BY timestamp DESC: Mengurutkan dari yang paling baru ke yang paling lama.
     * Flow: Mengizinkan aplikasi memantau data secara Realtime (otomatis update saat ada data baru).
     */
    @Query("SELECT * FROM tabel_absensi ORDER BY waktuMillis DESC")
    fun ambilSemuaLog(): Flow<List<EntitasAbsensi>>

    /**
     * PERINTAH: Menambahkan baris data baru ke dalam tabel.
     * OnConflictStrategy.REPLACE: Jika ada ID yang sama, timpa data lama dengan yang baru.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun tambahLog(absen: EntitasAbsensi)

    @Query("SELECT * FROM tabel_lokasi")
    fun ambilSemuaLokasi(): Flow<List<EntitasLokasi>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun simpanSemuaLokasi(lokasi: List<EntitasLokasi>)
}
