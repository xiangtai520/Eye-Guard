package com.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.Typography

// Custom premium Material 3 Dynamic themes supporting immersive eyesafe palettes
@Composable
fun GuardAppTheme(
    themeMode: String, // "system", "geek", "forest", "ocean"
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when (themeMode) {
        "system" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dynamicDarkColorScheme(context)
            } else {
                darkColorScheme(
                    primary = Color(0xFFC5E384),
                    secondary = Color(0xFF00E5FF),
                    background = Color(0xFF000000),
                    surface = Color(0xFF121212),
                    surfaceVariant = Color(0xFF1D1F24),
                    onPrimary = Color.Black,
                    onSecondary = Color.Black,
                    onBackground = Color.White,
                    onSurface = Color(0xFFE2E2E2),
                    secondaryContainer = Color(0xFF2D2F34)
                )
            }
        }
        "geek" -> {
            darkColorScheme(
                primary = Color(0xFFC5E384),    // Geek Green
                secondary = Color(0xFF00E5FF),  // Geek Cyan
                background = Color(0xFF000000), // Pure Midnight Obsidian
                surface = Color(0xFF101214),    // Premium Geek Card Outer
                surfaceVariant = Color(0xFF1A1C1E),
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                onBackground = Color.White,
                onSurface = Color(0xFFE2E2E2),
                secondaryContainer = Color(0xFF2A2E32)
            )
        }
        "forest" -> {
            darkColorScheme(
                primary = Color(0xFF00E575),    // Vivid Emerald
                secondary = Color(0xFF66FFB2),  // Light Glow Mint
                background = Color(0xFF0C0F0D), // Forest Charcoal
                surface = Color(0xFF151C18),    // Forest Cozy Card Outer
                surfaceVariant = Color(0xFF1D2621),
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                onBackground = Color(0xFFF0F5F2),
                onSurface = Color(0xFFDCDFD1),
                secondaryContainer = Color(0xFF28362E)
            )
        }
        "ocean" -> {
            darkColorScheme(
                primary = Color(0xFF00B0FF),    // Neon Ocean Ice-Blue
                secondary = Color(0xFF80D8FF),  // Soft Shore Turquoise-Blue
                background = Color(0xFF050B14), // Unfathomable Deep Blue Space
                surface = Color(0xFF0D1726),    // Sea Reef card container
                surfaceVariant = Color(0xFF152238),
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                onBackground = Color(0xFFE1F5FE),
                onSurface = Color(0xFFCFE3EE),
                secondaryContainer = Color(0xFF1E2E44)
            )
        }
        else -> {
            darkColorScheme(
                primary = Color(0xFF00E575),
                secondary = Color(0xFF66FFB2),
                background = Color(0xFF0C0F0D),
                surface = Color(0xFF151C18),
                surfaceVariant = Color(0xFF1D2621),
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                onBackground = Color(0xFFF0F5F2),
                onSurface = Color(0xFFDCDFD1),
                secondaryContainer = Color(0xFF28362E)
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Master shell container combining bottom bar navigation, page state routing
@Composable
fun MainComposeApp(viewModel: MainViewModel = viewModel()) {
    val currentThemeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()
    var currentTab by rememberSaveable { mutableStateOf("home") }

    GuardAppTheme(themeMode = currentThemeMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == "home",
                        onClick = { currentTab = "home" },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == "home") Icons.Default.Visibility else Icons.Outlined.Visibility,
                                contentDescription = "护眼"
                            )
                        },
                        label = {
                            Text(
                                "护眼",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("tab_home")
                    )
                    NavigationBarItem(
                        selected = currentTab == "settings",
                        onClick = { currentTab = "settings" },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == "settings") Icons.Default.Settings else Icons.Outlined.Settings,
                                contentDescription = "设置"
                            )
                        },
                        label = {
                            Text(
                                "设置",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("tab_settings")
                    )
                    NavigationBarItem(
                        selected = currentTab == "about",
                        onClick = { currentTab = "about" },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == "about") Icons.Default.Info else Icons.Outlined.Info,
                                contentDescription = "关于"
                            )
                        },
                        label = {
                            Text(
                                "关于",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("tab_about")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentTab) {
                    "home" -> EyeGuardTab(viewModel)
                    "settings" -> SettingsTab(viewModel)
                    "about" -> AboutTab()
                }
            }
        }
    }
}

