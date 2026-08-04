package com.example.absen.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * FILE DATABASE UTAMA: BasisDataAbsen
 * Note: File ini adalah pusat kendali database lokal HP (SQLite).
 * Di sini kita menghubungkan Entitas (tabel) dengan DAO (perintah).
 */
@Database(entities = [EntitasAbsensi::class, EntitasLokasi::class], version = 2, exportSchema = false)
abstract class BasisDataAbsen : RoomDatabase() {
    
    // Fungsi untuk mendapatkan akses ke kumpulan perintah DAO
    abstract fun daoAbsensi(): DaoAbsensi

    companion object {
        @Volatile
        private var INSTANCE: BasisDataAbsen? = null

        /**
         * FUNGSI: Mendapatkan atau membuat database baru.
         * Singleton Pattern: Memastikan hanya ada 1 koneksi database di seluruh aplikasi untuk menghemat memori.
         */
        fun dapatkanDatabase(konteks: Context): BasisDataAbsen {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    konteks.applicationContext,
                    BasisDataAbsen::class.java,
                    "basis_data_absen" // Nama file database di memori HP
                )
                .fallbackToDestructiveMigration() // Menghapus data lama jika versi berubah (agar mudah migrasi saat pengembangan)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
