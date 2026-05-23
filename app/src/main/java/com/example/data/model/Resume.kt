package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "resumes")
data class Resume(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val lastModified: Long = System.currentTimeMillis(),
    val templateId: String = "ats_friendly", // "ats_friendly", "corporate", "minimalist", "creative", "dark"
    
    // Personal Information
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val website: String = "",
    val github: String = "",
    val linkedin: String = "",
    val summary: String = "",
    val targetRole: String = "",
    val experienceLevel: String = "Mid-Level", // "Student", "Junior", "Mid-Level", "Senior"
    
    // Serialized Lists
    val workHistoryJson: String = "[]",
    val educationJson: String = "[]",
    val skillsJson: String = "[]",
    val projectsJson: String = "[]",
    val certificationsJson: String = "[]"
)

@JsonClass(generateAdapter = true)
data class WorkExperience(
    val id: String,
    val jobTitle: String = "",
    val company: String = "",
    val duration: String = "", // e.g. "Oct 2023 - Present"
    val location: String = "",
    val description: String = "" // Professional description bullet points
)

@JsonClass(generateAdapter = true)
data class Education(
    val id: String,
    val degree: String = "",
    val school: String = "",
    val duration: String = "", // e.g. "2019 - 2023"
    val grade: String = "" // e.g. "GPA 3.8"
)

@JsonClass(generateAdapter = true)
data class Project(
    val id: String,
    val title: String = "",
    val role: String = "",
    val duration: String = "",
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class Certification(
    val id: String,
    val name: String = "",
    val issuer: String = "",
    val year: String = ""
)
