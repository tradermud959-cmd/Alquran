package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SetoranEntity
import com.example.data.SetoranRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SetoranRepository) : ViewModel() {

    val allSetoran: StateFlow<List<SetoranEntity>> = repository.allSetoran
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userName: StateFlow<String> = repository.userName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Hamba Allah"
        )

    val targetTadarusType: StateFlow<String> = repository.targetTadarusType
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Halaman"
        )

    val targetTadarusValue: StateFlow<Int> = repository.targetTadarusValue
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 10
        )

    fun addSetoran(setoran: SetoranEntity) {
        viewModelScope.launch {
            repository.insertSetoran(setoran)
        }
    }

    fun updateSetoran(setoran: SetoranEntity) {
        viewModelScope.launch {
            repository.updateSetoran(setoran)
        }
    }

    fun deleteSetoran(setoran: SetoranEntity) {
        viewModelScope.launch {
            repository.deleteSetoran(setoran)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAllSetoran()
        }
    }

    fun setUserName(name: String) {
        viewModelScope.launch {
            repository.setUserName(name)
        }
    }

    fun setTargetTadarus(type: String, value: Int) {
        viewModelScope.launch {
            repository.setTargetTadarus(type, value)
        }
    }

    fun exportDataToCsv(context: android.content.Context, uri: android.net.Uri, data: List<SetoranEntity>) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    java.io.OutputStreamWriter(outputStream).use { writer ->
                        writer.write("ID,Tanggal,Nama Surat,Ayat Awal,Ayat Akhir,Juz,Status,Catatan\n")
                        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale("id", "ID"))
                        data.forEach { setoran ->
                            val tanggal = format.format(java.util.Date(setoran.tanggal))
                            val catatan = setoran.catatan.replace("\"", "\"\"")
                            writer.write("${setoran.id},\"$tanggal\",\"${setoran.namaSurat}\",${setoran.ayatAwal},${setoran.ayatAkhir},${setoran.juz ?: ""},\"${setoran.status}\",\"$catatan\"\n")
                        }
                    }
                }
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Data berhasil diekspor", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Gagal mengekspor data", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

class MainViewModelFactory(private val repository: SetoranRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
