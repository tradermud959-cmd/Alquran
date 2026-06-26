package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    var editName by remember { mutableStateOf(userName) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val allSetoran by viewModel.allSetoran.collectAsStateWithLifecycle()
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri ->
            if (uri != null) {
                viewModel.exportDataToCsv(context, uri, allSetoran)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val targetTadarusType by viewModel.targetTadarusType.collectAsStateWithLifecycle()
            val targetTadarusValue by viewModel.targetTadarusValue.collectAsStateWithLifecycle()
            var editTargetType by remember { mutableStateOf(targetTadarusType) }
            var editTargetValue by remember { mutableStateOf(targetTadarusValue.toString()) }
            var targetExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Target Tadarus Harian", color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editTargetValue,
                            onValueChange = { editTargetValue = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentGreen, unfocusedBorderColor = BorderLight
                            ),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )

                        ExposedDropdownMenuBox(
                            expanded = targetExpanded,
                            onExpandedChange = { targetExpanded = !targetExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = editTargetType,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                                modifier = Modifier.menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = AccentGreen, unfocusedBorderColor = BorderLight
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = targetExpanded,
                                onDismissRequest = { targetExpanded = false },
                                modifier = Modifier.background(SurfaceDark)
                            ) {
                                listOf("Halaman", "Juz").forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption, color = TextPrimary) },
                                        onClick = {
                                            editTargetType = selectionOption
                                            targetExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            val valueInt = editTargetValue.toIntOrNull() ?: 10
                            viewModel.setTargetTadarus(editTargetType, valueInt) 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Target", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nama Pengguna", color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentGreen,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = AccentGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.setUserName(editName) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Nama", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tema Aplikasi", color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tema gelap ditetapkan sebagai default.", fontWeight = FontWeight.Medium, color = TextPrimary)
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tentang", color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Catatan Setoran Al-Qur'an v1.0\nAplikasi 100% Offline.", fontWeight = FontWeight.Medium, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { exportLauncher.launch("setoran_alquran.csv") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Text("Ekspor Data ke CSV", color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
            ) {
                Text("Reset Semua Data", color = ErrorRed, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = SurfaceDark,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Konfirmasi Reset") },
            text = { Text("Apakah Anda yakin ingin menghapus semua data setoran? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAll()
                        showDialog = false
                    }
                ) {
                    Text("HAPUS", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("BATAL", color = TextPrimary)
                }
            }
        )
    }
}
