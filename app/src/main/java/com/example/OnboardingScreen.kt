package com.example

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import kotlinx.coroutines.launch

// High-fidelity Onboarding Screens for "20-20-20 Eye Care"
// Designed entirely under Material Design 3 dark aesthetic principles.

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Colors according to M3 luxury dark requirements
    val pureBlack = Color(0xFF000000)
    val lemonGreen = Color(0xFFC5E384)   // Primary Highlight
    val geekCyan = Color(0xFF00E5FF)     // Icon and secondary highlight
    val surfaceVariant = Color(0xFF121212) // Dark texture card

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(pureBlack),
        containerColor = pureBlack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 28.dp, bottom = 48.dp), // Keeps bottom button exactly 48.dp from screen bottom
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Horizontal Pager with 4 slides
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                // Apply subtle parallax/zoom transitions on page scroll
                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                val alpha = (1f - pageOffset.coerceIn(-1f, 1f).let { if (it < 0) -it else it }).coerceIn(0f, 1f)
                val scale = (1f - 0.05f * (pageOffset.coerceIn(-1f, 1f).let { if (it < 0) -it else it })).coerceIn(0.9f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            this.alpha = alpha
                            this.scaleX = scale
                            this.scaleY = scale
                        }
                ) {
                    when (page) {
                        0 -> IntroduceRuleStep(lemonGreen, geekCyan, surfaceVariant)
                        1 -> BenefitsStep(lemonGreen, geekCyan, surfaceVariant)
                        2 -> PrivacyStep(lemonGreen, geekCyan, surfaceVariant)
                        3 -> WelcomeActionStep(lemonGreen, geekCyan, surfaceVariant)
                    }
                }
            }

            // Bottom Navigation Region
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                // Dot Indicator
                Row(
                    modifier = Modifier
                        .padding(bottom = 32.dp) // Generous breathing room
                        .height(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "dotWidth"
                        )
                        val color = if (isSelected) lemonGreen else Color.Gray.copy(alpha = 0.4f)

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = width, height = 8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Full-width pill-shaped "下一步 / 立即开启" Button
                val isLastPage = pagerState.currentPage == 3
                Button(
                    onClick = {
                        if (isLastPage) {
                            showPermissionDialog = true
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lemonGreen,
                        contentColor = pureBlack
                    ),
                    shape = RoundedCornerShape(50.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (isLastPage) "立即开启" else "下一步",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }

    if (showPermissionDialog) {
        PermissionRequestDialog(
            onDismiss = {
                showPermissionDialog = false
                onFinished()
            },
            onConfirm = {
                showPermissionDialog = false
                onFinished()
            }
        )
    }
}

// ==========================================
// SCREEN 1: What is the 20-20-20 Rule
// ==========================================
@Composable
fun IntroduceRuleStep(lemonGreen: Color, geekCyan: Color, surfaceVariant: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        SlideTitle("20-20-20 法则", lemonGreen)
        
        Spacer(modifier = Modifier.height(20.dp)) // Increased spacing
        
        SlideSubtitle("每用眼 20 分钟\n眺望 20 英尺外 20 秒，科学缓解疲劳。")

        Spacer(modifier = Modifier.height(48.dp)) // Increased spacing

        // Center visual indicator card: 3 circles
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceVariant),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background connection lines using Canvas
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    
                    // Connected path
                    val path = Path().apply {
                        moveTo(w * 0.22f, h * 0.5f)
                        quadraticTo(w * 0.5f, h * 0.2f, w * 0.78f, h * 0.5f)
                    }
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.15f),
                        style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    )

                    val pathBottom = Path().apply {
                        moveTo(w * 0.78f, h * 0.5f)
                        quadraticTo(w * 0.5f, h * 0.8f, w * 0.22f, h * 0.5f)
                    }
                    drawPath(
                        path = pathBottom,
                        color = Color.White.copy(alpha = 0.15f),
                        style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricCircle("20", "分钟", "专注工作", lemonGreen, geekCyan, surfaceVariant, modifier = Modifier.weight(1f))
                    MetricCircle("20", "英尺", "远眺放松", lemonGreen, geekCyan, surfaceVariant, modifier = Modifier.weight(1f))
                    MetricCircle("20", "秒", "闭眼/休息", lemonGreen, geekCyan, surfaceVariant, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun MetricCircle(value: String, unit: String, label: String, lemonGreen: Color, geekCyan: Color, surface: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(surface)
                .border(2.dp, Brush.radialGradient(listOf(geekCyan, lemonGreen)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = unit,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = geekCyan
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

// ==========================================
// SCREEN 2: Benefits step
// ==========================================
@Composable
fun BenefitsStep(lemonGreen: Color, geekCyan: Color, surfaceVariant: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        SlideTitle("科学微休，舒适体验", lemonGreen)
        
        Spacer(modifier = Modifier.height(20.dp)) // Increased spacing
        
        SlideSubtitle("规律眼部休息，告别长时间直视屏幕带来的酸涩与疲劳。")

        Spacer(modifier = Modifier.height(40.dp)) // Increased spacing

        // Chart Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(130.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceVariant),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                // Tiny rising comfort graph
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw comfort grid lines
                    for (i in 1..3) {
                        val y = h * (i / 4f)
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw smooth comfort rise path
                    val points = listOf(
                        Offset(w * 0.05f, h * 0.85f),
                        Offset(w * 0.2f, h * 0.78f),
                        Offset(w * 0.35f, h * 0.72f),
                        Offset(w * 0.5f, h * 0.5f),
                        Offset(w * 0.65f, h * 0.42f),
                        Offset(w * 0.8f, h * 0.22f),
                        Offset(w * 0.95f, h * 0.15f)
                    )

                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val pPrev = points[i - 1]
                            val pNext = points[i]
                            val cx = (pPrev.x + pNext.x) / 2f
                            cubicTo(cx, pPrev.y, cx, pNext.y, pNext.x, pNext.y)
                        }
                    }

                    // Draw background glow
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(points.last().x, h)
                        lineTo(points.first().x, h)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            listOf(lemonGreen.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )

                    // Draw outer main gradient curve
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(listOf(geekCyan, lemonGreen)),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw points
                    for (p in points) {
                        drawCircle(color = geekCyan, radius = 4.dp.toPx(), center = p)
                        drawCircle(color = Color.Black, radius = 1.5.dp.toPx(), center = p)
                    }
                }

                // X-axis text labels overlaid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("第1天", "第2天", "第3天", "第4天", "第5天", "第6天", "第7天")
                    for (d in days) {
                        Text(d, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    "双眼舒适度逐日提升 (%)",
                    fontSize = 11.sp,
                    color = lemonGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp)) // Increased spacing

        // Bullet list (Consolidated for better layout spacing)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BenefitItem(Icons.Default.Favorite, "减轻长时用眼的酸胀与紧绷", geekCyan)
            BenefitItem(Icons.Default.Face, "保持双眼温润舒畅不干涩", geekCyan)
            BenefitItem(Icons.Default.Star, "提升专注力与全天舒适感", geekCyan)
        }
    }
}

@Composable
fun BenefitItem(icon: ImageVector, text: String, geekCyan: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0C0C0C))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = geekCyan,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.Medium
        )
    }
}