// ------------------------------------------
// Tab 1: Precise countdown timer and eye-care triggers
// ------------------------------------------
@Composable
fun EyeGuardTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val appState by viewModel.appState.collectAsStateWithLifecycle()
    val timeLeftSeconds by viewModel.timeLeftSeconds.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isDemoMode.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()

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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header information group
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
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                Text(
                    text = "您的视力健康管家",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Perfect circular countdown clock
            val maxSeconds = if (appState == AppState.RESTING) viewModel.getRestMaxSeconds() else viewModel.getWorkMaxSeconds()
            CountdownCircle(
                currentValue = timeLeftSeconds,
                maxValue = maxSeconds,
                appState = appState,
                modifier = Modifier
                    .size(240.dp)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action triggers & instructions card
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.toggleProtection() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (appState == AppState.IDLE) MaterialTheme.colorScheme.primary else Color(0xFFE53935),
                        contentColor = if (appState == AppState.IDLE) Color.Black else Color.White
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

                Spacer(modifier = Modifier.height(24.dp))

                // Standardized 20-20-20 Instruction Card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {
                        Text(
                            text = "20-20-20 护眼黄金法则",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        RuleItemRow(number = "01", title = "工作计时", desc = "每持续看屏幕用眼 20 分钟")
                        RuleItemRow(number = "02", title = "远眺放松", desc = "往 20 英尺外 (约 6 米) 处远眺")
                        RuleItemRow(number = "03", title = "深度休整", desc = "让双眼聚焦远方舒缓精细 20 秒")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtly toggle demo switch
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { viewModel.toggleDemoMode() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("demo_mode_switch"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = if (isDemoMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isDemoMode) "已进入 20秒 演示体验环境" else "点此进入 20秒 快捷演示模式",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDemoMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        // Full-screen overlay rest mode panel
        AnimatedVisibility(
            visible = appState == AppState.RESTING,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
        ) {
            KeepScreenOn()
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background elements render based on the theme
                SceneryBackground(themeMode = currentThemeMode)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        Text(
                            text = "。眼部深呼吸时间 。",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary,
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
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    CountdownCircle(
                        currentValue = timeLeftSeconds,
                        maxValue = if (isDemoMode) MainViewModel.DEMO_REST_SECONDS else viewModel.getRestMaxSeconds(),
                        appState = appState,
                        modifier = Modifier
                            .size(220.dp)
                            .aspectRatio(1f),
                        themeMode = currentThemeMode
                    )

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

@Composable
fun RuleItemRow(number: String, title: String, desc: String) {
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
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// Immersive drawing backend scenery during active rest timers
@Composable
fun SceneryBackground(themeMode: String) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (themeMode == "ocean") {
            // Immersive Deep Ocean moonlight scenery drawing
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030811),
                        Color(0xFF0C192E)
                    )
                )
            )
            // Draw ocean wave paths
            val waveFar = Path().apply {
                moveTo(0f, height)
                lineTo(0f, height * 0.70f)
                cubicTo(
                    width * 0.25f, height * 0.65f,
                    width * 0.75f, height * 0.75f,
                    width, height * 0.68f
                )
                lineTo(width, height)
                close()
            }
            drawPath(waveFar, color = Color(0xFF09172B))

            val waveMid = Path().apply {
                moveTo(0f, height)
                lineTo(0f, height * 0.78f)
                cubicTo(
                    width * 0.3f, height * 0.82f,
                    width * 0.7f, height * 0.74f,
                    width, height * 0.80f
                )
                lineTo(width, height)
                close()
            }
            drawPath(waveMid, color = Color(0xFF0D213F))
        } else {
            // Cozy Green Obsidian Pine Forest drawing (geek/forest/system standard)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C100E),
                        Color(0xFF0F261B)
                    )
                )
            )

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
            drawPath(pathMid, color = Color(0xFF151C18))

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

            // Abstracts pine-trees silhouettes
            drawCustomTree(this, width * 0.18f, height * 0.79f, 45f)
            drawCustomTree(this, width * 0.32f, height * 0.81f, 60f)
            drawCustomTree(this, width * 0.78f, height * 0.78f, 52f)
        }
    }
}

private fun drawCustomTree(drawScope: DrawScope, x: Float, y: Float, size: Float) {
    val treePath = Path().apply {
        moveTo(x, y - size)
        lineTo(x - size * 0.35f, y)
        lineTo(x + size * 0.35f, y)
        close()
    }
    drawScope.drawPath(treePath, color = Color(0xFF163E2B))
}

