package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Resume
import com.example.data.model.WorkExperience
import com.example.data.repository.ResumeRepository
import com.example.data.api.GeminiHelper
import com.example.data.api.AtsScoreResult
import com.example.data.util.JsonUtils
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class ResumeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ResumeRepository
    private val moshi: Moshi = Moshi.Builder().build()
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = ResumeRepository(database.resumeDao())
    }

    val allResumes: StateFlow<List<Resume>> = repository.allResumes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentResume = MutableStateFlow<Resume?>(null)
    val currentResume: StateFlow<Resume?> = _currentResume.asStateFlow()

    // Loading indicators for AI operations
    val isSummaryGenerating = MutableStateFlow(false)
    val isDescriptionOptimizing = MutableStateFlow(false)
    val isSkillsSuggesting = MutableStateFlow(false)
    val isAtsScoring = MutableStateFlow(false)

    private val _skillsSuggestions = MutableStateFlow<List<String>>(emptyList())
    val skillsSuggestions: StateFlow<List<String>> = _skillsSuggestions.asStateFlow()

    private val _atsAssessment = MutableStateFlow<AtsScoreResult?>(null)
    val atsAssessment: StateFlow<AtsScoreResult?> = _atsAssessment.asStateFlow()

    private var autoSaveJob: Job? = null

    fun selectResume(id: Long) {
        if (id == -1L) {
            // High-fidelity pre-populated template data for quick test and visual polish
            val draft = Resume(
                title = "My Resume Draft",
                fullName = "Alex Mercer",
                email = "alex.mercer@gmail.com",
                phone = "+1 (555) 739-1052",
                location = "San Francisco, CA",
                website = "alexmercer.dev",
                github = "github.com/alexmercer",
                linkedin = "linkedin.com/in/alexmercer",
                targetRole = "Software Engineer",
                summary = "Innovative developer with 3+ years of experience constructing high-quality mobile products. Specialized in functional responsive system layouts and reactive client experiences.",
                skillsJson = JsonUtils.skillsToJson(listOf("Kotlin", "Jetpack Compose", "Android SDK", "Git", "REST APIs", "Clean Architecture")),
                workHistoryJson = JsonUtils.workListToJson(listOf(
                    WorkExperience(
                        id = "work_1",
                        jobTitle = "Software Developer",
                        company = "Apex Systems",
                        duration = "2023 - Present",
                        location = "Remote",
                        description = "• Developed high-fidelity mobile frontends using Jetpack Compose components.\n• Optimized memory usage in backgrounds, cutting thread resource leaks by 25%.\n• Coordinated structured code reviews and system release pipelines."
                    )
                )),
                educationJson = JsonUtils.educationListToJson(listOf(
                    com.example.data.model.Education(
                        id = "edu_1",
                        school = "Stanford University",
                        degree = "B.S. Computer Science",
                        duration = "2019 - 2023",
                        grade = "GPA 3.85"
                    )
                ))
            )
            viewModelScope.launch {
                val newId = repository.insert(draft)
                _currentResume.value = draft.copy(id = newId)
            }
        } else {
            viewModelScope.launch {
                val fetched = repository.getResumeById(id)
                _currentResume.value = fetched
                _atsAssessment.value = null // Reset evaluation score for new session
            }
        }
    }

    fun updateActiveResume(updated: Resume) {
        _currentResume.value = updated
        
        // Auto-save debounce logic
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(1000)
            repository.insert(updated.copy(lastModified = System.currentTimeMillis()))
        }
    }

    fun duplicateResume(resume: Resume) {
        viewModelScope.launch {
            val copy = resume.copy(
                id = 0,
                title = "${resume.title} (Copy)",
                lastModified = System.currentTimeMillis()
            )
            repository.insert(copy)
        }
    }

    fun deleteResume(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            if (_currentResume.value?.id == id) {
                _currentResume.value = null
            }
        }
    }

    fun generateSummary(role: String, level: String, skills: List<String>) {
        viewModelScope.launch {
            isSummaryGenerating.value = true
            val response = GeminiHelper.generateSummary(role, level, skills)
            _currentResume.value?.let { current ->
                updateActiveResume(current.copy(summary = response))
            }
            isSummaryGenerating.value = false
        }
    }

    fun optimizeExperience(experienceId: String, jobTitle: String, description: String, targetRole: String) {
        viewModelScope.launch {
            isDescriptionOptimizing.value = true
            val response = GeminiHelper.optimizeJobDescription(jobTitle, description, targetRole)
            _currentResume.value?.let { current ->
                val list = JsonUtils.jsonToWorkList(current.workHistoryJson).map { work ->
                    if (work.id == experienceId) work.copy(description = response) else work
                }
                updateActiveResume(current.copy(workHistoryJson = JsonUtils.workListToJson(list)))
            }
            isDescriptionOptimizing.value = false
        }
    }

    fun suggestSkillsForRole(role: String) {
        viewModelScope.launch {
            isSkillsSuggesting.value = true
            val skills = GeminiHelper.suggestSkills(role)
            _skillsSuggestions.value = skills
            isSkillsSuggesting.value = false
        }
    }

    fun runAtsCheck(targetRole: String) {
        viewModelScope.launch {
            val current = _currentResume.value ?: return@launch
            isAtsScoring.value = true
            
            // Build simple resume summary JSON to send to Gemini evaluator
            val inputString = "Name: ${current.fullName}, Role: ${current.targetRole}, Summary: ${current.summary}, Skills: ${current.skillsJson}, Experience: ${current.workHistoryJson}"
            val assessment = GeminiHelper.checkAtsCompatibility(inputString, targetRole)
            _atsAssessment.value = assessment
            isAtsScoring.value = false
        }
    }
}
