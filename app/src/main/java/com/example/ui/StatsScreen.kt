package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: MainViewModel) {
    val allSetoran by viewModel.allSetoran.collectAsStateWithLifecycle()

    val totalSetoran = allSetoran.size
    val lancarSetoran = allSetoran.filter { it.status == "Lancar" }
    val uniqueSurahs = lancarSetoran.map { it.namaSurat }.distinct().size
    val progressPercent = if (uniqueSurahs > 0) (uniqueSurahs.toFloat() / 114f) else 0f

    val mostReadSurah = allSetoran.groupingBy { it.namaSurat }.eachCount().maxByOrNull { it.value }?.key ?: "-"

    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val tadarusRecords = allSetoran.filter { it.jenisCatatan == "Tadarus" }
    val totalHalamanTadarus = tadarusRecords.sumOf { 
        if (it.halamanAwal != null && it.halamanAkhir != null && it.halamanAkhir >= it.halamanAwal) {
            it.halamanAkhir - it.halamanAwal + 1
        } else 0
    }
    val totalJuzTadarus = tadarusRecords.mapNotNull { it.juz }.distinct().size
    val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale("id", "ID"))
    val hariIstiqamah = tadarusRecords.map { format.format(java.util.Date(it.tanggal)) }.distinct().size
    val tadarusKhatamPercent = (totalHalamanTadarus.toFloat() / 604f).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistik", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(title = "Total Setoran", value = totalSetoran.toString(), subtitle = "Kali", modifier = Modifier.weight(1f))
                    StatCard(title = "Surat Tersering", value = mostReadSurah, modifier = Modifier.weight(1f))
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Progress Hafalan Keseluruhan", color = TextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${(animatedProgress * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 36.sp, color = AccentGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$uniqueSurahs / 114 Surat",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(BackgroundDark.copy(alpha = 0.5f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AccentGreen)
                            )
                        }
                    }
                }
            }

            item {
                Text("Statistik Tadarus", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(title = "Total Halaman", value = totalHalamanTadarus.toString(), modifier = Modifier.weight(1f))
                    StatCard(title = "Total Juz", value = totalJuzTadarus.toString(), modifier = Modifier.weight(1f))
                    StatCard(title = "Hari Istiqamah", value = hariIstiqamah.toString(), subtitle = "Hari", modifier = Modifier.weight(1f))
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Progress Menuju Khatam (604 Halaman)", color = TextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${(tadarusKhatamPercent * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 36.sp, color = AccentGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$totalHalamanTadarus / 604 Hal",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(BackgroundDark.copy(alpha = 0.5f))
                        ) {
                            val animatedTadarusKhatam by animateFloatAsState(
                                targetValue = tadarusKhatamPercent,
                                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                                label = "tadarusKhatam"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedTadarusKhatam)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AccentGreen)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentGreen
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(subtitle, fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 3.dp))
                }
            }
        }
    }
}
