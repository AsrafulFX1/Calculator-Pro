package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorMode
import com.example.model.ColorThemeScheme
import com.example.model.ThemeMode
import com.example.ui.components.AgeCalculatorView
import com.example.ui.components.CalcButton
import com.example.ui.components.CalculatorDisplaySection
import com.example.ui.components.CalculatorSidebar
import com.example.ui.components.HistoryPanel
import com.example.ui.components.ScientificKeypad
import com.example.ui.components.SplitParenButton
import com.example.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val historyList by viewModel.historyItems.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val scheme: ColorThemeScheme = uiState.currentScheme(isSystemDark)
    val coroutineScope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Handle system back gesture
    if (drawerState.isOpen) {
        BackHandler {
            coroutineScope.launch {
                drawerState.close()
            }
        }
    } else if (uiState.isHistoryVisible) {
        BackHandler {
            viewModel.toggleHistory(false)
        }
    }

    val animatedBg by animateColorAsState(
        targetValue = scheme.background,
        animationSpec = tween(300),
        label = "bg_color"
    )

    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as? android.app.Activity)?.window
            if (window != null) {
                val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !scheme.isDark
                insetsController.isAppearanceLightNavigationBars = !scheme.isDark
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !uiState.isHistoryVisible,
        drawerContent = {
            CalculatorSidebar(
                currentMode = uiState.calculatorMode,
                angleMode = uiState.angleMode,
                decimalSeparator = uiState.decimalSeparator,
                numberGrouping = uiState.numberGrouping,
                isLargeButtonsEnabled = uiState.isLargeButtonsEnabled,
                isVibrationEnabled = uiState.isVibrationEnabled,
                isSoundEnabled = uiState.isSoundEnabled,
                scheme = scheme,
                onSetDefaultMode = { mode ->
                    viewModel.setDefaultCalculatorMode(mode)
                    viewModel.setCalculatorMode(mode)
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                onSetAngleMode = { mode -> viewModel.setAngleMode(mode) },
                onSetDecimalSeparator = { sep -> viewModel.setDecimalSeparator(sep) },
                onSetNumberGrouping = { grp -> viewModel.setNumberGrouping(grp) },
                onToggleLargeButtons = { enabled -> viewModel.toggleLargeButtons(enabled) },
                onToggleVibration = { enabled -> viewModel.toggleVibration(enabled) },
                onToggleSound = { enabled -> viewModel.toggleSound(enabled) },
                onClose = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(animatedBg)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Main Calculator Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 480.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                    // Top Bar: 3 Icons Matching Screenshot [ = ] [ v ] [ ... ]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: 3-line Menu Icon (opens Sidebar from left)
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    coroutineScope.launch {
                                        drawerState.open()
                                    }
                                }
                                .padding(8.dp)
                                .testTag("top_history_icon_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = scheme.topIconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Center: Down Arrow (Chevron) for History
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { viewModel.toggleHistory(true) }
                                .padding(8.dp)
                                .testTag("top_history_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "View History",
                                tint = scheme.topIconColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Right: Light (Purple) / Dark (Violet) Mode Switch
                        val isDarkMode = scheme.isDark
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDarkMode) Color(0xFF281A2E) else Color(0xFFE5D7FA))
                                .clickable { viewModel.toggleLightDarkMode(isSystemDark) }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .testTag("top_settings_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Sun Icon: Active when Light Mode (Purple Theme)
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(if (!isDarkMode) Color(0xFFA0157A) else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = "Light Purple Theme",
                                        tint = if (!isDarkMode) Color.White else Color(0xFF8E79A8),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                // Moon Icon: Active when Dark Mode (Violet Theme)
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkMode) Color(0xFFA0157A) else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NightlightRound,
                                        contentDescription = "Dark Violet Theme",
                                        tint = if (isDarkMode) Color.White else Color(0xFF8E79A8),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.calculatorMode == CalculatorMode.AGE) {
                        AgeCalculatorView(
                            scheme = scheme,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    } else {
                        // Display Area: Fills available space, anchored to bottom
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures { change, dragAmount ->
                                        if (dragAmount > 20f) {
                                            change.consume()
                                            viewModel.toggleHistory(true)
                                        }
                                    }
                                }
                                .padding(bottom = 12.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            CalculatorDisplaySection(
                                expression = uiState.expression,
                                displayExpression = uiState.displayExpression,
                                previousFormula = uiState.previousFormula,
                                result = uiState.result,
                                isEvaluated = uiState.isEvaluated,
                                decimalSeparator = uiState.decimalSeparator,
                                numberGrouping = uiState.numberGrouping,
                                scheme = scheme
                            )
                        }

                        // Dynamic Keypad Switcher: BASIC vs SCIENTIFIC
                        AnimatedContent(
                            targetState = uiState.calculatorMode,
                            transitionSpec = {
                                fadeIn(tween(180)) togetherWith fadeOut(tween(140))
                            },
                            label = "keypad_mode_switch"
                        ) { mode ->
                            if (mode == CalculatorMode.BASIC) {
                            // 5-Row Calculator Keypad Grid - Compact height with 8.dp row spacing
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // ROW 1: [ C ] [ ⌫ ] [ ( ) ] [ ÷ ]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                                ) {
                                    // C Button: Vibrant Magenta with White text
                                    CalcButton(
                                        text = "C",
                                        backgroundColor = scheme.accentButtonBackground,
                                        contentColor = scheme.accentButtonText,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        tag = "btn_clear",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onClear() }
                                    )

                                    // Backspace Button: Vibrant Magenta with White icon
                                    CalcButton(
                                        text = "",
                                        backgroundColor = scheme.accentButtonBackground,
                                        contentColor = scheme.accentButtonText,
                                        tag = "btn_backspace",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        icon = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                contentDescription = "Backspace",
                                                tint = scheme.accentButtonText,
                                                modifier = Modifier.size(23.dp)
                                            )
                                        },
                                        onClick = { viewModel.onBackspace() }
                                    )

                                    // Split Parentheses Button: Pastel Lavender with Deep text
                                    SplitParenButton(
                                        backgroundColor = scheme.splitParenBackground,
                                        contentColor = scheme.splitParenText,
                                        modifier = Modifier.weight(1f),
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        onOpenParenClick = { viewModel.onParenthesis("(") },
                                        onCloseParenClick = { viewModel.onParenthesis(")") }
                                    )

                                    // Divide Button: Pastel Lavender with Deep text
                                    CalcButton(
                                        text = "÷",
                                        backgroundColor = scheme.operatorButtonBackground,
                                        contentColor = scheme.operatorButtonText,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        tag = "btn_divide",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onOperator("÷") }
                                    )
                                }

                                // ROW 2: [ 7 ] [ 8 ] [ 9 ] [ × ]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                                ) {
                                    CalcButton(
                                        text = "7",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("7") }
                                    )
                                    CalcButton(
                                        text = "8",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("8") }
                                    )
                                    CalcButton(
                                        text = "9",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("9") }
                                    )
                                    CalcButton(
                                        text = "×",
                                        backgroundColor = scheme.operatorButtonBackground,
                                        contentColor = scheme.operatorButtonText,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        tag = "btn_multiply",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onOperator("×") }
                                    )
                                }

                                // ROW 3: [ 4 ] [ 5 ] [ 6 ] [ − ]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                                ) {
                                    CalcButton(
                                        text = "4",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("4") }
                                    )
                                    CalcButton(
                                        text = "5",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("5") }
                                    )
                                    CalcButton(
                                        text = "6",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("6") }
                                    )
                                    CalcButton(
                                        text = "−",
                                        backgroundColor = scheme.operatorButtonBackground,
                                        contentColor = scheme.operatorButtonText,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        tag = "btn_minus",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onOperator("−") }
                                    )
                                }

                                // ROW 4: [ 1 ] [ 2 ] [ 3 ] [ + ]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                                ) {
                                    CalcButton(
                                        text = "1",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("1") }
                                    )
                                    CalcButton(
                                        text = "2",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("2") }
                                    )
                                    CalcButton(
                                        text = "3",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("3") }
                                    )
                                    CalcButton(
                                        text = "+",
                                        backgroundColor = scheme.operatorButtonBackground,
                                        contentColor = scheme.operatorButtonText,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        tag = "btn_add",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onOperator("+") }
                                    )
                                }

                                // ROW 5: [ . ] [ 0 ] [ % ] [ = ]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                                ) {
                                    CalcButton(
                                        text = ".",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        tag = "btn_decimal",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDecimal() }
                                    )
                                    CalcButton(
                                        text = "0",
                                        backgroundColor = scheme.numberButtonBackground,
                                        contentColor = scheme.numberButtonText,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onDigit("0") }
                                    )
                                    CalcButton(
                                        text = "%",
                                        backgroundColor = scheme.operatorButtonBackground,
                                        contentColor = scheme.operatorButtonText,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        tag = "btn_percent",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onPercentage() }
                                    )
                                    CalcButton(
                                        text = "=",
                                        backgroundColor = scheme.accentButtonBackground,
                                        contentColor = scheme.accentButtonText,
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.Bold,
                                        tag = "btn_equals",
                                        isVibrationEnabled = uiState.isVibrationEnabled,
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onEquals() }
                                    )
                                }
                            }
                        } else {
                            // Scientific Keypad Grid
                            ScientificKeypad(
                                scheme = scheme,
                                angleMode = uiState.angleMode,
                                isSecondFunction = uiState.isSecondFunction,
                                isVibrationEnabled = uiState.isVibrationEnabled,
                                isSoundEnabled = uiState.isSoundEnabled,
                                onDigit = { viewModel.onDigit(it) },
                                onDecimal = { viewModel.onDecimal() },
                                onOperator = { viewModel.onOperator(it) },
                                onEquals = { viewModel.onEquals() },
                                onClear = { viewModel.onClear() },
                                onBackspace = { viewModel.onBackspace() },
                                onParenthesis = { viewModel.onParenthesis(it) },
                                onPercentage = { viewModel.onPercentage() },
                                onScientificFunction = { viewModel.onScientificFunction(it) },
                                onPower = { viewModel.onPower(it) },
                                onReciprocal = { viewModel.onReciprocal() },
                                onConstant = { viewModel.onConstant(it) },
                                onToggleAngleMode = { viewModel.toggleAngleMode() },
                                onToggleSecondFunction = { viewModel.toggleSecondFunction() },
                                isLargeButtonsEnabled = uiState.isLargeButtonsEnabled
                            )
                        }
                    }
                }
            }

            // ==========================================
            // CALCULATION HISTORY PANEL
            // ==========================================
            if (uiState.isHistoryVisible) {
                HistoryPanel(
                    historyList = historyList,
                    scheme = scheme,
                    onSelectItem = { viewModel.onSelectHistoryItem(it) },
                    onDeleteItem = { viewModel.deleteHistoryItem(it) },
                    onClearAll = { viewModel.clearAllHistory() },
                    onDismiss = { viewModel.toggleHistory(false) }
                )
            }
        }
    }
}
