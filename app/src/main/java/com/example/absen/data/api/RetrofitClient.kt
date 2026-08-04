package com.example.absen.data.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RETROFIT CLIENT: Konfigurasi koneksi HTTP dan header wajib.
 */
object RetrofitClient {
    // GANTI DENGAN LINK NGROK TERBARU ANDA
    private const val BASE_URL = "https://salary-prelaw-seclusion.ngrok-free.dev/api/"

    fun buatLayanan(context: Context): LayananApi {
        val managerSesi = ManagerSesi(context)

        // Interceptor untuk Header Wajib (ngrok bypass & JSON Accept & Auth Token)
        val interceptorHeader = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("ngrok-skip-browser-warning", "true")
                .addHeader("Accept", "application/json")
            
            // Ambil token dari session manager secara otomatis
            val token = managerSesi.ambilToken()
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            
            chain.proceed(requestBuilder.build())
        }

        // Logging Interceptor untuk mempermudah Debug di Logcat
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptorHeader)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(LayananApi::class.java)
    }
}
