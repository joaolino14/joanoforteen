package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }
    
    // Animation flags for sleek entering state
    var entranceTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        entranceTriggered = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FB))
    ) {
        // Decorative soft radial layout element in background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            IndigoPrimary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = entranceTriggered,
                enter = fadeIn(animationSpec = spring()) + slideInVertically(
                    initialOffsetY = { -50 },
                    animationSpec = spring()
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Logo Banner Icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(IndigoPrimary, CyanSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "CV Logo",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CV Maker AI",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Unlock your dream career in minutes",
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = entranceTriggered,
                enter = fadeIn(animationSpec = spring(stiffness = 150f)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = spring(stiffness = 150f)
                )
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (showForgotPassword) "Reset Password" else "Sign In",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Form Input
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = CyanSecondary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IndigoPrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedLabelColor = IndigoPrimary,
                                unfocusedLabelColor = Color(0xFF64748B),
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input")
                        )

                        if (!showForgotPassword) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // Password Form Input
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock",
                                        tint = CyanSecondary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Info else Icons.Default.Lock,
                                            contentDescription = "Password toggle",
                                            tint = Color.LightGray
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = IndigoPrimary,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedLabelColor = IndigoPrimary,
                                    unfocusedLabelColor = Color(0xFF64748B),
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("password_input")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Biometrics Option Switch Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { biometricEnabled = !biometricEnabled }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Biometrics",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Biometric Login",
                                        fontSize = 13.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                                Switch(
                                    checked = biometricEnabled,
                                    onCheckedChange = { biometricEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = IndigoPrimary
                                    ),
                                    modifier = Modifier.testTag("biometric_switch")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Submit Button
                        Button(
                            onClick = { onLoginSuccess() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IndigoPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_button")
                        ) {
                            Text(
                                text = if (showForgotPassword) "Send Link" else "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        TextButton(
                            onClick = { showForgotPassword = !showForgotPassword }
                        ) {
                            Text(
                                text = if (showForgotPassword) "Back to Sign In" else "Forgot Password?",
                                color = CyanSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Guest Launch Button/Label Option
            AnimatedVisibility(visible = entranceTriggered) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Or continue securely",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedButton(
                        onClick = { onLoginSuccess() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF0F172A)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("guest_login_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Guest",
                                tint = AccentPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Connect as Guest (Offline)", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
