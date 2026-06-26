package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "setoran")
data class SetoranEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tanggal: Long = System.currentTimeMillis(),
    val namaSurat: String,
    val ayatAwal: Int,
    val ayatAkhir: Int,
    val juz: Int? = null,
    val status: String,
    val catatan: String,
    val jenisCatatan: String = "Setoran Bacaan", // "Setoran Bacaan" atau "Tadarus"
    val halamanAwal: Int? = null,
    val halamanAkhir: Int? = null
)