// ==========================================
// SCREEN 3: Privacy and Data security
// ==========================================
@Composable
fun PrivacyStep(lemonGreen: Color, geekCyan: Color, surfaceVariant: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        SlideTitle("100% 本地隐私保护", lemonGreen)
        
        Spacer(modifier = Modifier.height(20.dp)) // Increased spacing
        
        SlideSubtitle("纯单机离线运行，所有设置与习惯仅保存在本地。")

        Spacer(modifier = Modifier.height(40.dp)) // Increased spacing

        // Secure Lock/Shield Canvas Inside Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(115.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceVariant),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    val w = size.width
                    val h = size.height

                    // Draw a protective cyber shield
                    val shieldPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.1f)
                        lineTo(w * 0.85f, h * 0.2f)
                        quadraticTo(w * 0.85f, h * 0.6f, w * 0.5f, h * 0.9f)
                        quadraticTo(w * 0.15f, h * 0.6f, w * 0.15f, h * 0.2f)
                        close()
                    }
                    drawPath(
                        path = shieldPath,
                        color = geekCyan.copy(alpha = 0.15f)
                    )
                    drawPath(
                        path = shieldPath,
                        brush = Brush.radialGradient(listOf(geekCyan, lemonGreen)),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw Lock inside
                    drawRoundRect(
                        color = lemonGreen,
                        topLeft = Offset(w * 0.38f, h * 0.45f),
                        size = Size(w * 0.24f, h * 0.22f),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    drawArc(
                        color = lemonGreen,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.42f, h * 0.32f),
                        size = Size(w * 0.16f, h * 0.25f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp)) // Increased spacing

        // Privacy Bullet Points
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PrivacyBullet(Icons.Default.Done, "离线安全运行", "作息偏好均完好保存于本地安全存储中。", geekCyan)
            PrivacyBullet(Icons.Default.Warning, "无需因特网", "零网络请求，绝不上传或者监控任何生活偏好。", geekCyan)
            PrivacyBullet(Icons.Default.Lock, "绝免摄像头要求", "纯计时器运转，永远不需要拍摄眼球或采集摄像头。", geekCyan)
        }
    }
}