// ------------------------------------------
// Tab 2: Premium Preference grouped setting sheets
// ------------------------------------------
@Composable
fun SettingsTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentThemeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()
    val workDuration by viewModel.workDuration.collectAsStateWithLifecycle()
    val breakDuration by viewModel.breakDuration.collectAsStateWithLifecycle()
    val customSoundUri by viewModel.customSoundUri.collectAsStateWithLifecycle()

    val isSystemDynamicColor = currentThemeMode == "system"

    // Launcher for system file browser to select user custom audio files
    val soundPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setCustomSoundUri(uri.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "偏好设置",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        // CARD Group 1: Modern dynamic styling preference
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "跟随系统动态色彩",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "开启以启用 Android Material You 动态背板配色 (需 Android 12+ 支持)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            lineHeight = 16.sp
                        )
                    }
                    Switch(
                        checked = isSystemDynamicColor,
                        onCheckedChange = { active ->
                            if (active) {
                                viewModel.setThemeMode("system")
                            } else {
                                viewModel.setThemeMode("forest") // switch fallback
                            }
                        },
                        modifier = Modifier.testTag("system_dynamic_color_switch")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "界面主题选择",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Geek dark
                    ThreeColorThemeBadge(
                        label = "极客暗黑",
                        primaryColor = Color(0xFFC5E384),    // Geek Green
                        secondaryColor = Color(0xFF00E5FF),  // Geek Cyan
                        tertiaryColor = Color(0xFF004D40),   // Dark Green
                        isSelected = currentThemeMode == "geek",
                        onClick = {
                            viewModel.setThemeMode("geek")
                        }
                    )
                    
                    // 2. Forest Green
                    ThreeColorThemeBadge(
                        label = "森林护眼",
                        primaryColor = Color(0xFF00E575),    // Vivid Emerald
                        secondaryColor = Color(0xFF66FFB2),  // Light Glow Mint
                        tertiaryColor = Color(0xFF1B5E20),   // Moss Green
                        isSelected = currentThemeMode == "forest",
                        onClick = {
                            viewModel.setThemeMode("forest")
                        }
                    )
                    
                    // 3. Deep ocean blue
                    ThreeColorThemeBadge(
                        label = "深海沉静",
                        primaryColor = Color(0xFF00B0FF),    // Neon Ocean Ice-Blue
                        secondaryColor = Color(0xFF80D8FF),  // Soft Shore Turquoise-Blue
                        tertiaryColor = Color(0xFF1A237E),   // Deep Navy Indigo
                        isSelected = currentThemeMode == "ocean",
                        onClick = {
                            viewModel.setThemeMode("ocean")
                        }
                    )
                }
            }
        }

        // CARD Group 2: Timer Duration Tuning controls
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Work timer duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "专注工作时长",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$workDuration 分钟",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = workDuration.toFloat(),
                    onValueChange = { value ->
                        val intVal = value.toInt()
                        if (intVal != workDuration) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.setWorkDuration(intVal)
                        }
                    },
                    valueRange = 10f..60f,
                    steps = 9, // Ticks at 10, 15, 20, 25 ... 60
                    modifier = Modifier.testTag("work_duration_slider")
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(16.dp))

                // Rest timer duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "远眺放松时长",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$breakDuration 秒",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = breakDuration.toFloat(),
                    onValueChange = { value ->
                        val intVal = value.toInt()
                        if (intVal != breakDuration) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.setBreakDuration(intVal)
                        }
                    },
                    valueRange = 10f..120f,
                    steps = 21, // Multiples of 5: (120 - 10)/5 - 1 = 21 ticks
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.testTag("break_duration_slider")
                )
            }
        }

        // CARD Group 3: Customize notification sound Uri configurations
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { soundPickerLauncher.launch(arrayOf("audio/*")) }
                ) {
                    Text(
                        text = "放松提示音管理",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (customSoundUri.isEmpty()) "当前选择：系统内置 Chimes 铃音" else "当前：${customSoundUri.substringAfterLast("/")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                        .clickable { playPreviewSound(context, customSoundUri) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "试听",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Perform safe audio simulation playback
private fun playPreviewSound(context: Context, soundUriString: String) {
    try {
        if (soundUriString.isNotEmpty()) {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(soundUriString))
                prepare()
                start()
                setOnCompletionListener { release() }
            }
        } else {
            // Standard notification melody play
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            ringtone?.play()
        }
    } catch (e: Exception) {
        // Safe robust hardware tone generator fallback
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
        } catch (ex: Exception) {}
    }
}

@Composable
fun ThreeColorThemeBadge(
    label: String,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            }
        ),
        modifier = Modifier
            .width(88.dp)
            .height(108.dp)
            .clickable { onClick() }
            .testTag("theme_badge_${label}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Split 360 degrees into 3 equal pieces of 120 degrees
                    // Slice 1: from -90f (pointing top) to 30f
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 120f,
                        useCenter = true
                    )
                    // Slice 2: from 30f to 150f
                    drawArc(
                        color = secondaryColor,
                        startAngle = 30f,
                        sweepAngle = 120f,
                        useCenter = true
                    )
                    // Slice 3: from 150f to 270f
                    drawArc(
                        color = tertiaryColor,
                        startAngle = 150f,
                        sweepAngle = 120f,
                        useCenter = true
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                            shadowElevation = 2.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(12.dp)
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
        }
    }
}

// ------------------------------------------
// Tab 3: Minimalist aesthetic representation
// ------------------------------------------
@Composable
fun AboutTab() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Large Top App Bar - Under M3 Specifications (Large but styled)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "关于",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.weight(0.25f))

        // Center visual app identity brand card (Leave generous whitespace strictly)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant large round branding eye badge container
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 6.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    EyeIcon(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Eye Guard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Version 1.0.0 (100)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.weight(0.75f))
    }
}
