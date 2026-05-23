package com.example.data.repository

import com.example.data.database.ResumeDao
import com.example.data.model.Resume
import kotlinx.coroutines.flow.Flow

class ResumeRepository(private val resumeDao: ResumeDao) {
    val allResumes: Flow<List<Resume>> = resumeDao.getAllResumes()

    suspend fun getResumeById(id: Long): Resume? = resumeDao.getResumeById(id)

    suspend fun insert(resume: Resume): Long = resumeDao.insert(resume)

    suspend fun delete(resume: Resume) = resumeDao.delete(resume)

    suspend fun deleteById(id: Long) = resumeDao.deleteById(id)
}
