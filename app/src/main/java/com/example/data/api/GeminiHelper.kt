package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiHelper {
    private val apiService = RetrofitClient.service
    private val moshi = RetrofitClient.moshi

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    suspend fun generateSummary(
        role: String,
        experienceLevel: String,
        skills: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Proactive and results-driven professional seeking a role as $role, with a focus on level $experienceLevel. Possesses strong analytical and technical proficiency including: ${skills.joinToString(", ")}."
        }

        val prompt = """
            Write a 3-4 sentence professional resume executive summary for a candidate applying for the role of "$role" at experience level "$experienceLevel".
            Highlight these skills: ${skills.joinToString(", ")}.
            Focus on business impact, modern industry terminology, and high readability. Do not mention "as requested by user", do not include any bullet points or titles. Return it as a single coherent paragraph.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: "Motivated professional qualified for $role. Experience level: $experienceLevel."
        } catch (e: Exception) {
            Log.e("GeminiHelper", "Summary generation failed", e)
            "Results-driven $role specializing in $experienceLevel delivery. Proficient in key skills: ${skills.joinToString(", ")}."
        }
    }

    suspend fun optimizeJobDescription(
        jobTitle: String,
        description: String,
        targetRole: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "• Spearheaded deployment of premium frontend systems as $jobTitle, fully aligned with $targetRole parameters.\n• Achieved a 25% improvement in application fluidness and performance optimization.\n• Structured unit test configurations and coordinated code review sessions."
        }

        val prompt = """
            You are an expert ATS-focused resume writer. Optimize the following job experience description for a "$jobTitle" to make it highly professional, keyword-optimized, and outstandingly aligned with target role "$targetRole".
            
            Input description: 
            $description

            Rules:
            1. Rewrite as 2 to 3 action-packed bullet points starting with strong power verbs (e.g., Executed, Built, Accelerated, Engineered).
            2. Quantify results where possible (if there are no metrics, create highly plausible, industry-realistic metrics e.g., 'boosting efficiency by 15%').
            3. Do not write any intro, outro, explanations, or notes. Return ONLY the bullet points, each on a new line starting with "• ".
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.6f)
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: description
        } catch (e: Exception) {
            Log.e("GeminiHelper", "Description optimization failed", e)
            description
        }
    }

    suspend fun suggestSkills(role: String): List<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val defaultSkills = listOf("Kotlin", "Jetpack Compose", "Android SDK", "Git", "Clean Architecture", "REST APIs", "CI/CD", "Testing")
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext defaultSkills
        }

        val prompt = """
            Suggest exactly 8 key tools or professional skills for a candidate looking to become a "$role".
            Return your response in a simple JSON array of strings, like this: ["Skill 1", "Skill 2", "Skill 3"].
            Do NOT include markdown backticks (such as ```json or ```). Return ONLY the strict JSON array text.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(responseMimeType = "application/json", temperature = 0.4f)
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""
            val cleanJson = jsonText.removeSurrounding("```json", "```").trim()
            val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, String::class.java)
            val adapter = moshi.adapter<List<String>>(listType)
            adapter.fromJson(cleanJson) ?: defaultSkills
        } catch (e: Exception) {
            Log.e("GeminiHelper", "Skills suggestion failed", e)
            defaultSkills
        }
    }

    suspend fun checkAtsCompatibility(
        resumeDataJson: String,
        targetRole: String
    ): AtsScoreResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val fallbackResult = AtsScoreResult(
            score = 78,
            keywordsMatch = 72,
            grammarCheck = 85,
            strongKeywords = listOf("Communication", "Leadership", "Technical Alignment"),
            missingKeywords = listOf("Target Metrics", "ATS Keywords", "Standard Formatting"),
            directRecommendations = listOf(
                "Integrate prominent industry keywords matching the target role.",
                "Ensure work achievements start with strong power verbs.",
                "Quantify your career descriptions with specific metric improvements."
            )
        )
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackResult
        }

        val prompt = """
            Analyze the following resume JSON and evaluate its ATS matching compatibility with the target role "$targetRole".
            
            Resume Data:
            $resumeDataJson

            Return a structured JSON object reflecting the score details, key matching keywords, missing keywords, and actionable recommendations.
            Do NOT write any markdown backticks. Return ONLY the raw JSON matching this structure exactly:
            {
              "score": 75,
              "keywordsMatch": 68,
              "grammarCheck": 88,
              "strongKeywords": ["Android SDK", "Kotlin"],
              "missingKeywords": ["Moshi", "Firebase Build"],
              "directRecommendations": ["Recommendation A", "Recommendation B"]
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(responseMimeType = "application/json", temperature = 0.4f)
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""
            val cleanJson = jsonText.removeSurrounding("```json", "```").trim()
            val adapter = moshi.adapter(AtsScoreResult::class.java)
            adapter.fromJson(cleanJson) ?: fallbackResult
        } catch (e: Exception) {
            Log.e("GeminiHelper", "ATS verification failed", e)
            fallbackResult
        }
    }
}
