package com.example.absen.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.absen.ui.ViewModelUtama
import com.example.absen.ui.theme.DarkBlueBG
import com.example.absen.ui.theme.DarkFieldBG
import com.example.absen.ui.theme.AccentBlue
import com.example.absen.ui.theme.ErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarLogin(viewModel: ViewModelUtama = viewModel(), padaLoginBerhasil: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var kataSandi by remember { mutableStateOf("") }
    var ingatAkun by remember { mutableStateOf(false) }
    var sandiTerlihat by remember { mutableStateOf(false) }

    val pesanError by viewModel.pesanErrorLogin.collectAsState()
    val sedangMemuat by viewModel.sedangMemuat.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBG)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Icon Box (Top)
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(AccentBlue, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Absensi Mobile",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Masuk untuk mencatat kehadiran hari ini",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Input NIP
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "NIP / ID Pegawai",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkFieldBG,
                    unfocusedContainerColor = DarkFieldBG,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue)
                },
                placeholder = { Text("Masukkan NIP", color = Color.Gray) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Password
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Kata Sandi",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = kataSandi,
                onValueChange = { kataSandi = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                visualTransformation = if (sandiTerlihat) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkFieldBG,
                    unfocusedContainerColor = DarkFieldBG,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFB400))
                },
                trailingIcon = {
                    IconButton(onClick = { sandiTerlihat = !sandiTerlihat }) {
                        Icon(
                            imageVector = if (sandiTerlihat) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (sandiTerlihat) "Sembunyikan sandi" else "Tampilkan sandi",
                            tint = Color.Gray
                        )
                    }
                },
                placeholder = { Text("••••••••", color = Color.Gray) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Remember Me & Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = ingatAkun,
                    onCheckedChange = { ingatAkun = it },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = Color.Gray,
                        checkedColor = AccentBlue
                    )
                )
                Text("Ingat Akun", color = Color.Gray, fontSize = 12.sp)
            }
            TextButton(onClick = { /* TODO */ }) {
                Text("Lupa Password?", color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (pesanError != null) {
            Text(
                text = pesanError!!,
                color = ErrorRed,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { viewModel.login(email, kataSandi) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            shape = RoundedCornerShape(12.dp),
            enabled = !sedangMemuat
        ) {
            if (sedangMemuat) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "MASUK SEKARANG",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Biometric Login
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                onClick = { /* Biometric Action */ },
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(30.dp),
                color = DarkFieldBG
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("☝️", fontSize = 30.sp) // Fingerprint Emoji as Placeholder
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Atau masuk dengan Biometrik / Sidik Jari",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
