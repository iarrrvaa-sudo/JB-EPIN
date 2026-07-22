package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onBack: () -> Unit,
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (username: String, email: String, pass: String, confirmPass: String) -> Unit
) {
    var isLoginTab by remember { mutableStateOf(true) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = OrangeGlow)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        JBEpinLogoHeader(logoSize = 72.dp, showSubtext = true)

        Spacer(modifier = Modifier.height(32.dp))

        // Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GamingDarkSurfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { isLoginTab = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLoginTab) OrangePrimary else Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_login")
            ) {
                Text("Login", color = if (isLoginTab) GamingDarkBackground else TextPrimary, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { isLoginTab = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isLoginTab) OrangePrimary else Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_register")
            ) {
                Text("Register", color = if (!isLoginTab) GamingDarkBackground else TextPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            if (isLoginTab) {
                Text("Selamat Datang Kembali!", style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                Text("Masuk ke akun JB EPIN Anda", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("Contoh: user@jbepin.com") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = GamingBrownBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = GamingBrownBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_password_input")
                )

                Spacer(modifier = Modifier.height(24.dp))

                GradientGlowButton(
                    text = "MASUK SEKARANG",
                    onClick = { onLogin(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_login_submit_button")
                )
            } else {
                Text("Buat Akun Baru", style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                Text("Bergabung di platform game top up & marketplace", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = GamingBrownBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_username_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = GamingBrownBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_email_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = GamingBrownBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_password_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Konfirmasi Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = GamingBrownBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_confirm_password_input")
                )

                Spacer(modifier = Modifier.height(24.dp))

                GradientGlowButton(
                    text = "DAFTAR AKUN JB EPIN",
                    onClick = { onRegister(username, email, password, confirmPassword) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_register_submit_button")
                )
            }
        }
    }
}
