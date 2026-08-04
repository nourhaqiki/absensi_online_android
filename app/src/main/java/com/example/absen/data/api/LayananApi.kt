package com.example.absen.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * INTERFACE API: LayananApi
 * Note: Definisi endpoint Laravel untuk Absensi dan Manajemen User.
 */
interface LayananApi {

    @POST("login")
    suspend fun login(
        @Body body: Map<String, String>
    ): Response<ResponLogin>

    @GET("user-profile")
    suspend fun ambilProfil(): Response<ResponProfil>

    @Multipart
    @POST("user-update")
    suspend fun perbaruiProfil(
        @Part("name") nama: RequestBody,
        @Part("nip") nip: RequestBody,
        @Part("position") jabatan: RequestBody,
        @Part("phone") noHp: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<ResponUpdateProfil>

    @Multipart
    @POST("absen")
    suspend fun kirimAbsen(
        @Part("latitude") lat: RequestBody,
        @Part("longitude") lng: RequestBody,
        @Part("tipe") tipe: RequestBody,
        @Part("location_id") idLokasi: RequestBody,
        @Part foto: MultipartBody.Part
    ): Response<ResponAbsen>

    @GET("riwayat-absen")
    suspend fun ambilRiwayat(): Response<ResponRiwayat>

    @GET("locations")
    suspend fun ambilLokasi(): Response<ResponLokasi>
}
