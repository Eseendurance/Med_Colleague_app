package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedColleagueDao {
    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()

    // High Yield Pearls
    @Query("SELECT * FROM high_yield_pearls ORDER BY timestamp DESC")
    fun getAllPearls(): Flow<List<PearlEntity>>

    @Query("SELECT * FROM high_yield_pearls WHERE nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    fun getDuePearls(currentTime: Long = System.currentTimeMillis()): Flow<List<PearlEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPearl(pearl: PearlEntity): Long

    @Update
    suspend fun updatePearl(pearl: PearlEntity)

    @Query("DELETE FROM high_yield_pearls WHERE id = :id")
    suspend fun deletePearlById(id: Long)

    // Case Vignettes
    @Query("SELECT * FROM case_vignettes ORDER BY id DESC")
    fun getAllCaseVignettes(): Flow<List<CaseVignetteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaseVignettes(vignettes: List<CaseVignetteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleCaseVignette(vignette: CaseVignetteEntity): Long

    @Update
    suspend fun updateCaseVignette(vignette: CaseVignetteEntity)

    @Query("DELETE FROM case_vignettes")
    suspend fun clearCaseVignettes()
}
