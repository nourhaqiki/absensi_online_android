// KONFIGURASI BUILD LEVEL MODUL (Aplikasi)
// Note: File ini berisi pengaturan khusus untuk aplikasi, termasuk library yang digunakan,
// versi SDK, dan fitur-fitur yang diaktifkan (seperti Jetpack Compose).

plugins {
    // Plugin standar untuk membuat aplikasi Android
    alias(libs.plugins.android.application)
    // Plugin agar bisa menggunakan bahasa pemrograman Kotlin
    alias(libs.plugins.kotlin.android)
    // Plugin khusus untuk mendukung UI modern Jetpack Compose
    alias(libs.plugins.kotlin.compose)
    // Plugin KSP (Kotlin Symbol Processing) untuk memproses database Room secara efisien
    alias(libs.plugins.ksp)
    // Plugin Google Services untuk integrasi Firebase (dinonaktifkan jika belum ada file json)
    // alias(libs.plugins.google.services) 
}

android {
    // Namespace aplikasi (identitas unik di dalam sistem)
    namespace = "com.example.absen"
    // Versi SDK Android yang digunakan untuk proses compile (Build)
    compileSdk = 35

    defaultConfig {
        // ID unik aplikasi (digunakan di Play Store)
        applicationId = "com.example.absen"
        // Versi minimal Android yang bisa menjalankan aplikasi (API 26 = Android 8.0 Oreo)
        minSdk = 26
        // Versi target Android yang dioptimalkan untuk aplikasi ini
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Pengaturan untuk versi rilis (mematikan fitur debug dan optimasi kode)
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        // Menggunakan standar Java 11 agar kompatibel dengan library terbaru
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        // Menargetkan bytecode Java 11 untuk performa Kotlin yang lebih baik
        jvmTarget = "11"
    }
    buildFeatures {
        // Mengaktifkan fitur Jetpack Compose (Tanpa XML)
        compose = true
    }
}

dependencies {
    // LIBRARY DASAR ANDROID & KOTLIN
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // UI JETPACK COMPOSE (Modern UI)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Komponen Material 3
    implementation(libs.androidx.material.icons.extended) // Kumpulan Icon Lengkap
    implementation(libs.material) // Komponen Material dasar
    implementation(libs.androidx.navigation.compose) // Navigasi antar halaman
    
    // GPS & LOKASI (Google Play Services)
    implementation(libs.play.services.location) // Untuk ambil koordinat Lat/Lng
    implementation(libs.play.services.maps) // Komponen dasar Maps
    implementation(libs.maps.compose) // Google Maps versi Compose
    
    // KAMERA (CameraX)
    implementation(libs.androidx.camera.core) // Inti kamera
    implementation(libs.androidx.camera.camera2) // Dukungan hardware kamera modern
    implementation(libs.androidx.camera.lifecycle) // Sinkronisasi kamera dengan layar
    implementation(libs.androidx.camera.view) // Tampilan preview kamera di layar
    
    // IMAGE LOADING (Coil)
    implementation(libs.coil.compose) // Untuk menampilkan foto selfie di daftar riwayat
    
    // UTILITAS TAMBAHAN
    implementation("com.google.guava:guava:33.3.1-android") // Diperlukan oleh CameraX
    
    // RETROFIT & API (Laravel Connector)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // BACKEND ONLINE (Firebase)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth) // Untuk login user
    implementation(libs.firebase.firestore) // Database online realtime
    implementation(libs.firebase.storage) // Penyimpanan file foto online
    
    // DATABASE LOKAL (Room / SQLite)
    // Note: Digunakan untuk menyimpan data secara offline di handphone
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // Prosesor database
    
    // LOGIKA BISNIS (ViewModel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // TESTING (Untuk pengetesan aplikasi)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
