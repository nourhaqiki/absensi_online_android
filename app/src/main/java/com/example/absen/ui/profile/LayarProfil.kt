package com.example.absen.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.absen.ui.ViewModelUtama
import com.example.absen.ui.theme.PrimaryBlue

/**
 * FILE LAYAR: LayarProfil
 * Note: Menampilkan data pengguna terintegrasi Laravel.
 */
@Composable
fun LayarProfil(
    viewModel: ViewModelUtama = viewModel(), 
    onBukaKartu: () -> Unit,
    onEditProfil: () -> Unit
) {
    val pengguna by viewModel.penggunaSaatIni.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // HEADER: Background Biru dengan Foto Profil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = pengguna?.foto?.replace("localhost", "10.0.2.2"), // Handle localhost if using emulator
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(pengguna?.nama ?: "Memuat...", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(pengguna?.jabatan ?: "-", color = Color.Gray, fontSize = 14.sp)
        Text("NIP: ${pengguna?.nip ?: "-"}", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // KARTU INFORMASI
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ItemInfoProfil("Email", pengguna?.email ?: "-")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ItemInfoProfil("Telepon", pengguna?.noHp ?: "-")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ItemInfoProfil("Status Akun", if (pengguna?.aktif == 1) "Aktif" else "Nonaktif")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // KARTU AKSI
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Edit Profil Saya",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { onEditProfil() }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    text = "Buat Kartu Pegawai",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBukaKartu() }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    text = "Keluar Aplikasi",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.logout() }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ItemInfoProfil(label: String, nilai: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(nilai, fontWeight = FontWeight.Medium)
    }
}
