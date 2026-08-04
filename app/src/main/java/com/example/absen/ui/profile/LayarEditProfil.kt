package com.example.absen.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import com.example.absen.ui.ViewModelUtama
import com.example.absen.ui.theme.PrimaryBlue

/**
 * FILE LAYAR: LayarEditProfil
 * Note: Form untuk mengubah data user dan upload foto profil ke Laravel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarEditProfil(viewModel: ViewModelUtama = viewModel(), onBack: () -> Unit) {
    val konteks = LocalContext.current
    val pengguna by viewModel.penggunaSaatIni.collectAsState()
    val sedangMemuat by viewModel.sedangMemuat.collectAsState()

    // State Form
    var nama by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }
    var jabatan by remember { mutableStateOf("") }
    var uriFoto by remember { mutableStateOf<Uri?>(null) }

    // Auto-fill saat data pengguna dimuat
    LaunchedEffect(pengguna) {
        pengguna?.let {
            nama = it.nama
            nip = it.nip ?: ""
            email = it.email
            noHp = it.noHp ?: ""
            jabatan = it.jabatan ?: ""
        }
    }

    // Pemilih Gambar dari Galeri
    val launcherGaleri = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uriFoto = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // FOTO PROFIL
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = uriFoto ?: pengguna?.foto,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable { launcherGaleri.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        color = PrimaryBlue,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp).offset(x = (-4).dp, y = (-4).dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // INPUT NAMA
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // INPUT NIP
                OutlinedTextField(
                    value = nip,
                    onValueChange = { nip = it },
                    label = { Text("NIP / NIK") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // EMAIL (Read Only)
                OutlinedTextField(
                    value = email,
                    onValueChange = { },
                    label = { Text("Email (Tidak bisa diubah)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = false
                )
                Spacer(modifier = Modifier.height(16.dp))

                // INPUT NO HP
                OutlinedTextField(
                    value = noHp,
                    onValueChange = { noHp = it },
                    label = { Text("Nomor HP") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // INPUT JABATAN
                OutlinedTextField(
                    value = jabatan,
                    onValueChange = { jabatan = it },
                    label = { Text("Jabatan / Posisi") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // TOMBOL SIMPAN
                Button(
                    onClick = {
                        viewModel.perbaruiProfil(nama, nip, jabatan, noHp, uriFoto) { sukses, pesan ->
                            Toast.makeText(konteks, pesan, Toast.LENGTH_SHORT).show()
                            if (sukses) onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = !sedangMemuat
                ) {
                    if (sedangMemuat) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("SIMPAN PERUBAHAN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