@Composable
fun PrivacyBullet(icon: ImageVector, title: String, desc: String, geekCyan: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0C0C0C))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = geekCyan,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = Color.LightGray.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// ==========================================
// SCREEN 4: Welcome & Action Quick Grid
// ==========================================
@Composable
fun WelcomeActionStep(lemonGreen: Color, geekCyan: Color, surfaceVariant: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        SlideTitle("科学自主掌控", lemonGreen)
        
        Spacer(modifier = Modifier.height(20.dp)) // Increased spacing
        
        SlideSubtitle("自定义最契合打工、代码或创作节奏的科学用眼护航。")

        Spacer(modifier = Modifier.height(36.dp)) // Increased spacing

        // Center visual: Concentric circles with large Done icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = geekCyan.copy(alpha = 0.08f),
                    radius = size.width * 0.45f
                )
                drawCircle(
                    color = lemonGreen.copy(alpha = 0.12f),
                    radius = size.width * 0.35f
                )
            }
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = lemonGreen,
                modifier = Modifier.size(48.dp)
            )
        }
        
        Text(
            text = "Eye Guard 护眼助手",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp)
        )

        Spacer(modifier = Modifier.height(32.dp)) // Increased spacing

        // Bottom Configuration Quick Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard("手动启停控制", "一键自由开启护眼计时状态", surfaceVariant, lemonGreen, modifier = Modifier.weight(1f))
                FeatureCard("时长配置随心", "自定义您的专属专注与远眺时长", surfaceVariant, lemonGreen, modifier = Modifier.weight(1f))
            }
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard("高质背景大图", "舒缓、高对比度的亮丽绿意效果", surfaceVariant, lemonGreen, modifier = Modifier.weight(1f))
                FeatureCard("静默后台提醒", "极简的手机后台振动与声音机制", surfaceVariant, lemonGreen, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String, surface: Color, accent: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(78.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = accent,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.LightGray.copy(alpha = 0.6f),
                lineHeight = 13.sp,
                maxLines = 2
            )
        }
    }
}

// Global Text Helpers to keep layout consistent
@Composable
fun SlideTitle(text: String, lemonGreen: Color) {
    Text(
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        color = lemonGreen,
        textAlign = TextAlign.Center,
        letterSpacing = (-0.5).sp,
        lineHeight = 38.sp
    )
}

@Composable
fun SlideSubtitle(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = Color.LightGray.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        lineHeight = 22.sp
    )
}

@Composable
fun PermissionRequestDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    val lemonGreen = Color(0xFFC5E384)
    val geekCyan = Color(0xFF00E5FF)
    val darkCard = Color(0xFF121212)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = lemonGreen,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "立即授权",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "稍后设置",
                    color = Color.LightGray.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = geekCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "权限配置指南",
                    color = lemonGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "您好！为了让「Eye Guard」在后台精准为您计时，并在20分钟到期时能够提醒您休息，我们需要您授予以下必要权限。我们承诺所有权限仅用于本地定时器触发，绝不收集隐私。",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start
                )

                // Divider/Line
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Notifications
                PermissionItemRow(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = geekCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    title = "通知权限",
                    desc = "用于在后台正常计时，并在到达20分钟时发出声音与横幅提醒。",
                    onClick = {
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                        } else {
                            Intent("android.settings.APP_NOTIFICATION_SETTINGS").apply {
                                putExtra("app_package", context.packageName)
                                putExtra("app_uid", context.applicationInfo.uid)
                            }
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                val detailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(detailsIntent)
                            } catch (ex: Exception) {}
                        }
                    }
                )

                // Autostart
                PermissionItemRow(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = geekCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    title = "自启动/后台弹出权限",
                    desc = "允许应用在后台倒计时结束时，能够自动通知，确保强提醒效果。",
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                )

                // Battery Optimizations
                PermissionItemRow(
                    icon = {
                        BatteryIcon(color = geekCyan, modifier = Modifier.size(22.dp))
                    },
                    title = "电池优化改为「无限制」",
                    desc = "防止安卓系统为了省电在后台恶意杀死定时器服务，确保提醒准时。",
                    onClick = {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                val detailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(detailsIntent)
                            } catch (ex: Exception) {}
                        }
                    }
                )
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = darkCard,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
fun PermissionItemRow(
    icon: @Composable () -> Unit,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1A1A))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "点击设置 ➔",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC5E384)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color.LightGray.copy(alpha = 0.75f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun BatteryIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidthPx = 1.8f.dp.toPx()
        
        // Battery outer border
        drawRoundRect(
            color = color,
            topLeft = Offset(strokeWidthPx, h * 0.25f),
            size = Size(w * 0.7f, h * 0.5f),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = strokeWidthPx)
        )
        // Battery tip on the right
        drawRect(
            color = color,
            topLeft = Offset(w * 0.72f + strokeWidthPx, h * 0.36f),
            size = Size(w * 0.1f, h * 0.28f)
        )
        // Battery inner fill (fully charged charging look)
        drawRect(
            color = color,
            topLeft = Offset(strokeWidthPx * 2f, h * 0.33f),
            size = Size(w * 0.45f, h * 0.34f)
        )
    }
}
