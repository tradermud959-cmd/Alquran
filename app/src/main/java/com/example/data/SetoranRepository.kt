package com.example.data

import kotlinx.coroutines.flow.Flow

class SetoranRepository(
    private val setoranDao: SetoranDao,
    private val settingsManager: SettingsManager
) {
    val allSetoran: Flow<List<SetoranEntity>> = setoranDao.getAllSetoran()
    val userName: Flow<String> = settingsManager.userNameFlow
    val targetTadarusType: Flow<String> = settingsManager.targetTadarusTypeFlow
    val targetTadarusValue: Flow<Int> = settingsManager.targetTadarusValueFlow

    suspend fun insertSetoran(setoran: SetoranEntity) {
        setoranDao.insertSetoran(setoran)
    }

    suspend fun updateSetoran(setoran: SetoranEntity) {
        setoranDao.updateSetoran(setoran)
    }

    suspend fun deleteSetoran(setoran: SetoranEntity) {
        setoranDao.deleteSetoran(setoran)
    }

    suspend fun deleteAllSetoran() {
        setoranDao.deleteAll()
    }

    suspend fun setUserName(name: String) {
        settingsManager.setUserName(name)
    }

    suspend fun setTargetTadarus(type: String, value: Int) {
        settingsManager.setTargetTadarus(type, value)
    }
}
