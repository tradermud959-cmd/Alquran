package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavController
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.scale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val allSetoran by viewModel.allSetoran.collectAsStateWithLifecycle()

    val totalSetoran = allSetoran.size
    val lastSetoran = allSetoran.firstOrNull()

    // Sederhana: 114 Surat. Anggap hafal jika ada setoran lancar.
    val lancarSetoran = allSetoran.filter { it.status == "Lancar" }
    val uniqueSurahs = lancarSetoran.map { it.namaSurat }.distinct().size
    val progressPercent = if (uniqueSurahs > 0) (uniqueSurahs.toFloat() / 114f) else 0f
    val recentSetorans = allSetoran.take(3)

    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val targetType by viewModel.targetTadarusType.collectAsStateWithLifecycle()
    val targetValue by viewModel.targetTadarusValue.collectAsStateWithLifecycle()

    val format = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
    val todayStr = format.format(Date())

    val todayTadarus = allSetoran.filter { 
        it.jenisCatatan == "Tadarus" && format.format(Date(it.tanggal)) == todayStr 
    }

    val tadarusProgressValue = if (targetType == "Halaman") {
        todayTadarus.sumOf { 
            if (it.halamanAwal != null && it.halamanAkhir != null && it.halamanAkhir >= it.halamanAwal) {
                it.halamanAkhir - it.halamanAwal + 1
            } else 0
        }
    } else {
        todayTadarus.mapNotNull { it.juz }.distinct().size
    }

    val tadarusProgressPercent = if (targetValue > 0) (tadarusProgressValue.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f) else 0f
    
    val animatedTadarusProgress by animateFloatAsState(
        targetValue = tadarusProgressPercent,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "tadarusProgress"
    )

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.Add.route) },
                containerColor = AccentGreen,
                contentColor = BackgroundDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Setoran", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("TAMBAH SETORAN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "ASSALAMU'ALAIKUM,",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.5.sp,
                            color = AccentGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                        val greeting = when (hour) {
                            in 4..9 -> "🌅 Selamat Pagi"
                            in 10..14 -> "☀️ Selamat Siang"
                            in 15..17 -> "🌇 Selamat Sore"
                            else -> "🌙 Selamat Malam"
                        }
                        Text(
                            text = "$greeting, $userName",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .padding(1.dp)
                            .background(SurfaceDark, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, AccentGreenTransparent)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = AccentGreen,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // Fake AI / Motivational Card
            item {
                MotivationalCard()
            }

            // Target Tadarus Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Target Tadarus Hari Ini", fontSize = 14.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$tadarusProgressValue / $targetValue",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = targetType,
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 6.dp)
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
                                    .fillMaxWidth(animatedTadarusProgress)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AccentGreen)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        if (tadarusProgressValue >= targetValue) {
                            Text(
                                text = "🎉 Alhamdulillah\nTarget tadarus hari ini telah tercapai.\nSemoga Allah menerima setiap huruf yang telah dibaca.",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        } else {
                            val remaining = targetValue - tadarusProgressValue
                            Text(
                                text = "Sisa $remaining $targetType Lagi",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Progres Hafalan", fontSize = 14.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
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

            // Quick Stats Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total Setoran", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = totalSetoran.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kali", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 3.dp))
                            }
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Setoran Terakhir", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lastSetoran?.namaSurat ?: "-",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentGreen
                            )
                        }
                    }
                }
            }

            // Recent History Preview
            if (recentSetorans.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Setoran Terbaru", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(
                            "Lihat Semua",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AccentGreen,
                            modifier = Modifier.clickable { navController.navigate(Screen.History.route) }
                        )
                    }
                }

                items(recentSetorans, key = { it.id }) { setoran ->
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
                            }
                            
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
                }
            }
        }
    }
}

@Composable
fun MotivationalCard() {
    val quotes = listOf(
        "Assalamu'alaikum. Semoga bacaan hari ini lebih baik dari kemarin.",
        "Jangan lupa menyempatkan waktu untuk membaca Al-Qur'an hari ini.",
        "Sedikit demi sedikit, istiqamah akan membawa hasil yang besar.",
        "Semoga Allah memudahkan setiap ayat yang kamu baca.",
        "Tidak perlu terburu-buru, yang terpenting adalah konsisten.",
        "Hari ini adalah kesempatan baru untuk mendekat kepada Allah.",
        "Semoga setiap huruf yang dibaca menjadi amal kebaikan.",
        "Luangkan beberapa menit hari ini untuk Al-Qur'an.",
        "Jangan menyerah jika masih harus mengulang bacaan, proses juga bagian dari ibadah.",
        "Semoga Allah memberikan kelancaran dalam setiap bacaanmu.",
        "Tetap semangat, setiap langkah kecil menuju Al-Qur'an sangat berharga.",
        "Bacalah dengan tenang, pahami maknanya, dan nikmati prosesnya."
    )
    val selectedQuote = rememberSaveable { quotes.random() }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(1000))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGreenTransparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Motivasi",
                        tint = AccentGreen,
                        modifier = Modifier
                            .size(20.dp)
                            .scale(pulseScale)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = selectedQuote,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
