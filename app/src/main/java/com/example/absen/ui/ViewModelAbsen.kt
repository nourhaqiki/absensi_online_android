package com.example.absen.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.absen.data.api.*
import kotlinx.coroutines.launch

/**
 * VIEWMODEL: Mengelola logika bisnis dan state UI menggunakan Coroutines.
 */
class ViewModelAbsen(private val api: ApiService) : ViewModel() {

    private val _pesanError = mutableStateOf<String?>(null)
    val pesanError: State<String?> = _pesanError

    private val _sedangMemuat = mutableStateOf(false)
    val sedangMemuat: State<Boolean> = _sedangMemuat

    fun login(email: String, sandi: String, onBerhasil: (LoginResponse) -> Unit) {
        viewModelScope.launch {
            _sedangMemuat.value = true
            _pesanError.value = null
            
            try {
                val respon = api.login(LoginRequest(email, sandi))
                
                if (respon.isSuccessful && respon.body()?.success == true) {
                    onBerhasil(respon.body()!!)
                } else {
                    _pesanError.value = respon.body()?.message ?: "Gagal Login: Cek Email/Password"
                }
            } catch (e: Exception) {
                // Menangani error jaringan
                _pesanError.value = "Koneksi Bermasalah: ${e.localizedMessage}"
            } finally {
                _sedangMemuat.value = false
            }
        }
    }

    fun kirimAbsen(token: String, type: String, lat: Double, lng: Double, onHasil: (String) -> Unit) {
        viewModelScope.launch {
            _sedangMemuat.value = true
            try {
                val tokenBearer = "Bearer $token"
                val request = AttendanceRequest(type, lat, lng)
                val respon = api.kirimAbsen(tokenBearer, request)

                if (respon.isSuccessful) {
                    onHasil(respon.body()?.message ?: "Absensi Terkirim")
                } else {
                    onHasil("Gagal Absen: ${respon.code()}")
                }
            } catch (e: Exception) {
                onHasil("Error Jaringan: ${e.message}")
            } finally {
                _sedangMemuat.value = false
            }
        }
    }
}
