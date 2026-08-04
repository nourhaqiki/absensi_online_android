package com.example.absen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.absen.data.api.ManagerSesi
import com.example.absen.ui.ViewModelUtama
import com.example.absen.ui.ViewModelUtamaFactory
import com.example.absen.ui.dashboard.LayarDashboard
import com.example.absen.ui.history.LayarRiwayat
import com.example.absen.ui.login.LayarLogin
import com.example.absen.ui.profile.LayarEditProfil
import com.example.absen.ui.profile.LayarKartuPegawai
import com.example.absen.ui.profile.LayarProfil
import com.example.absen.ui.theme.AbsenTheme

/**
 * AKTIVITAS UTAMA: MainActivity
 * Note: Pintu masuk aplikasi dengan integrasi API Laravel dan Database Lokal.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as AbsenApp
        // Menyiapkan dependensi untuk ViewModel
        val pabrikViewModel = ViewModelUtamaFactory(
            dao = app.basisData.daoAbsensi(),
            api = app.layananApi,
            sesi = ManagerSesi(this)
        )
        
        setContent {
            AbsenTheme {
                AplikasiUtama(viewModel = viewModel(factory = pabrikViewModel))
            }
        }
    }
}

@Composable
fun AplikasiUtama(viewModel: ViewModelUtama) {
    val pengontrolNavigasi = rememberNavController()
    val penggunaSaatIni by viewModel.penggunaSaatIni.collectAsState()

    LaunchedEffect(penggunaSaatIni) {
        Log.d("NAV_DEBUG", "penggunaSaatIni changed: ${penggunaSaatIni?.nama}")
    }

    if (penggunaSaatIni == null) {
        LayarLogin(viewModel = viewModel, padaLoginBerhasil = { 
            Log.d("NAV_DEBUG", "padaLoginBerhasil triggered")
        })
    } else {
        Scaffold(
            bottomBar = {
                val entriNavigasi by pengontrolNavigasi.currentBackStackEntryAsState()
                val ruteSaatIni = entriNavigasi?.destination?.route
                
                NavigationBar {
                    NavigationBarItem(
                        selected = ruteSaatIni == "dashboard",
                        onClick = { pengontrolNavigasi.navigate("dashboard") },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Dashboard") }
                    )
                    NavigationBarItem(
                        selected = ruteSaatIni == "riwayat",
                        onClick = { pengontrolNavigasi.navigate("riwayat") },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text("Riwayat") }
                    )
                    NavigationBarItem(
                        selected = ruteSaatIni == "profil",
                        onClick = { pengontrolNavigasi.navigate("profil") },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profil") }
                    )
                }
            }
        ) { paddingDalam ->
            NavHost(
                navController = pengontrolNavigasi,
                startDestination = "dashboard",
                modifier = Modifier.padding(paddingDalam)
            ) {
                composable("dashboard") { LayarDashboard(viewModel = viewModel) }
                composable("riwayat") { LayarRiwayat(viewModel = viewModel) }
                composable("profil") { 
                    LayarProfil(
                        viewModel = viewModel, 
                        onBukaKartu = { pengontrolNavigasi.navigate("kartu_pegawai") },
                        onEditProfil = { pengontrolNavigasi.navigate("edit_profil") }
                    ) 
                }
                composable("edit_profil") {
                    LayarEditProfil(
                        viewModel = viewModel,
                        onBack = { pengontrolNavigasi.popBackStack() }
                    )
                }
                composable("kartu_pegawai") {
                    val pengguna by viewModel.penggunaSaatIni.collectAsState()
                    pengguna?.let {
                        LayarKartuPegawai(pengguna = it, onBack = { pengontrolNavigasi.popBackStack() })
                    }
                }
            }
        }
    }
}
