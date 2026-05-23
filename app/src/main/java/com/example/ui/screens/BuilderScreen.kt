package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.api.AtsScoreResult
import com.example.data.model.Education
import com.example.data.model.Project
import com.example.data.model.Resume
import com.example.data.model.WorkExperience
import com.example.data.util.JsonUtils
import com.example.data.util.PdfGenerator
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResumeViewModel
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BuilderScreen(
    resumeId: Long,
    viewModel: ResumeViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentResume by viewModel.currentResume.collectAsState()
    
    // Step configuration state
    var selectedStep by remember { mutableIntStateOf(0) }
    val steps = listOf("Contact", "Experience", "Academic & Projects", "Skills", "AI ATS Score", "Visual & Share")
    val tabScrollState = rememberScrollState()

    // Loading streams
    val isSummaryGenerating by viewModel.isSummaryGenerating.collectAsState()
    val isDescriptionOptimizing by viewModel.isDescriptionOptimizing.collectAsState()
    val isSkillsSuggesting by viewModel.isSkillsSuggesting.collectAsState()
    val isAtsScoring by viewModel.isAtsScoring.collectAsState()

    val suggestedSkillsList by viewModel.skillsSuggestions.collectAsState()
    val atsResultDetails by viewModel.atsAssessment.collectAsState()

    // Load active draft
    LaunchedEffect(resumeId) {
        viewModel.selectResume(resumeId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = Slate900,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Slate800)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = currentResume?.title?.ifEmpty { "Edit CV" } ?: "Loading Resume",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Step ${selectedStep + 1} of ${steps.size}: ${steps[selectedStep]}",
                        fontSize = 11.sp,
                        color = CyanSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->
        if (currentResume == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Slate900),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IndigoPrimary)
            }
        } else {
            val activeResume = currentResume!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Horizontal wizard step tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tabScrollState)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    steps.forEachIndexed { index, name ->
                        val isSelected = selectedStep == index
                        val bgFill = if (isSelected) IndigoPrimary else Slate800
                        val tc = if (isSelected) Color.White else Color.LightGray

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgFill)
                                .clickable { selectedStep = index }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = tc
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedStep) {
                        0 -> { // Contact & Header Personal
                            ContactSection(
                                resume = activeResume,
                                onUpdate = { viewModel.updateActiveResume(it) },
                                isSummaryGenerating = isSummaryGenerating,
                                onGenerateSummary = { role, exp, skills ->
                                    val skillsList = JsonUtils.jsonToSkills(activeResume.skillsJson)
                                    viewModel.generateSummary(role, exp, skillsList)
                                }
                            )
                        }
                        1 -> { // Work History
                            ExperienceSection(
                                resume = activeResume,
                                onUpdate = { viewModel.updateActiveResume(it) },
                                isOptimizing = isDescriptionOptimizing,
                                onOptimizeBulletPoints = { workId, job, desc ->
                                    viewModel.optimizeExperience(workId, job, desc, activeResume.targetRole)
                                }
                            )
                        }
                        2 -> { // Academic & Projects
                            EducationAndProjectsSection(
                                resume = activeResume,
                                onUpdate = { viewModel.updateActiveResume(it) }
                            )
                        }
                        3 -> { // Skills Segment with AI chips recommendation lists
                            SkillsSection(
                                resume = activeResume,
                                onUpdate = { viewModel.updateActiveResume(it) },
                                isSuggesting = isSkillsSuggesting,
                                suggested = suggestedSkillsList,
                                onRequestSuggestions = {
                                    val target = activeResume.targetRole.ifEmpty { "Software Developer" }
                                    viewModel.suggestSkillsForRole(target)
                                }
                            )
                        }
                        4 -> { // ATS Compatibility Gauge Evaluation
                            AtsScoreSection(
                                resume = activeResume,
                                onRunCheck = { viewModel.runAtsCheck(activeResume.targetRole) },
                                isAtsChecking = isAtsScoring,
                                result = atsResultDetails
                            )
                        }
                        5 -> { // Selection & Export Render Visuals
                            VisualAndExportSection(
                                context = context,
                                resume = activeResume,
                                onUpdate = { viewModel.updateActiveResume(it) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Master back/next control bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate800)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (selectedStep > 0) selectedStep-- },
                        enabled = selectedStep > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate900, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("step_back_button")
                    ) {
                        Text("Previous")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { 
                            if (selectedStep < steps.size - 1) {
                                selectedStep++
                            } else {
                                // Save & Exit
                                Toast.makeText(context, "Resumes draft auto-saved securely!", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("step_next_button")
                    ) {
                        Text(if (selectedStep == steps.size - 1) "Save & Exit" else "Next")
                    }
                }
            }
        }
    }
}

