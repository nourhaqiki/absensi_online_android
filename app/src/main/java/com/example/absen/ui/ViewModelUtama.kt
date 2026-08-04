package com.example.absen.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.absen.data.api.LayananApi
import com.example.absen.data.api.ManagerSesi
import com.example.absen.data.local.DaoAbsensi
import com.example.absen.data.local.EntitasAbsensi
import com.example.absen.data.local.EntitasLokasi
import com.example.absen.data.model.LogAbsensi
import com.example.absen.data.model.Pengguna
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * FILE LOGIKA BISNIS: ViewModelUtama
 * Note: Mengelola alur data dari Database Lokal (Room) dan API Laravel.
 */
class ViewModelUtama(
    private val daoAbsensi: DaoAbsensi,
    private val api: LayananApi,
    private val managerSesi: ManagerSesi
) : ViewModel() {

    private val _penggunaSaatIni = MutableStateFlow<Pengguna?>(null)
    val penggunaSaatIni: StateFlow<Pengguna?> = _penggunaSaatIni

    private val _sedangMemuat = MutableStateFlow(false)
    val sedangMemuat: StateFlow<Boolean> = _sedangMemuat

    /**
     * ALIRAN DATA: Lokasi dari Database Lokal
     */
    val daftarLokasi: StateFlow<List<EntitasLokasi>> = daoAbsensi.ambilSemuaLokasi()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _riwayatOnline = MutableStateFlow<List<LogAbsensi>>(emptyList())
    val riwayatOnline: StateFlow<List<LogAbsensi>> = _riwayatOnline

    private val _pesanErrorLogin = MutableStateFlow<String?>(null)
    val pesanErrorLogin: StateFlow<String?> = _pesanErrorLogin

    private val _waktuBlokirSampai = MutableStateFlow(0L)
    val waktuBlokirSampai: StateFlow<Long> = _waktuBlokirSampai

    init {
        val tokenTersimpan = managerSesi.ambilToken()
        if (tokenTersimpan != null) {
            // Ambil data profil terbaru dari server saat aplikasi dibuka
            ambilProfil()
            ambilLokasi()
        }
    }

    /**
     * FUNGSI: Mengambil Profil User dari Laravel
     */
    fun ambilProfil() {
        viewModelScope.launch {
            _sedangMemuat.value = true
            try {
                val respon = api.ambilProfil()
                if (respon.isSuccessful && respon.body()?.status == "success") {
                    _penggunaSaatIni.value = respon.body()?.dataUser
                    // Setelah profil berhasil diambil, ambil juga riwayat spesifik user ini
                    ambilRiwayatOnline()
                }
            } catch (e: Exception) {
                Log.e("API", "Gagal ambil profil: ${e.message}")
            } finally {
                _sedangMemuat.value = false
            }
        }
    }

    /**
     * FUNGSI: Mengambil Daftar Lokasi dari Backend dan Simpan ke Lokal
     */
    fun ambilLokasi() {
        viewModelScope.launch {
            try {
                val respon = api.ambilLokasi()
                if (respon.isSuccessful && respon.body()?.status == "success") {
                    val listApi = respon.body()?.daftarLokasi ?: emptyList()
                    val listEntitas = listApi.map {
                        EntitasLokasi(
                            id = it.id,
                            nama = it.nama,
                            alamat = it.alamat,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            radiusMeter = it.radiusMeter,
                            aktif = it.aktif
                        )
                    }
                    daoAbsensi.simpanSemuaLokasi(listEntitas)
                }
            } catch (e: Exception) {
                Log.e("API", "Gagal ambil lokasi: ${e.message}")
            }
        }
    }

    /**
     * FUNGSI: Mengambil Riwayat Absensi khusus user yang sedang login dari Backend
     */
    fun ambilRiwayatOnline() {
        viewModelScope.launch {
            _sedangMemuat.value = true
            try {
                val respon = api.ambilRiwayat()
                if (respon.isSuccessful && respon.body()?.data != null) {
                    val listApi = respon.body()?.data ?: emptyList()
                    val formatTgl = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    
                    val listHasil = mutableListOf<LogAbsensi>()
                    
                    listApi.forEach { item ->
                        // Tambahkan data Masuk jika ada
                        if (!item.jam_masuk.isNullOrEmpty()) {
                            listHasil.add(LogAbsensi(
                                id = "${item.id}_masuk",
                                tipe = "Datang",
                                waktu = try { formatTgl.parse("${item.tanggal} ${item.jam_masuk}") ?: Date() } catch (e: Exception) { Date() },
                                lintang = item.lat_masuk?.toDoubleOrNull() ?: 0.0,
                                bujur = item.long_masuk?.toDoubleOrNull() ?: 0.0,
                                alamat = "Absen Datang",
                                jalurFoto = item.foto_masuk_url ?: "",
                                status = "Terkirim",
                                idPengguna = _penggunaSaatIni.value?.id ?: 0L
                            ))
                        }
                        
                        // Tambahkan data Pulang jika ada
                        if (!item.jam_pulang.isNullOrEmpty()) {
                            listHasil.add(LogAbsensi(
                                id = "${item.id}_pulang",
                                tipe = "Pulang",
                                waktu = try { formatTgl.parse("${item.tanggal} ${item.jam_pulang}") ?: Date() } catch (e: Exception) { Date() },
                                lintang = item.lat_pulang?.toDoubleOrNull() ?: 0.0,
                                bujur = item.long_pulang?.toDoubleOrNull() ?: 0.0,
                                alamat = "Absen Pulang",
                                jalurFoto = item.foto_pulang_url ?: "",
                                status = "Terkirim",
                                idPengguna = _penggunaSaatIni.value?.id ?: 0L
                            ))
                        }
                    }
                    
                    // Urutkan berdasarkan waktu terbaru
                    _riwayatOnline.value = listHasil.sortedByDescending { it.waktu }
                }
            } catch (e: Exception) {
                Log.e("API", "Gagal ambil riwayat: ${e.message}")
            } finally {
                _sedangMemuat.value = false
            }
        }
    }

    /**
     * FUNGSI: Memperbarui Profil User (Termasuk Foto)
     */
    fun perbaruiProfil(
        nama: String,
        nip: String,
        jabatan: String,
        noHp: String,
        uriFoto: Uri? = null,
        onHasil: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _sedangMemuat.value = true
            try {
                val namaPart = nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val nipPart = nip.toRequestBody("text/plain".toMediaTypeOrNull())
                val jabatanPart = jabatan.toRequestBody("text/plain".toMediaTypeOrNull())
                val noHpPart = noHp.toRequestBody("text/plain".toMediaTypeOrNull())

                var fotoPart: MultipartBody.Part? = null
                uriFoto?.path?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        fotoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                    }
                }

                val respon = api.perbaruiProfil(namaPart, nipPart, jabatanPart, noHpPart, fotoPart)
                if (respon.isSuccessful && respon.body()?.status == "success") {
                    _penggunaSaatIni.value = respon.body()?.dataUser
                    onHasil(true, "Profil Berhasil Diperbarui")
                } else {
                    onHasil(false, respon.body()?.pesan ?: "Gagal memperbarui profil")
                }
            } catch (e: Exception) {
                onHasil(false, "Koneksi Bermasalah: ${e.message}")
            } finally {
                _sedangMemuat.value = false
            }
        }
    }

    /**
     * ALIRAN DATA: Riwayat dari Database Lokal
     */
    val daftarLogAbsensi: StateFlow<List<LogAbsensi>> = daoAbsensi.ambilSemuaLog()
        .map { entities ->
            entities.map {
                LogAbsensi(
                    id = it.id,
                    tipe = it.tipe,
                    waktu = Date(it.waktuMillis),
                    lintang = it.lintang,
                    bujur = it.bujur,
                    alamat = it.alamat,
                    jalurFoto = it.jalurFoto,
                    status = it.status,
                    idPengguna = it.idPengguna
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tipeAbsenTersedia: StateFlow<String> = daftarLogAbsensi.map { riwayat ->
        if (riwayat.isEmpty()) "Datang"
        else {
            val logTerakhir = riwayat.first()
            if (logTerakhir.tipe == "Pulang") "Datang"
            else {
                if (apakahHariIni(logTerakhir.waktu)) "Pulang" else "Datang"
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Datang")

    private fun apakahHariIni(tanggal: Date): Boolean {
        val kalenderSekarang = Calendar.getInstance()
        val kalenderTarget = Calendar.getInstance()
        kalenderTarget.time = tanggal
        return kalenderSekarang.get(Calendar.YEAR) == kalenderTarget.get(Calendar.YEAR) &&
               kalenderSekarang.get(Calendar.DAY_OF_YEAR) == kalenderTarget.get(Calendar.DAY_OF_YEAR)
    }

    fun login(email: String, sandi: String) {
        viewModelScope.launch {
            _sedangMemuat.value = true
            try {
                val respon = api.login(mapOf("email" to email, "password" to sandi))
                val body = respon.body()
                
                // Cek apakah ada token (ini indikator login sukses yang paling valid)
                if (respon.isSuccessful && body?.data?.token != null) {
                    val data = body.data
                    data.token?.let { managerSesi.simpanToken(it) }
                    
                    // Pastikan data user tidak null sebelum pindah halaman
                    val user = data.dataUser ?: Pengguna(id = 0L, nama = "User", nip = "123")
                    
                    // Reset error secara total
                    _pesanErrorLogin.value = null
                    
                    // Update state pengguna untuk memicu navigasi di MainActivity
                    _penggunaSaatIni.value = user
                    
                    Log.d("LOGIN_SUCCESS", "Token ditemukan, masuk ke dashboard. User: ${user.nama}")
                } else {
                    val errorBody = respon.errorBody()?.string()
                    Log.e("LOGIN_FAILED", "Code: ${respon.code()}, Body: $errorBody")
                    
                    // Tampilkan pesan error murni dari server jika ada
                    val pesanDariServer = body?.pesan
                    _pesanErrorLogin.value = if (!pesanDariServer.isNullOrEmpty()) pesanDariServer else "Login Gagal: Cek Email/Password"
                }
            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", "Gagal login: ${e.message}", e)
                val pesan = if (e is com.google.gson.JsonParseException || e is IllegalStateException) {
                    "Data dari server bukan JSON. Pastikan endpoint Backend /api/login sudah benar."
                } else {
                    "Koneksi Bermasalah: ${e.message}"
                }
                _pesanErrorLogin.value = pesan
            } finally {
                _sedangMemuat.value = false
            }
        }
    }

    fun logout() {
        managerSesi.hapusSesi()
        _penggunaSaatIni.value = null
    }

    fun simpanAbsensi(tipe: String, lat: Double, lng: Double, alamat: String, fotoPath: String, idLokasi: Long = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            val idUnik = UUID.randomUUID().toString()
            val dataLokal = EntitasAbsensi(
                id = idUnik,
                idPengguna = _penggunaSaatIni.value?.id ?: 0L,
                tipe = tipe,
                waktuMillis = System.currentTimeMillis(),
                lintang = lat,
                bujur = lng,
                alamat = alamat,
                jalurFoto = fotoPath,
                status = "Menunggu Sinkronisasi"
            )
            daoAbsensi.tambahLog(dataLokal)

            try {
                val uri = Uri.parse(fotoPath)
                val path = if (uri.scheme == "file") uri.path else fotoPath
                val fileFoto = File(path ?: "")
                
                if (fileFoto.exists()) {
                    val requestFile = fileFoto.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    // Gunakan nama file yang valid untuk form-data
                    val partFoto = MultipartBody.Part.createFormData("foto", fileFoto.name, requestFile)
                    val latPart = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val lngPart = lng.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val tipePart = tipe.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
                    val lokasiPart = idLokasi.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                    val respon = api.kirimAbsen(latPart, lngPart, tipePart, lokasiPart, partFoto)
                    if (respon.isSuccessful) {
                        daoAbsensi.tambahLog(dataLokal.copy(status = "Terkirim"))
                        ambilRiwayatOnline() // Refresh riwayat setelah berhasil kirim
                    } else {
                        Log.e("SYNC", "Gagal Sinkronisasi: ${respon.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SYNC", "Error Sinkronisasi: ${e.message}")
            }
        }
    }
}

class ViewModelUtamaFactory(
    private val dao: DaoAbsensi,
    private val api: LayananApi,
    private val sesi: ManagerSesi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ViewModelUtama(dao, api, sesi) as T
    }
}
