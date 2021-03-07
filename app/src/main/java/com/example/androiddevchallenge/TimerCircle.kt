/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun TimerCircles(
    seconds: Int,
    currentState: MutableTransitionState<TimerState>,
    modifier: Modifier,
) {
    val indicatorColor = MaterialTheme.colors.primary
    val circleColor = contentColorFor(MaterialTheme.colors.background)
    val transition = updateTransition(currentState)
    val offsetByState: (TimerState) -> Int = { state ->
        if (state == TimerState.INITIAL) {
            0
        } else {
            360
        }
    }
    val hoursOffset by transition.animateInt(
        transitionSpec = {
            tween(
                delayMillis = 50,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }
    ) { state -> offsetByState(state) }
    val minutesOffset by transition.animateInt(
        transitionSpec = {
            tween(
                delayMillis = 250,
                durationMillis = 450,
                easing = LinearEasing
            )
        }
    ) { state -> offsetByState(state) }
    val secondsOffset by transition.animateInt(
        transitionSpec = {
            tween(
                delayMillis = 450,
                durationMillis = 450,
                easing = CubicBezierEasing(0f, 0.75f, 0.35f, 0.85f)
            )
        }
    ) { state -> offsetByState(state) }

    Canvas(modifier = modifier) {
        val canvasRadius = size.minDimension / 2f

        val innerCircleRadius = canvasRadius - 72.dp.toPx()
        val middleCircleRadius = canvasRadius - 48.dp.toPx()
        val outerCircleRadius = canvasRadius - 24.dp.toPx()

        timerCircle(outerCircleRadius, 2.dp.toPx(), 0.8f, hoursOffset, circleColor)
        timerCircle(middleCircleRadius, 1.dp.toPx(), 0.6f, minutesOffset, circleColor)
        timerCircle(innerCircleRadius, 0.7.dp.toPx(), 0.5f, secondsOffset, circleColor)

        drawTimerIndicator(indicatorColor, 2.dp.toPx(), innerCircleRadius, seconds)
        drawTimerIndicator(indicatorColor, 3.dp.toPx(), middleCircleRadius, seconds / 60)
        drawTimerIndicator(indicatorColor, 6.dp.toPx(), outerCircleRadius, seconds / 3600)
    }
}

private fun DrawScope.drawTimerIndicator(color: Color, circleRadius: Float, pointRadius: Float, time: Int) {
    drawCircle(
        color = color, radius = circleRadius,
        center = offset(
            center = center,
            radius = pointRadius,
            angle = time * 6 - 90
        )
    )
}

private fun DrawScope.timerCircle(
    pointRadius: Float,
    strokeWidth: Float,
    alpha: Float,
    angleOffset: Int,
    color: Color
) {
    drawPoints(
        points = createPoints(center, radius = pointRadius, ((0..angleOffset step 6)).toList()),
        pointMode = PointMode.Points,
        color = color,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
        alpha = alpha
    )
}

fun createPoints(center: Offset, radius: Float, angles: List<Int>): List<Offset> {
    return angles.map { angle ->
        offset(center, radius, angle - 90)
    }
}