// 1. Personal Header Contacts Screen Tab Component
@Composable
fun ContactSection(
    resume: Resume,
    onUpdate: (Resume) -> Unit,
    isSummaryGenerating: Boolean,
    onGenerateSummary: (role: String, level: String, skills: List<String>) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Personal Information", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            
            OutlinedTextField(
                value = resume.fullName,
                onValueChange = { onUpdate(resume.copy(fullName = it)) },
                label = { Text("Full Name") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("edit_fullName")
            )

            OutlinedTextField(
                value = resume.targetRole,
                onValueChange = { onUpdate(resume.copy(targetRole = it)) },
                label = { Text("Target Professional Role") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("edit_targetRole")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = resume.email,
                    onValueChange = { onUpdate(resume.copy(email = it)) },
                    label = { Text("Email") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("edit_email")
                )

                OutlinedTextField(
                    value = resume.phone,
                    onValueChange = { onUpdate(resume.copy(phone = it)) },
                    label = { Text("Phone") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("edit_phone")
                )
            }

            OutlinedTextField(
                value = resume.location,
                onValueChange = { onUpdate(resume.copy(location = it)) },
                label = { Text("Location (e.g. Chicago, IL)") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = resume.website,
                    onValueChange = { onUpdate(resume.copy(website = it)) },
                    label = { Text("Website/Portfolio") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = resume.linkedin,
                    onValueChange = { onUpdate(resume.copy(linkedin = it)) },
                    label = { Text("LinkedIn URL") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Executive Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                
                Button(
                    onClick = { 
                        onGenerateSummary(
                            resume.targetRole.ifEmpty { "Software Engineer" },
                            resume.experienceLevel,
                            emptyList()
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("ai_summary_button")
                ) {
                    if (isSummaryGenerating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.0.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "AI summary", modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI Write", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            Text(
                text = "Target Experience Level",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyanSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Student", "Junior", "Mid-Level", "Senior").forEach { lvl ->
                    val select = resume.experienceLevel == lvl
                    val bg = if (select) IndigoPrimary else Slate900
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .clickable { onUpdate(resume.copy(experienceLevel = lvl)) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(lvl, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedTextField(
                value = resume.summary,
                onValueChange = { onUpdate(resume.copy(summary = it)) },
                label = { Text("Profile Summary Description") },
                minLines = 4,
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("edit_summary")
            )
        }
    }
}

// 2. Work Experiences Tab Component
@Composable
fun ExperienceSection(
    resume: Resume,
    onUpdate: (Resume) -> Unit,
    isOptimizing: Boolean,
    onOptimizeBulletPoints: (workId: String, job: String, desc: String) -> Unit
) {
    val workList = remember(resume.workHistoryJson) {
        JsonUtils.jsonToWorkList(resume.workHistoryJson).toMutableList()
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Work History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            
            Button(
                onClick = {
                    val newItem = WorkExperience(
                        id = "work_${System.currentTimeMillis()}",
                        jobTitle = "Software Analyst",
                        company = "Acme Labs",
                        duration = "2022 - 2024",
                        location = "San Jose, CA",
                        description = "• Formulated high speed algorithmic systems to translate raw information loads."
                    )
                    workList.add(newItem)
                    onUpdate(resume.copy(workHistoryJson = JsonUtils.workListToJson(workList)))
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Job", fontSize = 12.sp)
            }
        }

        workList.forEachIndexed { index, work ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Position #${index + 1}", fontSize = 13.sp, color = CyanSecondary, fontWeight = FontWeight.Bold)
                        
                        IconButton(
                            onClick = {
                                workList.removeAt(index)
                                onUpdate(resume.copy(workHistoryJson = JsonUtils.workListToJson(workList)))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = SoftRed, modifier = Modifier.size(18.dp))
                        }
                    }

                    OutlinedTextField(
                        value = work.jobTitle,
                        onValueChange = { text ->
                            workList[index] = work.copy(jobTitle = text)
                            onUpdate(resume.copy(workHistoryJson = JsonUtils.workListToJson(workList)))
                        },
                        label = { Text("Job Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = work.company,
                        onValueChange = { text ->
                            workList[index] = work.copy(company = text)
                            onUpdate(resume.copy(workHistoryJson = JsonUtils.workListToJson(workList)))
                        },
                        label = { Text("Company Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = work.duration,
                            onValueChange = { text ->
                                workList[index] = work.copy(duration = text)
                                onUpdate(resume.copy(workHistoryJson = JsonUtils.workListToJson(workList)))
                            },
                            label = { Text("Period (e.g. 2021-Present)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = work.location,
                            onValueChange = { text ->
                                workList[index] = work.copy(location = text)
                                onUpdate(resume.copy(workHistoryJson = JsonUtils.workListToJson(workList)))
                            },
                            label = { Text("Location") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    // Bullet descriptions and AI optimization box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Duties & Accomplishments", fontSize = 12.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
                        
                        Button(
                            onClick = { onOptimizeBulletPoints(work.id, work.jobTitle, work.description) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("ai_optimize_job_${work.id}")
                        ) {
                            if (isOptimizing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "AI Optimize", modifier = Modifier.size(12.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("AI ATS-Optimize", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = work.description,
                        onValueChange = { text ->
                            workList[index] = work.copy(description = text)
                            onUpdate(resume.copy(workHistoryJson = JsonUtils.workListToJson(workList)))
                        },
                        label = { Text("Bullet descriptions") },
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// 3. Education Histories + Custom Projects Screener
@Composable
fun EducationAndProjectsSection(
    resume: Resume,
    onUpdate: (Resume) -> Unit
) {
    val eduList = remember(resume.educationJson) {
        JsonUtils.jsonToEducationList(resume.educationJson).toMutableList()
    }
    val projectsList = remember(resume.projectsJson) {
        JsonUtils.jsonToProjectList(resume.projectsJson).toMutableList()
    }

    // A. Academic block editing
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Education History", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            
            Button(
                onClick = {
                    val newEdu = Education(
                        id = "edu_${System.currentTimeMillis()}",
                        school = "Tech State University",
                        degree = "M.S. Systems Management",
                        duration = "2020 - 2022",
                        grade = "GPA 3.9"
                    )
                    eduList.add(newEdu)
                    onUpdate(resume.copy(educationJson = JsonUtils.educationListToJson(eduList)))
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Academic", fontSize = 11.sp)
            }
        }

        eduList.forEachIndexed { index, edu ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Academic Degree #${index + 1}", fontSize = 12.sp, color = CyanSecondary, fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            eduList.removeAt(index)
                            onUpdate(resume.copy(educationJson = JsonUtils.educationListToJson(eduList)))
                        }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = SoftRed, modifier = Modifier.size(17.dp))
                        }
                    }

                    OutlinedTextField(
                        value = edu.degree,
                        onValueChange = { text ->
                            eduList[index] = edu.copy(degree = text)
                            onUpdate(resume.copy(educationJson = JsonUtils.educationListToJson(eduList)))
                        },
                        label = { Text("Degree Name (e.g. B.S. Chemistry)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = edu.school,
                        onValueChange = { text ->
                            eduList[index] = edu.copy(school = text)
                            onUpdate(resume.copy(educationJson = JsonUtils.educationListToJson(eduList)))
                        },
                        label = { Text("School Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = edu.duration,
                            onValueChange = { text ->
                                eduList[index] = edu.copy(duration = text)
                                onUpdate(resume.copy(educationJson = JsonUtils.educationListToJson(eduList)))
                            },
                            label = { Text("Period") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = edu.grade,
                            onValueChange = { text ->
                                eduList[index] = edu.copy(grade = text)
                                onUpdate(resume.copy(educationJson = JsonUtils.educationListToJson(eduList)))
                            },
                            label = { Text("Score/Metrics") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // B. Custom Projects block editing
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Highlights & Projects", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            
            Button(
                onClick = {
                    val newProj = Project(
                        id = "proj_${System.currentTimeMillis()}",
                        title = "E-Commerce Pipeline",
                        role = "Principal Developer",
                        duration = "3 Months",
                        description = "Engineered real-time database interfaces with 150ms transactional latencies."
                    )
                    projectsList.add(newProj)
                    onUpdate(resume.copy(projectsJson = JsonUtils.projectListToJson(projectsList)))
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Project", fontSize = 11.sp)
            }
        }

        projectsList.forEachIndexed { index, proj ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Project #${index + 1}", fontSize = 12.sp, color = CyanSecondary, fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            projectsList.removeAt(index)
                            onUpdate(resume.copy(projectsJson = JsonUtils.projectListToJson(projectsList)))
                        }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = SoftRed, modifier = Modifier.size(17.dp))
                        }
                    }

                    OutlinedTextField(
                        value = proj.title,
                        onValueChange = { text ->
                            projectsList[index] = proj.copy(title = text)
                            onUpdate(resume.copy(projectsJson = JsonUtils.projectListToJson(projectsList)))
                        },
                        label = { Text("Project Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = proj.role,
                            onValueChange = { text ->
                                projectsList[index] = proj.copy(role = text)
                                onUpdate(resume.copy(projectsJson = JsonUtils.projectListToJson(projectsList)))
                            },
                            label = { Text("Role") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.0f)
                        )

                        OutlinedTextField(
                            value = proj.duration,
                            onValueChange = { text ->
                                projectsList[index] = proj.copy(duration = text)
                                onUpdate(resume.copy(projectsJson = JsonUtils.projectListToJson(projectsList)))
                            },
                            label = { Text("Duration") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.0f)
                        )
                    }

                    OutlinedTextField(
                        value = proj.description,
                        onValueChange = { text ->
                            projectsList[index] = proj.copy(description = text)
                            onUpdate(resume.copy(projectsJson = JsonUtils.projectListToJson(projectsList)))
                        },
                        label = { Text("Short Description") },
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// 4. Skills Section Tab Component
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsSection(
    resume: Resume,
    onUpdate: (Resume) -> Unit,
    isSuggesting: Boolean,
    suggested: List<String>,
    onRequestSuggestions: () -> Unit
) {
    val skillsList = remember(resume.skillsJson) {
        JsonUtils.jsonToSkills(resume.skillsJson).toMutableList()
    }
    var entrySkillName by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Core Capabilities", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = entrySkillName,
                        onValueChange = { entrySkillName = it },
                        placeholder = { Text("Type skill, e.g. Figma, Swift", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanSecondary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.0f).testTag("skill_input_field")
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (entrySkillName.trim().isNotEmpty()) {
                                if (!skillsList.contains(entrySkillName.trim())) {
                                    skillsList.add(entrySkillName.trim())
                                    onUpdate(resume.copy(skillsJson = JsonUtils.skillsToJson(skillsList)))
                                }
                                entrySkillName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(52.dp).testTag("add_skill_btn")
                    ) {
                        Text("Add", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Flow of added skills
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    skillsList.forEach { skill ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate900)
                                .border(1.dp, IndigoPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(skill, fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = SoftRed,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        skillsList.remove(skill)
                                        onUpdate(resume.copy(skillsJson = JsonUtils.skillsToJson(skillsList)))
                                    }
                            )
                        }
                    }
                }
            }
        }

        // B. AI Suggestion Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Smart AI Skill Suggestions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Based on target role: ${resume.targetRole.ifEmpty { "Software Developer" }}", fontSize = 11.sp, color = Color.LightGray)
                    }

                    Button(
                        onClick = onRequestSuggestions,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isSuggesting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = "Suggest", modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Analyze", fontSize = 10.sp)
                            }
                        }
                    }
                }

                if (suggested.isNotEmpty()) {
                    Text("Tap to add directly to your CV:", fontSize = 11.sp, color = CyanSecondary, fontWeight = FontWeight.Bold)
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggested.forEach { skill ->
                            val alreadyHas = skillsList.contains(skill)
                            val bgCol = if (alreadyHas) Color.Gray.copy(alpha = 0.3f) else AccentPurple.copy(alpha = 0.15f)
                            val borderCol = if (alreadyHas) Color.Transparent else AccentPurple

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgCol)
                                    .border(1.dp, borderCol, RoundedCornerShape(8.dp))
                                    .clickable(enabled = !alreadyHas) {
                                        skillsList.add(skill)
                                        onUpdate(resume.copy(skillsJson = JsonUtils.skillsToJson(skillsList)))
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (alreadyHas) "$skill ✓" else "+ $skill",
                                    fontSize = 11.sp,
                                    color = if (alreadyHas) Color.LightGray else Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate900.copy(alpha = 0.5f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Insight", tint = CyanSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tap Analyze to fetch 8 professional tools optimized for ATS matching.", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

// 5. ATS Score circular representation gauge checker
@Composable
fun AtsScoreSection(
    resume: Resume,
    onRunCheck: () -> Unit,
    isAtsChecking: Boolean,
    result: AtsScoreResult?
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "ATS Scanner & Score Card",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            if (result == null) {
                // Score idle state
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Slate900),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Scan status", tint = CyanSecondary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Run Scan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Text(
                    text = "Evaluate your CV's keyword matching, structural integrity, and alignment with target role: \"${resume.targetRole.ifEmpty { "Software Developer" }}\".",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )

                Button(
                    onClick = onRunCheck,
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("run_ats_btn")
                ) {
                    if (isAtsChecking) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Analyze")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Compatibility Level")
                        }
                    }
                }
            } else {
                // Assessment results loaded
                val scoreSweepAngle = animateFloatAsState(
                    targetValue = (result.score.toFloat() / 100f) * 360f,
                    animationSpec = tween(durationMillis = 1000),
                    label = "sweep"
                )

                val gaugeAccent = when {
                    result.score >= 75 -> SoftGreen
                    result.score >= 50 -> SoftOrange
                    else -> SoftRed
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Custom Canvas Score Gauge
                    Box(
                        modifier = Modifier.size(126.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Backing track ring
                            drawCircle(
                                color = Color.LightGray.copy(alpha = 0.15f),
                                style = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Animated active sweep
                            drawArc(
                                color = gaugeAccent,
                                startAngle = -90f,
                                sweepAngle = scoreSweepAngle.value,
                                useCenter = false,
                                style = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${result.score}%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "ATS SCORE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        ScoreStatsIndicator("Keyword Matches", "${result.keywordsMatch}%", CyanSecondary)
                        ScoreStatsIndicator("Spelling/Grammar", "${result.grammarCheck}%", AccentPurple)
                        ScoreStatsIndicator("Target Role Match", if (result.score >= 70) "High" else "Medium", SoftGreen)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Matched & Missing Keywords lists
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1.0f)) {
                        Text("Matched Keywords", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                        result.strongKeywords.take(4).forEach { kw ->
                            Text("✓ $kw", fontSize = 10.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (result.strongKeywords.isEmpty()) Text("• Basic elements", fontSize = 10.sp, color = Color.LightGray)
                    }

                    Column(modifier = Modifier.weight(1.0f)) {
                        Text("Missing Keywords", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftOrange)
                        Spacer(modifier = Modifier.height(6.dp))
                        result.missingKeywords.take(4).forEach { kw ->
                            Text("! $kw", fontSize = 10.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (result.missingKeywords.isEmpty()) Text("• None detected", fontSize = 10.sp, color = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Structured suggestions list
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("AI Optimizer Recommendations", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    result.directRecommendations.take(3).forEach { rec ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Rec", tint = CyanSecondary, modifier = Modifier.size(13.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(rec, fontSize = 10.sp, color = Color.LightGray)
                        }
                    }
                }

                Button(
                    onClick = onRunCheck,
                    colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    if (isAtsChecking) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Re-assess Resume Compatibility", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreStatsIndicator(label: String, valText: String, color: Color) {
    Column {
        Text(label, fontSize = 10.sp, color = Color.LightGray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(valText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// 6. Visual Template selection real-time interactive preview + export print system
@Composable
fun VisualAndExportSection(
    context: Context,
    resume: Resume,
    onUpdate: (Resume) -> Unit
) {
    val templates = listOf(
        TemplateData("ats_friendly", "ATS Friendly", "Standard layout maximized for robot scanning, black on white.", IndigoSecondary),
        TemplateData("corporate", "Corporate Blue", "A blue-header slate corporate resume with elevated tags.", CyanSecondary),
        TemplateData("minimalist", "Minimal Gray", "Slate-toned quiet typography featuring thin divider banners.", Color.LightGray),
        TemplateData("creative", "Creative Pink", "Pink accented headings paired with custom structural gaps.", AccentPurple),
        TemplateData("dark", "Midnight Dark", "Stunning slate-black theme for cutting-edge developers.", Slate900)
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // A. Layout template switch selector
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Resume Template", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    templates.forEach { temp ->
                        val isSelected = resume.templateId == temp.id
                        val borderColor = if (isSelected) IndigoPrimary else Color.Transparent
                        val bg = if (isSelected) Slate900 else Slate900.copy(alpha = 0.5f)

                        Column(
                            modifier = Modifier
                                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .clickable { onUpdate(resume.copy(templateId = temp.id)) }
                                .padding(12.dp)
                                .width(110.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(temp.accent.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(temp.accent))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(temp.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // B. Dynamic live real-time preview (Simulated zoomed resume paper sheet)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("On-Screen Preview", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))

                // Simulated layout preview frame
                val previewDark = resume.templateId == "dark"
                val paperBg = if (previewDark) Slate900 else Color.White
                val txtPrimary = if (previewDark) Color.White else Slate900
                val txtSecondary = if (previewDark) Color.LightGray else Color.Gray

                val themeAccent = when (resume.templateId) {
                    "creative" -> AccentPurple
                    "corporate" -> CyanSecondary
                    "minimalist" -> Color.Gray
                    else -> IndigoSecondary
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(paperBg)
                        .border(1.dp, Slate900.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header inside preview
                    if (resume.templateId == "corporate") {
                        // blue band layout
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate800)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(resume.fullName.ifEmpty { "ALEX MERCER" }, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(resume.targetRole.ifEmpty { "Software Architect" }.uppercase(), fontSize = 9.sp, color = CyanSecondary)
                            }
                        }
                    } else {
                        // regular banner
                        Column {
                            Text(resume.fullName.ifEmpty { "ALEX MERCER" }, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = txtPrimary)
                            Text(resume.targetRole.ifEmpty { "Software Architect" }.uppercase(), fontSize = 9.sp, color = themeAccent, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Contact sublines
                    Text(
                        text = "${resume.email.ifEmpty { "alex@gmail.com" }}  |  ${resume.phone.ifEmpty { "+1 555-0342" }}  |  ${resume.location.ifEmpty { "SF, CA" }}",
                        fontSize = 8.sp,
                        color = txtSecondary
                    )

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(themeAccent.copy(alpha = 0.3f)))

                    // Summary
                    if (resume.summary.isNotEmpty()) {
                        Text("PROFESSIONAL SUMMARY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = themeAccent)
                        Text(resume.summary, fontSize = 8.sp, color = txtPrimary, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }

                    // Work
                    val history = JsonUtils.jsonToWorkList(resume.workHistoryJson)
                    if (history.isNotEmpty()) {
                        Text("WORK EXPERIENCE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = themeAccent)
                        val primaryJob = history.first()
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${primaryJob.jobTitle} - ${primaryJob.company}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = txtPrimary)
                                Text(primaryJob.duration, fontSize = 7.5.sp, color = txtSecondary)
                            }
                            Text(primaryJob.description, fontSize = 7.5.sp, color = txtPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    // Skills list
                    val skills = JsonUtils.jsonToSkills(resume.skillsJson)
                    if (skills.isNotEmpty()) {
                        Text("CORE SKILLS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = themeAccent)
                        Text(skills.joinToString("   •   "), fontSize = 8.sp, color = txtPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        // C. Share & Print Trigger actions
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Export PDF Document", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Export print button
                    Button(
                        onClick = {
                            val file = PdfGenerator.generateResumePdf(context, resume)
                            if (file != null) {
                                Toast.makeText(context, "Successfully generated: ${file.name}", Toast.LENGTH_LONG).show()
                                
                                // Launch standard android open view intent
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Error saving PDF draft", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.2f).height(50.dp).testTag("download_pdf_btn")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Download")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download PDF", fontSize = 12.sp)
                    }

                    // Export sharing button
                    Button(
                        onClick = {
                            val file = PdfGenerator.generateResumePdf(context, resume)
                            if (file != null) {
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share My professional CV"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.8f).height(50.dp).testTag("share_pdf_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Data holder classes for templates mapping
data class TemplateData(
    val id: String,
    val name: String,
    val desc: String,
    val accent: Color
)
