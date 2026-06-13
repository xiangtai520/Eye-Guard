package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPrefs = getSharedPreferences("eye_guard_prefs", MODE_PRIVATE)
        setContent {
            MyApplicationTheme {
                var onboardingCompleted by remember {
                    mutableStateOf(sharedPrefs.getBoolean("onboarding_completed", false))
                }
                if (!onboardingCompleted) {
                    OnboardingScreen(onFinished = {
                        sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
                        onboardingCompleted = true
                    })
                } else {
                    MainComposeApp()
                }
            }
        }
    }
}

@Composable
fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val appState by viewModel.appState.collectAsState()
    val timeLeftSeconds by viewModel.timeLeftSeconds.collectAsState()
    val isDemoMode by viewModel.isDemoMode.collectAsState()

    // Android 13+ runtime permission prompt helper
    var hasCheckedPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (!hasCheckedPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            hasCheckedPermission = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main protection screen UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        EyeIcon(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Eye Guard",
                            style = TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                    Text(
                        text = "您的视力健康管家",
                        fontSize = 14.sp,
                        color = MutedSlate,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Circular countdown region - locked to perfectly square with aspect ratio
                val maxSeconds = if (appState == AppState.RESTING) viewModel.getRestMaxSeconds() else viewModel.getWorkMaxSeconds()
                CountdownCircle(
                    currentValue = timeLeftSeconds,
                    maxValue = maxSeconds,
                    appState = appState,
                    modifier = Modifier
                        .size(240.dp)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Large action trigger button and explanation card
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { viewModel.toggleProtection() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appState == AppState.IDLE) VividEmerald else Color(0xFFE53935),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .testTag("protection_toggle_button"),
                        shape = RoundedCornerShape(30.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (appState == AppState.IDLE) {
                                EyeIcon(color = Color.Black, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "开启护眼防护",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "解除眼部保护",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Instruction Card (Rule Explanation)
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = ForestCard),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp)
                        ) {
                            Text(
                                text = "20-20-20 护眼黄金法则",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = VividEmerald,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            RuleRow(number = "01", title = "工作计时", desc = "每持续看屏幕用眼 20 分钟")
                            RuleRow(number = "02", title = "远眺放松", desc = "往 20 英尺外 (约 6 米) 处远眺")
                            RuleRow(number = "03", title = "深度休整", desc = "让双眼聚焦远方舒缓精细 20 秒")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtle developer demo Mode switch
                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ForestCard)
                        .clickable { viewModel.toggleDemoMode() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("demo_mode_switch"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = if (isDemoMode) VividEmerald else MutedSlate,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isDemoMode) "已进入 20秒 演示体验环境" else "点此进入 20秒 快捷演示模式",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDemoMode) VividEmerald else MutedSlate
                    )
                }
            }

            // High-Penetration Rest Overlay Screen (RESTING Overlay cover)
            AnimatedVisibility(
                visible = appState == AppState.RESTING,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(100f)
            ) {
                // Safeguard Screen Wake Lock during active 20 seconds rest
                KeepScreenOn()
                
                // Override back key press under rest screen to protect focus
                BackHandler(enabled = true) {
                    // Do nothing - require user to deliberate click skip button
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Calming Forest Green background scenery
                    SoothingScenicBackground()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Title on Rest Screen
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 40.dp)
                        ) {
                            Text(
                                text = "。眼部深呼吸时间 。",
                                fontSize = 16.sp,
                                color = LightGlowMint,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "请将视线彻底离开屏幕",
                                fontSize = 24.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "望向远处，眨眼舒张，放松 20 秒",
                                fontSize = 14.sp,
                                color = MutedSlate,
                                modifier = Modifier.padding(top = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Countdown circle on rest screen - also aspect ratio locked and centered
                        CountdownCircle(
                            currentValue = timeLeftSeconds,
                            maxValue = if (isDemoMode) MainViewModel.DEMO_REST_SECONDS else MainViewModel.DEFAULT_REST_SECONDS,
                            appState = appState,
                            modifier = Modifier
                                .size(220.dp)
                                .aspectRatio(1f)
                        )

                        // Emergency Skip Button
                        OutlinedButton(
                            onClick = { viewModel.skipRest() },
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier
                                .padding(bottom = 32.dp)
                                .testTag("skip_button")
                        ) {
                            Text(
                                text = "跳过当前休眠 ⏱",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RuleRow(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(VividEmerald.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VividEmerald
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = desc,
                fontSize = 13.sp,
                color = MutedSlate,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun EyeIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // Eye contour path
        val eyePath = Path().apply {
            moveTo(w * 0.1f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.15f, w * 0.9f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.85f, w * 0.1f, h * 0.5f)
            close()
        }
        drawPath(
            path = eyePath,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Pupil
        drawCircle(
            color = color,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.5f)
        )
        
        // Iris reflection highlight
        drawCircle(
            color = Color.White,
            radius = w * 0.05f,
            center = Offset(w * 0.54f, h * 0.46f)
        )
    }
}

@Composable
fun CountdownCircle(
    currentValue: Int,
    maxValue: Int,
    appState: AppState,
    modifier: Modifier = Modifier,
    themeMode: String = "forest"
) {
    val progress = if (maxValue > 0) currentValue.toFloat() / maxValue.toFloat() else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ProgressCircle"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeWidth = 14.dp.toPx()
            
            // Calculate absolute perfect dimensions to prevent outer deformation
            val minDim = size.minDimension
            val radius = (minDim - strokeWidth) / 2f
            val centerOffset = Offset(size.width / 2f, size.height / 2f)

            // Draw centered outer & inner moon glow behind rest circle if active
            if (appState == AppState.RESTING) {
                val outerGlowRadius = minDim * 1.6f
                val innerGlowRadius = minDim * 0.9f
                
                val glowColor1 = if (themeMode == "ocean") Color(0xFF00B0FF).copy(alpha = 0.08f)
                                 else Color(0xFF00E575).copy(alpha = 0.08f)
                val glowColor2 = if (themeMode == "ocean") Color(0xFF80D8FF).copy(alpha = 0.12f)
                                 else Color(0xFF66FFB2).copy(alpha = 0.12f)
                
                drawCircle(
                    color = glowColor1,
                    radius = outerGlowRadius,
                    center = centerOffset
                )
                drawCircle(
                    color = glowColor2,
                    radius = innerGlowRadius,
                    center = centerOffset
                )
            }

            // Draw background circle
            drawCircle(
                color = if (appState == AppState.RESTING) Color.White.copy(alpha = 0.12f) 
                        else if (appState == AppState.IDLE) SoftSageDark 
                        else VividEmerald.copy(alpha = 0.15f),
                radius = radius,
                center = centerOffset,
                style = Stroke(width = strokeWidth)
            )
            
            // Calculate coordinates for bounding box to ensure perfect circular arc
            val arcSize = minDim - strokeWidth
            val topLeftX = (size.width - arcSize) / 2f
            val topLeftY = (size.height - arcSize) / 2f

            // Draw moving progress arc with perfect bounding box
            drawArc(
                color = if (appState == AppState.RESTING) LightGlowMint else VividEmerald,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(topLeftX, topLeftY),
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner timer text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val minutes = currentValue / 60
            val seconds = currentValue % 60
            
            val timeText = String.format("%02d:%02d", minutes, seconds)
            
            Text(
                text = timeText,
                style = TextStyle(
                    fontSize = if (appState == AppState.RESTING) 54.sp else 46.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                ),
                modifier = Modifier.testTag("countdown_text")
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            val subtitle = when (appState) {
                AppState.WORKING -> "距离下一次休息"
                AppState.RESTING -> "正在保护双眼..."
                else -> "保护未开启"
            }
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (appState == AppState.RESTING) LightGlowMint else MutedSlate
                )
            )
        }
    }
}

@Composable
fun SoothingScenicBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Background: Calm dark obsidian forest gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    ObsidianBlack,
                    Color(0xFF0F261B)
                )
            )
        )

        // Far mountain paths
        val pathFar = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.65f)
            cubicTo(
                width * 0.3f, height * 0.58f,
                width * 0.7f, height * 0.72f,
                width, height * 0.62f
            )
            lineTo(width, height)
            close()
        }
        drawPath(pathFar, color = Color(0xFF132B20))

        // Mid mountain paths
        val pathMid = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.75f)
            cubicTo(
                width * 0.4f, height * 0.68f,
                width * 0.6f, height * 0.82f,
                width, height * 0.74f
            )
            lineTo(width, height)
            close()
        }
        drawPath(pathMid, color = ForestCard)

        // Near gentle paths
        val pathNear = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.85f)
            cubicTo(
                width * 0.35f, height * 0.82f,
                width * 0.75f, height * 0.88f,
                width, height * 0.82f
            )
            lineTo(width, height)
            close()
        }
        drawPath(pathNear, color = Color(0xFF080F0C))

        // Abstract pine-trees layout
        drawMinimalTree(this, width * 0.18f, height * 0.79f, 45f)
        drawMinimalTree(this, width * 0.32f, height * 0.81f, 60f)
        drawMinimalTree(this, width * 0.78f, height * 0.78f, 52f)
    }
}

private fun drawMinimalTree(drawScope: DrawScope, x: Float, y: Float, size: Float) {
    val treePath = Path().apply {
        moveTo(x, y - size)
        lineTo(x - size * 0.35f, y)
        lineTo(x + size * 0.35f, y)
        close()
    }
    drawScope.drawPath(treePath, color = Color(0xFF163E2B))
}
