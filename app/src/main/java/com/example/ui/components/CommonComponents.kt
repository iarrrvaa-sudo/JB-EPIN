package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

fun Long.toRupiah(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(this).replace(",00", "").replace("Rp", "Rp ")
}

@Composable
fun JBEpinLogoHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 40.dp,
    showSubtext: Boolean = true,
    onLogoClick: (() -> Unit)? = null
) {
    // Subtle blinking and soft pulse animation behind the logo
    val infiniteTransition = rememberInfiniteTransition(label = "LogoPulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScalePulse"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(if (onLogoClick != null) Modifier.clickable { onLogoClick() } else Modifier)
            .testTag("jb_epin_header_logo")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(logoSize + 10.dp)
        ) {
            // Glowing ring background (Orange / Amber glow shadow)
            Box(
                modifier = Modifier
                    .size(logoSize + 8.dp)
                    .scale(scalePulse)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                OrangePrimary.copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Custom JB EPIN Logo Badge with custom image
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(logoSize)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AccentBrown, OrangePrimary)
                        )
                    )
                    .border(
                        1.dp,
                        OrangePrimary.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_jb_epin_app_logo),
                    contentDescription = "JB EPIN Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = "EPIN",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = TextPrimary
                )
            )
            if (showSubtext) {
                Text(
                    text = "ELITE GAMING HUB",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OrangePrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GamingGlassBorder,
    backgroundColor: Color = Color(0x0FFFFFFF), // Translucent glass fill (~6% white)
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(
                border = BorderStroke(1.dp, Brush.linearGradient(listOf(borderColor, OrangePrimary.copy(alpha = 0.25f)))),
                shape = shape
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun GradientGlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = GamingDarkSurfaceVariant
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .height(50.dp)
            .testTag("glow_button_${text.lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(
                            colors = listOf(OrangePrimaryVariant, OrangePrimary, RustTertiary)
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(GamingDarkSurfaceVariant, GamingDarkSurfaceVariant)
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    BorderStroke(1.dp, OrangeGlow.copy(alpha = if (enabled) 0.6f else 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) GamingDarkBackground else TextMuted
                    )
                )
            }
        }
    }
}

@Composable
fun CopyableNumberCard(
    label: String,
    number: String,
    accountHolder: String = "JB EPIN OFFICIAL"
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = OrangePrimary.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(color = OrangeGlow, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = number,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "A/N: $accountHolder",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }

            OutlinedButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(number))
                    copied = true
                },
                border = BorderStroke(1.dp, OrangePrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeGlow),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_number_button")
            ) {
                Icon(
                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (copied) "Tersalin!" else "Salin")
            }
        }
    }
}

@Composable
fun PaymentProofUploader(
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    onRemoveImage: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Upload Bukti Pembayaran *",
            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Wajib mengunggah screenshot bukti transfer resmi.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, OrangePrimary, RoundedCornerShape(12.dp))
                    .background(GamingDarkSurfaceVariant)
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Bukti Pembayaran",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .size(36.dp)
                            .background(GamingDarkSurface.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Ganti Image", tint = OrangeGlow)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onRemoveImage,
                        modifier = Modifier
                            .size(36.dp)
                            .background(StatusError.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Image", tint = Color.White)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(OrangePrimary, GamingBrownBorder))), RoundedCornerShape(12.dp))
                    .background(GamingDarkSurfaceVariant)
                    .clickable { launcher.launch("image/*") }
                    .testTag("upload_proof_box"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = OrangePrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Klik di sini untuk upload bukti transfer (Gambar)",
                        style = MaterialTheme.typography.bodyMedium.copy(color = OrangeGlow, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Format: JPG, PNG (Maks 5MB)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }
    }
}
