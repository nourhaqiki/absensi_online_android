package com.example.absen.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * INTERFACE API: Definisi endpoint untuk interaksi dengan Laravel.
 */
interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("attendance")
    suspend fun kirimAbsen(
        @Header("Authorization") token: String,
        @Body request: AttendanceRequest
    ): Response<AttendanceResponse>

    @GET("user-profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String // Isi dengan: "Bearer " + token_anda
    ): Response<UserProfileResponse>
}
