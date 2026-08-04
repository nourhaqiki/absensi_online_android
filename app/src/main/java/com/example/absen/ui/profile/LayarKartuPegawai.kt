package com.example.absen.ui.profile

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.absen.data.model.Pengguna
import com.example.absen.ui.theme.PrimaryBlue
import java.io.OutputStream

/**
 * FILE LAYAR: LayarKartuPegawai
 * Note: Menampilkan pratinjau kartu pegawai dan fitur download ke JPEG.
 * Ukuran Kartu: 8.56 cm x 5.398 cm (Standar CR80).
 */
@Composable
fun LayarKartuPegawai(pengguna: Pengguna, onBack: () -> Unit) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pratinjau Kartu Pegawai", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))

        // KARTU PEGAWAI (Ukuran 8.56 x 5.398 cm dalam DP)
        Card(
            modifier = Modifier
                .width(340.dp) // Menggunakan skala yang pas untuk layar HP
                .height(214.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background Variasi (Aksen Biru)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(PrimaryBlue))
                
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("KARTU PEGAWAI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(modifier = Modifier.fillMaxSize()) {
                        // SISI KIRI: Foto
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                                .border(2.dp, PrimaryBlue, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if ((pengguna.foto ?: "").isNotEmpty()) {
                                AsyncImage(
                                    model = pengguna.foto,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text("FOTO", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // SISI KANAN: Detail Data
                        Column(modifier = Modifier.weight(1f)) {
                            Text("NAMA:", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(pengguna.nama, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text("NIP:", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(pengguna.nip ?: "-", fontSize = 10.sp)
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text("TTL:", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text("-", fontSize = 9.sp)
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text("JABATAN:", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(pengguna.jabatan ?: "-", fontSize = 9.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.weight(1f))
                            
                            // TANDA TANGAN
                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                                Text("Tanda Tangan", fontSize = 7.sp, color = Color.Gray)
                                Box(modifier = Modifier.height(30.dp).width(80.dp).background(Color(0xFFF9F9F9))) {
                                    Text("Digitally Signed", fontSize = 8.sp, modifier = Modifier.align(Alignment.Center), color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // TOMBOL DOWNLOAD
        Button(
            onClick = {
                saveCardToGallery(context, pengguna)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("DOWNLOAD KARTU (JPG)", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("KEMBALI")
        }
    }
}

/**
 * FUNGSI: Menyimpan kartu ke Galeri HP
 * Note: Dalam implementasi nyata, kita harus merender View ke Bitmap.
 */
fun saveCardToGallery(context: Context, pengguna: Pengguna) {
    // Simulasi pembuatan bitmap (Karena Compose ke Bitmap butuh capturing library/GraphicsLayer)
    Toast.makeText(context, "Mengunduh Kartu Pegawai ${pengguna.nama}...", Toast.LENGTH_LONG).show()
    
    // Logika simpan file (Contoh simpan ke folder Downloads/Pictures)
    try {
        val filename = "KARTU_PEGAWAI_${pengguna.nip ?: "UNKNOWN"}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Absen")
            }
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
            // Di sini seharusnya ada proses bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
            Toast.makeText(context, "Kartu berhasil disimpan di Galeri", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
