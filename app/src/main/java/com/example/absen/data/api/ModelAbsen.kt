package com.example.absen.data.api

import com.google.gson.annotations.SerializedName

/**
 * DATA MODELS: Struktur data sesuai dengan spesifikasi Backend Laravel.
 */

// User Data
data class UserData(
    val id: Int,
    val name: String,
    val email: String
)

// Login
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserData?
)

// Attendance (Absensi)
data class AttendanceRequest(
    val type: String, // 'in' atau 'out'
    val latitude: Double,
    val longitude: Double
)

data class AttendanceResponse(
    val success: Boolean,
    val message: String
)

// User Profile
data class UserProfileResponse(
    val success: Boolean,
    val message: String,
    val user: UserData?
)
