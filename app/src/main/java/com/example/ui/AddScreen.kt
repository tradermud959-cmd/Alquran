package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.SetoranEntity
import com.example.data.SurahData
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController, viewModel: MainViewModel) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val today = format.format(Date())

    var selectedSurah by remember { mutableStateOf("") }
    var ayatAwal by remember { mutableStateOf("") }
    var ayatAkhir by remember { mutableStateOf("") }
    var juz by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Lancar") }
    var catatan by remember { mutableStateOf("") }
    
    var jenisCatatan by remember { mutableStateOf("Setoran Bacaan") }
    var halamanAwal by remember { mutableStateOf("") }
    var halamanAkhir by remember { mutableStateOf("") }

    var expandedSurah by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Setoran", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = BorderLight,
            cursorColor = AccentGreen,
            focusedLabelColor = AccentGreen,
            unfocusedLabelColor = TextSecondary,
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isSetoran = jenisCatatan == "Setoran Bacaan"
                    Button(
                        onClick = { jenisCatatan = "Setoran Bacaan" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSetoran) AccentGreen else SurfaceDark,
                            contentColor = if (isSetoran) BackgroundDark else TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Setoran Bacaan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = { jenisCatatan = "Tadarus" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isSetoran) AccentGreen else SurfaceDark,
                            contentColor = if (!isSetoran) BackgroundDark else TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tadarus", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = today,
                    onValueChange = {},
                    label = { Text("Tanggal") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
            }
            item {
                OutlinedTextField(
                    value = userName,
                    onValueChange = {},
                    label = { Text("Penyetor") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = expandedSurah,
                    onExpandedChange = { expandedSurah = !expandedSurah }
                ) {
                    OutlinedTextField(
                        value = selectedSurah,
                        onValueChange = { selectedSurah = it },
                        label = { Text("Nama Surat") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSurah) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentGreen,
                            unfocusedBorderColor = BorderLight,
                            focusedLabelColor = AccentGreen,
                            unfocusedLabelColor = TextSecondary,
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    val filteredSurahs = SurahData.listSurah.filter { it.contains(selectedSurah, ignoreCase = true) }
                    if (filteredSurahs.isNotEmpty() && expandedSurah) {
                        ExposedDropdownMenu(
                            expanded = expandedSurah,
                            onDismissRequest = { expandedSurah = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            filteredSurahs.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption, color = TextPrimary) },
                                    onClick = {
                                        selectedSurah = selectionOption
                                        expandedSurah = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            item {
                if (jenisCatatan == "Tadarus") {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = halamanAwal,
                            onValueChange = { halamanAwal = it },
                            label = { Text("Halaman Awal") },
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = halamanAkhir,
                            onValueChange = { halamanAkhir = it },
                            label = { Text("Halaman Akhir") },
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = ayatAwal,
                        onValueChange = { ayatAwal = it },
                        label = { Text(if (jenisCatatan == "Tadarus") "Ayat Awal (Opsional)" else "Ayat Awal") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = ayatAkhir,
                        onValueChange = { ayatAkhir = it },
                        label = { Text(if (jenisCatatan == "Tadarus") "Ayat Akhir (Opsional)" else "Ayat Akhir") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            }
            item {
                OutlinedTextField(
                    value = juz,
                    onValueChange = { juz = it },
                    label = { Text("Juz (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
            }
            if (jenisCatatan == "Setoran Bacaan") {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedStatus,
                        onExpandedChange = { expandedStatus = !expandedStatus }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            label = { Text("Status") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentGreen,
                                unfocusedBorderColor = BorderLight,
                                focusedLabelColor = AccentGreen,
                                unfocusedLabelColor = TextSecondary,
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStatus,
                            onDismissRequest = { expandedStatus = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            listOf("Lancar", "Ulang", "Perlu Latihan").forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption, color = TextPrimary) },
                                    onClick = {
                                        status = selectionOption
                                        expandedStatus = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = catatan,
                    onValueChange = { catatan = it },
                    label = { Text("Catatan Tambahan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = textFieldColors
                )
            }
            item {
                Button(
                    onClick = {
                        if (selectedSurah.isNotBlank()) {
                            val newSetoran = SetoranEntity(
                                namaSurat = selectedSurah,
                                ayatAwal = ayatAwal.toIntOrNull() ?: 1,
                                ayatAkhir = ayatAkhir.toIntOrNull() ?: 1,
                                juz = juz.toIntOrNull(),
                                status = if (jenisCatatan == "Tadarus") "Lancar" else status,
                                catatan = catatan,
                                jenisCatatan = jenisCatatan,
                                halamanAwal = halamanAwal.toIntOrNull(),
                                halamanAkhir = halamanAkhir.toIntOrNull()
                            )
                            viewModel.addSetoran(newSetoran)
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("SIMPAN", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
