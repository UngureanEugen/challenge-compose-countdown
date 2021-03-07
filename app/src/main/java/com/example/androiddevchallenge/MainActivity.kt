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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme(darkTheme = true) {
                MyApp()
            }
        }
    }
}

const val startTime = 3690

// Start building your app here!
@Composable
fun MyApp(countDownTimerViewModel: CountDownTimerViewModel = viewModel()) {
    val seconds by animateIntAsState(
        targetValue = countDownTimerViewModel.remainingTime.collectAsState(initial = startTime).value,
        animationSpec = tween(450, easing = LinearOutSlowInEasing)
    )
    val currentState = remember {
        MutableTransitionState(TimerState.INITIAL)
            .apply { targetState = TimerState.END }
    }

    Surface(color = MaterialTheme.colors.background) {
        TimerContent(seconds, currentState) {
            countDownTimerViewModel.submit(StartAction(startTime))
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}

@Composable
fun TimerContent(seconds: Int, currentState: MutableTransitionState<TimerState>, onButtonClick: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TimerCircles(seconds, currentState, Modifier.fillMaxSize())
            TimerLabels(Modifier.align(Alignment.Center), seconds)
        }
        Button(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .size(88.dp)
                .shadow(4.dp, shape = CircleShape),
            onClick = onButtonClick
        ) {
            Text(
                text = if (seconds == startTime) "START" else "RESET",
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@Composable
fun TimerLabels(modifier: Modifier, time: Int) {
    val seconds = time % 60
    val minutes = (time / 60) % 60
    val hours = (time / 3600) % 60
    Row(modifier = modifier) {
        Text(style = MaterialTheme.typography.h3, text = "%1\$02d:".format(hours))
        Text(style = MaterialTheme.typography.h3, text = "%1\$02d:".format(minutes))
        Text(style = MaterialTheme.typography.h3, text = "%1\$02d".format(seconds))
    }
}

internal fun offset(center: Offset, radius: Float, angle: Int) = Offset(
    (center.x + radius * cos(angle * Math.PI / 180)).toFloat(),
    (center.y + radius * sin(angle * Math.PI / 180)).toFloat()
)

enum class TimerState { INITIAL, END }
