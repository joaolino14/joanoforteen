package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Resume
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes ORDER BY lastModified DESC")
    fun getAllResumes(): Flow<List<Resume>>

    @Query("SELECT * FROM resumes WHERE id = :id")
    suspend fun getResumeById(id: Long): Resume?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resume: Resume): Long

    @Delete
    suspend fun delete(resume: Resume)

    @Query("DELETE FROM resumes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
