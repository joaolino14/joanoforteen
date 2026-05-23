package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Resume
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.ResumeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: ResumeViewModel,
    onNavigateToBuilder: (Long) -> Unit,
    onLogout: () -> Unit
) {
    val resumes by viewModel.allResumes.collectAsState()
    var searchPrompt by remember { mutableStateOf("") }
    
    val filteredResumes = resumes.filter {
        it.title.contains(searchPrompt, ignoreCase = true) || 
        it.targetRole.contains(searchPrompt, ignoreCase = true) ||
        it.fullName.contains(searchPrompt, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF6F8FB),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToBuilder(-1L) }, // Creates new draft
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("create_resume_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New CV",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Header Profile row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AccentPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hello, Career Creator",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Optimize your industry CV with AI",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
                
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                ) {
                    Text("Exit", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Search Bar Area
            OutlinedTextField(
                value = searchPrompt,
                onValueChange = { searchPrompt = it },
                placeholder = { Text("Search CVs, roles, keywords...", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyanSecondary
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("search_bar")
            )

            Text(
                text = "My Resumes",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            if (filteredResumes.isEmpty()) {
                // Beautiful Empty slate placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(32.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Drafts empty",
                                tint = IndigoSecondary,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = if (searchPrompt.isNotEmpty()) "No matching resumes found" else "Establish Your Career Path",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchPrompt.isNotEmpty()) "Try editing your search query keywords" else "Create your first professional ATS-ready resume and get noticed.",
                            fontSize = 12.sp,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(260.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredResumes, key = { it.id }) { resume ->
                        ResumeDashboardCard(
                            resume = resume,
                            onEdit = { onNavigateToBuilder(resume.id) },
                            onDuplicate = { viewModel.duplicateResume(resume) },
                            onDelete = { viewModel.deleteResume(resume.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResumeDashboardCard(
    resume: Resume,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val dateText = remember(resume.lastModified) {
        val formatter = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        formatter.format(Date(resume.lastModified))
    }

    val outlineColor by animateColorAsState(
        targetValue = when (resume.templateId) {
            "creative" -> AccentPurple
            "corporate" -> CyanSecondary
            "dark" -> Color.Black
            else -> IndigoSecondary
        },
        label = "color"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
            .clickable { onEdit() }
            .testTag("resume_card_${resume.id}")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Headline title
                    Text(
                        text = resume.fullName.ifEmpty { "Alex Mercer" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = resume.targetRole.ifEmpty { "Draft Application" },
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                }
                
                // Color accent banner tagging
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(outlineColor.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = resume.templateId.uppercase(),
                        fontSize = 10.sp,
                        color = outlineColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub text metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateText,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Row {
                    IconButton(
                        onClick = onDuplicate,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF2F6))
                            .testTag("duplicate_button_${resume.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Duplicate",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.08f))
                            .testTag("edit_button_${resume.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = IndigoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.06f))
                            .testTag("delete_button_${resume.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
