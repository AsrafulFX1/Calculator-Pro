package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.util.ButtonFeedbackHelper

@Composable
fun SplitParenButton(
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    isVibrationEnabled: Boolean = true,
    isSoundEnabled: Boolean = true,
    onOpenParenClick: () -> Unit,
    onCloseParenClick: () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current

    fun triggerFeedback() {
        if (isVibrationEnabled) {
            ButtonFeedbackHelper.playLightVibration(context, view)
        }
        if (isSoundEnabled) {
            ButtonFeedbackHelper.playClickSound(context, view)
        }
    }

    var isLeftPressed by remember { mutableStateOf(false) }
    val leftScale by animateFloatAsState(
        targetValue = if (isLeftPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "left_scale"
    )

    var isRightPressed by remember { mutableStateOf(false) }
    val rightScale by animateFloatAsState(
        targetValue = if (isRightPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "right_scale"
    )

    val leftShape = RoundedCornerShape(
        topStart = 24.dp,
        bottomStart = 24.dp,
        topEnd = 8.dp,
        bottomEnd = 8.dp
    )
    val rightShape = RoundedCornerShape(
        topStart = 8.dp,
        bottomStart = 8.dp,
        topEnd = 24.dp,
        bottomEnd = 24.dp
    )

    Row(
        modifier = modifier.aspectRatio(1.08f),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Left Paren '('
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .scale(leftScale)
                .clip(leftShape)
                .background(backgroundColor)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isLeftPressed = true
                            tryAwaitRelease()
                            isLeftPressed = false
                        },
                        onTap = {
                            triggerFeedback()
                            onOpenParenClick()
                        }
                    )
                }
                .testTag("btn_paren_open"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "(",
                color = contentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }

        // Right Paren ')'
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .scale(rightScale)
                .clip(rightShape)
                .background(backgroundColor)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isRightPressed = true
                            tryAwaitRelease()
                            isRightPressed = false
                        },
                        onTap = {
                            triggerFeedback()
                            onCloseParenClick()
                        }
                    )
                }
                .testTag("btn_paren_close"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ")",
                color = contentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
