package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AngleMode
import com.example.model.CalculatorMode
import com.example.model.ColorThemeScheme

@Composable
fun CalculatorSidebar(
    currentMode: CalculatorMode,
    angleMode: AngleMode,
    decimalSeparator: String,
    numberGrouping: String,
    isLargeButtonsEnabled: Boolean,
    isVibrationEnabled: Boolean,
    isSoundEnabled: Boolean,
    scheme: ColorThemeScheme,
    onSetDefaultMode: (CalculatorMode) -> Unit,
    onSetAngleMode: (AngleMode) -> Unit,
    onSetDecimalSeparator: (String) -> Unit,
    onSetNumberGrouping: (String) -> Unit,
    onToggleLargeButtons: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerBg = if (scheme.isDark) Color(0xFF1B1822) else Color(0xFFFBF8FD)
    val cardBg = if (scheme.isDark) Color(0xFF24202C) else Color(0xFFF0EAF5)
    val cardStroke = if (scheme.isDark) Color(0xFF352F40) else Color(0xFFE2D6EB)

    ModalDrawerSheet(
        drawerContainerColor = drawerBg,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        modifier = modifier
            .widthIn(max = 330.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header: Logo, Title, Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_logo),
                        contentDescription = "Calculator Logo",
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            text = "Calculator",
                            color = scheme.expressionTextColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "Preferences",
                            color = scheme.topIconColor.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("sidebar_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Sidebar",
                        tint = scheme.topIconColor
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Section: CALCULATOR MODE
                SidebarSectionTitle("CALCULATOR MODE", Icons.Default.Calculate, scheme)
                SidebarCard(cardBg, cardStroke) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(
                            text = "Active Calculator",
                            color = scheme.expressionTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SidebarSegmentChip(
                                label = "Basic",
                                isSelected = currentMode == CalculatorMode.BASIC,
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetDefaultMode(CalculatorMode.BASIC) }
                            )
                            SidebarSegmentChip(
                                label = "Age",
                                isSelected = currentMode == CalculatorMode.AGE,
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetDefaultMode(CalculatorMode.AGE) }
                            )
                            SidebarSegmentChip(
                                label = "Scientific",
                                isSelected = currentMode == CalculatorMode.SCIENTIFIC,
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetDefaultMode(CalculatorMode.SCIENTIFIC) }
                            )
                        }
                    }
                }

                // 2. Section: ANGLE UNIT
                SidebarSectionTitle("ANGLE UNIT", Icons.Default.Tune, scheme)
                SidebarCard(cardBg, cardStroke) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(
                            text = "Trigonometric Unit",
                            color = scheme.expressionTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SidebarSegmentChip(
                                label = "DEG",
                                isSelected = angleMode == AngleMode.DEG,
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetAngleMode(AngleMode.DEG) }
                            )
                            SidebarSegmentChip(
                                label = "RAD",
                                isSelected = angleMode == AngleMode.RAD,
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetAngleMode(AngleMode.RAD) }
                            )
                        }
                    }
                }

                // 3. Section: FORMATTING
                SidebarSectionTitle("FORMATTING", Icons.Default.Tune, scheme)

                // Decimal Separator
                SidebarCard(cardBg, cardStroke) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(
                            text = "Decimal Separator",
                            color = scheme.expressionTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SidebarSegmentChip(
                                label = "Dot ( . )",
                                isSelected = decimalSeparator == ".",
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetDecimalSeparator(".") }
                            )
                            SidebarSegmentChip(
                                label = "Comma ( , )",
                                isSelected = decimalSeparator == ",",
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetDecimalSeparator(",") }
                            )
                        }
                    }
                }

                // Number Grouping
                SidebarCard(cardBg, cardStroke) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(
                            text = "Number Grouping",
                            color = scheme.expressionTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SidebarSegmentChip(
                                label = "Off",
                                isSelected = numberGrouping == "Off",
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetNumberGrouping("Off") }
                            )
                            SidebarSegmentChip(
                                label = "1,000",
                                isSelected = numberGrouping == "Comma",
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetNumberGrouping("Comma") }
                            )
                            SidebarSegmentChip(
                                label = "1.000",
                                isSelected = numberGrouping == "Dot",
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetNumberGrouping("Dot") }
                            )
                            SidebarSegmentChip(
                                label = "1 000",
                                isSelected = numberGrouping == "Space",
                                scheme = scheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetNumberGrouping("Space") }
                            )
                        }
                    }
                }

                // 4. Section: CONTROLS & FEEDBACK
                SidebarSectionTitle("CONTROLS & FEEDBACK", Icons.Default.Vibration, scheme)

                // Haptic Feedback
                SidebarCard(cardBg, cardStroke) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Haptic Feedback",
                            color = scheme.expressionTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isVibrationEnabled,
                            onCheckedChange = onToggleVibration,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = scheme.accentButtonBackground,
                                uncheckedTrackColor = scheme.topIconColor.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("sidebar_switch_vibration")
                        )
                    }
                }

                // Button Sound
                SidebarCard(cardBg, cardStroke) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Button Sound",
                            color = scheme.expressionTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isSoundEnabled,
                            onCheckedChange = onToggleSound,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = scheme.accentButtonBackground,
                                uncheckedTrackColor = scheme.topIconColor.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("sidebar_switch_sound")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SidebarSectionTitle(title: String, icon: ImageVector, scheme: ColorThemeScheme) {
    Row(
        modifier = Modifier.padding(start = 2.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = scheme.topIconColor.copy(alpha = 0.7f),
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = title,
            color = scheme.topIconColor.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.6.sp
        )
    }
}

@Composable
private fun SidebarCard(cardBg: Color, cardStroke: Color, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, cardStroke, RoundedCornerShape(14.dp))
    ) {
        content()
    }
}

@Composable
private fun SidebarSegmentChip(
    label: String,
    isSelected: Boolean,
    scheme: ColorThemeScheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (isSelected) scheme.accentButtonBackground else Color.Transparent
    val textColor = if (isSelected) Color.White else scheme.expressionTextColor

    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            maxLines = 1
        )
    }
}
