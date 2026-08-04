package com.example.absen.ui.history

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.absen.data.model.LogAbsensi
import com.example.absen.ui.ViewModelUtama
import com.example.absen.ui.theme.PrimaryBlue
import com.example.absen.ui.theme.SuccessGreen
import com.example.absen.ui.theme.ErrorRed
import java.text.SimpleDateFormat
import java.util.*

/**
 * FILE LAYAR: LayarRiwayat
 * Note: Menampilkan daftar riwayat absensi yang tersimpan di database lokal.
 * Menggunakan sistem 'Realtime' sehingga setiap ada absen baru akan langsung muncul di sini.
 */
@Composable
fun LayarRiwayat(viewModel: ViewModelUtama = viewModel()) {
    val daftarAbsen by viewModel.riwayatOnline.collectAsState()
    val sedangMemuat by viewModel.sedangMemuat.collectAsState()

    var tanggalTerpilih by remember { mutableStateOf(Calendar.getInstance()) }
    val formatBulanTahun = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))

    val riwayatTerfilter = remember(daftarAbsen, tanggalTerpilih) {
        daftarAbsen.filter { log ->
            val calLog = Calendar.getInstance().apply { time = log.waktu }
            calLog.get(Calendar.MONTH) == tanggalTerpilih.get(Calendar.MONTH) &&
                    calLog.get(Calendar.YEAR) == tanggalTerpilih.get(Calendar.YEAR)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.ambilRiwayatOnline()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        Box(modifier = Modifier.fillMaxWidth().background(PrimaryBlue).padding(16.dp), contentAlignment = Alignment.Center) {
            Text("Riwayat Absensi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    val newCal = (tanggalTerpilih.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                    tanggalTerpilih = newCal
                }) { Icon(Icons.Default.ChevronLeft, contentDescription = null) }
                
                Text(formatBulanTahun.format(tanggalTerpilih.time), fontWeight = FontWeight.Bold)
                
                IconButton(onClick = {
                    val newCal = (tanggalTerpilih.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                    tanggalTerpilih = newCal
                }) { Icon(Icons.Default.ChevronRight, contentDescription = null) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sedangMemuat) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (riwayatTerfilter.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat pada bulan ini", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(riwayatTerfilter) { itemAbsen ->
                        ItemBarisRiwayat(itemAbsen)
                    }
                }
            }
        }
    }
}

/**
 * KOMPONEN: ItemBarisRiwayat
 * Note: Menampilkan satu kotak (Card) informasi absensi.
 * Foto selfie di kiri dan detail data di kanan sesuai permintaan Anda.
 */
@Composable
fun ItemBarisRiwayat(log: LogAbsensi) {
    // Format waktu agar mudah dibaca: dd MMM yyyy, HH:mm:ss (Detik disertakan)
    val formatWaktuLengkap = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // SISI KIRI: Foto Selfie hasil absen
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(log.jalurFoto))
                    .crossfade(true)
                    .build(),
                contentDescription = "Foto Selfie",
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // SISI KANAN: Informasi Detail (Tipe, Waktu, Lokasi)
            Column(modifier = Modifier.weight(1f)) {
                // Baris Status (Datang/Pulang + Waktu)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = if (log.tipe == "Datang") SuccessGreen else ErrorRed, shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = "Absen ${log.tipe}",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = formatWaktuLengkap.format(log.waktu), fontSize = 11.sp, color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Info Lokasi (Nama Tempat)
                Text(text = "Lokasi: ${log.alamat}", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 2, lineHeight = 16.sp)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Info Koordinat (Lat/Lng)
                Text(text = "Lat: ${log.lintang}, Lng: ${log.bujur}", fontSize = 10.sp, color = PrimaryBlue)
            }
        }
    }
}
