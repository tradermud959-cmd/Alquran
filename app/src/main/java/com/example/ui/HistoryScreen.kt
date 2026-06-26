package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SetoranEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val allSetoran by viewModel.allSetoran.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Lancar", "Ulang", "Perlu Latihan")

    val filteredList = if (selectedFilter == "Semua") {
        allSetoran
    } else {
        allSetoran.filter { it.status == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Setoran", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                edgePadding = 24.dp,
                containerColor = BackgroundDark,
                contentColor = AccentGreen,
                divider = { HorizontalDivider(color = BorderLight) }
            ) {
                filters.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedFilter == title,
                        onClick = { selectedFilter = title },
                        text = { Text(title, color = if (selectedFilter == title) AccentGreen else TextSecondary) },
                        selectedContentColor = AccentGreen,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat setoran.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList, key = { it.id }) { setoran ->
                        SetoranItemCard(setoran = setoran, onDelete = { viewModel.deleteSetoran(setoran) })
                    }
                }
            }
        }
    }
}

@Composable
fun SetoranItemCard(setoran: SetoranEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundDark)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    color = BackgroundDark,
                    border = BorderStroke(1.dp, AccentGreenTransparent)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = setoran.namaSurat.take(1),
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = setoran.namaSurat,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                val format = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val detailText = if (setoran.jenisCatatan == "Tadarus") {
                    if (setoran.halamanAwal != null && setoran.halamanAkhir != null) "Halaman ${setoran.halamanAwal} - ${setoran.halamanAkhir} • ${format.format(Date(setoran.tanggal))}"
                    else "Juz ${setoran.juz ?: "-"} • ${format.format(Date(setoran.tanggal))}"
                } else {
                    "Ayat ${setoran.ayatAwal} - ${setoran.ayatAkhir} • ${format.format(Date(setoran.tanggal))}"
                }
                Text(
                    text = detailText,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isTadarus = setoran.jenisCatatan == "Tadarus"
                    val badgeColor = if (isTadarus) AccentGreen else TextSecondary
                    val badgeBg = if (isTadarus) AccentGreenTransparent else BorderLight

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isTadarus) "🟢 TADARUS" else "🔵 SETORAN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = ErrorRed)
            }
        }
    }
}
