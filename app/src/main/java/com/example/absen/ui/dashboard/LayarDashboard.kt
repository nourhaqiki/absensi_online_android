package com.example.absen.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.absen.ui.ViewModelUtama
import com.example.absen.ui.theme.PrimaryBlue
import com.example.absen.ui.theme.SuccessGreen
import com.example.absen.ui.theme.ErrorRed
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * FILE LAYAR: LayarDashboard
 * Note: Ini adalah layar utama tempat user melakukan absensi.
 * Di sini kita menggunakan 1 tombol dinamis yang berubah antara Datang & Pulang.
 */
@SuppressLint("MissingPermission")
@Composable
fun LayarDashboard(viewModel: ViewModelUtama = viewModel()) {
    val konteks = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val pengguna by viewModel.penggunaSaatIni.collectAsState()
    
    // VARIABEL STATE: Memantau perizinan
    var izinLokasiDiberikan by remember { 
        mutableStateOf(ContextCompat.checkSelfPermission(konteks, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
    var izinKameraDiberikan by remember { 
        mutableStateOf(ContextCompat.checkSelfPermission(konteks, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    // STATE TOMBOL: Memantau jenis absen yang tersedia dari database (Otomatis Reset 00:00)
    val jenisAbsenTersedia by viewModel.tipeAbsenTersedia.collectAsState()

    var lokasiSaatIni by remember { mutableStateOf<LatLng?>(null) }
    var alamatSaatIni by remember { mutableStateOf("-") }
    
    val klienLokasi = remember { LocationServices.getFusedLocationProviderClient(konteks) }
    val tangkapGambar = remember { ImageCapture.Builder().build() }
    
    val peluncurIzin = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { hasilIzin ->
        izinKameraDiberikan = hasilIzin[Manifest.permission.CAMERA] ?: izinKameraDiberikan
        izinLokasiDiberikan = hasilIzin[Manifest.permission.ACCESS_FINE_LOCATION] ?: izinLokasiDiberikan
    }

    LaunchedEffect(Unit) {
        val daftarIzin = mutableListOf<String>()
        if (!izinKameraDiberikan) daftarIzin.add(Manifest.permission.CAMERA)
        if (!izinLokasiDiberikan) {
            daftarIzin.add(Manifest.permission.ACCESS_FINE_LOCATION)
            daftarIzin.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (daftarIzin.isNotEmpty()) peluncurIzin.launch(daftarIzin.toTypedArray())
    }

    LaunchedEffect(izinLokasiDiberikan) {
        if (izinLokasiDiberikan) {
            klienLokasi.lastLocation.addOnSuccessListener { lokasi: Location? ->
                lokasi?.let { lokasiSaatIni = LatLng(it.latitude, it.longitude) }
            }
        }
    }

    LaunchedEffect(lokasiSaatIni) {
        lokasiSaatIni?.let { latLng ->
            withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(konteks, Locale.getDefault())
                    val daftarAlamat = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (!daftarAlamat.isNullOrEmpty()) {
                        alamatSaatIni = daftarAlamat[0].getAddressLine(0) ?: "-"
                    }
                } catch (e: Exception) {
                    Log.e("Geocoder", "Gagal mengambil alamat", e)
                }
            }
        }
    }

    /**
     * FUNGSI: Ambil Foto Selfie & Simpan Absensi
     */
    fun ambilFotoDanAbsen(tipe: String) {
        val folderOutput = konteks.cacheDir
        val fileFoto = File(folderOutput, SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg")
        val opsiOutput = ImageCapture.OutputFileOptions.Builder(fileFoto).build()

        tangkapGambar.takePicture(
            opsiOutput,
            ContextCompat.getMainExecutor(konteks),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(hasil: ImageCapture.OutputFileResults) {
                    viewModel.simpanAbsensi(
                        tipe = tipe,
                        lat = lokasiSaatIni?.latitude ?: 0.0,
                        lng = lokasiSaatIni?.longitude ?: 0.0,
                        alamat = alamatSaatIni,
                        fotoPath = Uri.fromFile(fileFoto).toString()
                    )
                    Toast.makeText(konteks, "Absensi $tipe Berhasil!", Toast.LENGTH_SHORT).show()
                }
                override fun onError(exc: ImageCaptureException) {
                    Log.e("Kamera", "Gagal mengambil foto: ${exc.message}")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        // Header
        Box(modifier = Modifier.fillMaxWidth().background(PrimaryBlue).padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Selamat Pagi, ${pengguna?.nama ?: "User"}", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(pengguna?.jabatan ?: "Pegawai", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // KARTU LOKASI
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detail Lokasi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Nama Tempat/Jalan:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Gray)
                    Text(alamatSaatIni, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Latitude:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Gray)
                            Text(lokasiSaatIni?.latitude?.toString() ?: "-", fontSize = 13.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Longitude:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Gray)
                            Text(lokasiSaatIni?.longitude?.toString() ?: "-", fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // KARTU KAMERA
            Card(modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally).clip(CircleShape), elevation = CardDefaults.cardElevation(4.dp)) {
                if (izinKameraDiberikan) {
                    AndroidView(
                        factory = { ctx ->
                            val viewPreview = PreviewView(ctx)
                            val penyediaKameraFuture = ProcessCameraProvider.getInstance(ctx)
                            penyediaKameraFuture.addListener({
                                try {
                                    val penyediaKamera = penyediaKameraFuture.get()
                                    val preview = Preview.Builder().build().also { it.setSurfaceProvider(viewPreview.surfaceProvider) }
                                    penyediaKamera.unbindAll()
                                    penyediaKamera.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, tangkapGambar)
                                } catch (e: Exception) { Log.e("Kamera", "Gagal mengaktifkan kamera") }
                            }, ContextCompat.getMainExecutor(ctx))
                            viewPreview
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Kamera Mati", fontSize = 12.sp) }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // SINGLE DYNAMIC BUTTON: Berubah antara Datang & Pulang
            // Note: Warna tombol juga berubah (Hijau untuk Datang, Merah untuk Pulang)
            Button(
                onClick = { ambilFotoDanAbsen(jenisAbsenTersedia) },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (jenisAbsenTersedia == "Datang") SuccessGreen else ErrorRed
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = lokasiSaatIni != null && izinKameraDiberikan
            ) {
                Text(
                    text = if (jenisAbsenTersedia == "Datang") "ABSEN DATANG" else "ABSEN PULANG",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            
            if (jenisAbsenTersedia == "Pulang") {
                Text(
                    text = "*Tombol akan reset otomatis menjadi 'Datang' jam 00:00",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
