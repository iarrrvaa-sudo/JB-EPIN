package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OrderSuccessScreen(
    orderId: String,
    onBackHome: () -> Unit,
    onViewProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = OrangePrimary
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Surface(
                    color = StatusSuccess.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = StatusSuccess,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pesanan Berhasil Dikirim!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nomor Pesanan: $orderId",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = OrangeGlow,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pesanan Anda sedang dalam proses verifikasi oleh tim JB EPIN. Status dan detail transaksi dapat dipantau di menu Profil Anda.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                GradientGlowButton(
                    text = "KEMBALI KE BERANDA",
                    onClick = onBackHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("back_home_button")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LIHAT PESANAN SAYA", color = OrangeGlow, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
