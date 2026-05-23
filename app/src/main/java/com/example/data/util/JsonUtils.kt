package com.example.data.util

import com.example.data.model.WorkExperience
import com.example.data.model.Education
import com.example.data.model.Project
import com.example.data.model.Certification
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsonUtils {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val workListAdapter = moshi.adapter<List<WorkExperience>>(
        Types.newParameterizedType(List::class.java, WorkExperience::class.java)
    )
    private val educationListAdapter = moshi.adapter<List<Education>>(
        Types.newParameterizedType(List::class.java, Education::class.java)
    )
    private val projectListAdapter = moshi.adapter<List<Project>>(
        Types.newParameterizedType(List::class.java, Project::class.java)
    )
    private val certListAdapter = moshi.adapter<List<Certification>>(
        Types.newParameterizedType(List::class.java, Certification::class.java)
    )
    private val skillsAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    fun workListToJson(list: List<WorkExperience>): String = workListAdapter.toJson(list)
    fun jsonToWorkList(json: String?): List<WorkExperience> {
        if (json.isNullOrEmpty()) return emptyList()
        return try { workListAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    fun educationListToJson(list: List<Education>): String = educationListAdapter.toJson(list)
    fun jsonToEducationList(json: String?): List<Education> {
        if (json.isNullOrEmpty()) return emptyList()
        return try { educationListAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    fun projectListToJson(list: List<Project>): String = projectListAdapter.toJson(list)
    fun jsonToProjectList(json: String?): List<Project> {
        if (json.isNullOrEmpty()) return emptyList()
        return try { projectListAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    fun certListToJson(list: List<Certification>): String = certListAdapter.toJson(list)
    fun jsonToCertList(json: String?): List<Certification> {
        if (json.isNullOrEmpty()) return emptyList()
        return try { certListAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    fun skillsToJson(list: List<String>): String = skillsAdapter.toJson(list)
    fun jsonToSkills(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        return try { skillsAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
}
