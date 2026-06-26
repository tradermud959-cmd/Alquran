package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SetoranDao {
    @Query("SELECT * FROM setoran ORDER BY tanggal DESC")
    fun getAllSetoran(): Flow<List<SetoranEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetoran(setoran: SetoranEntity)
    
    @Update
    suspend fun updateSetoran(setoran: SetoranEntity)

    @Delete
    suspend fun deleteSetoran(setoran: SetoranEntity)
    
    @Query("DELETE FROM setoran")
    suspend fun deleteAll()
}
