/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jewer.dronedetectcompose.options

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jewer.dronedetectcompose.composables.MediaPipeBanner
import com.jewer.dronedetectcompose.R
import com.jewer.dronedetectcompose.objectdetector.ObjectDetectorHelper
import com.jewer.dronedetectcompose.ui.theme.Turquoise
import kotlin.math.max
import kotlin.math.min

// Options Screen is where object detector parameters are changed by the user,
// that's why for each "option" parameter we have a corresponding "setOption" parameter
// so that they can be used to set the state of the object detector parameters

// We also have "onBackButtonClick" function to navigate back to Home screen with

@Composable
fun OptionsScreen(
    threshold: Float, setThreshold: (Float) -> Unit,
    maxResults: Int, setMaxResults: (Int) -> Unit,
    delegate: Int, setDelegate: (Int) -> Unit,
    mlModel: Int, setMlModel: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    // We have two dropdowns in this page for selecting "delegate" and "mlModel" values
    // These dropdowns need to have states indicating if they are expanded or not
    // So we declare a boolean state for each one here
    var delegateDropdownExpanded by remember { mutableStateOf(false) }
    var mlModelDropdownExpanded by remember { mutableStateOf(false) }

    // Now we define the UI.
    Column {
        // Similar to Home screen, we have the MediaPipe banner,
        // but showing a back button instead of an options button
        MediaPipeBanner(
            onBackButtonClick = onBackButtonClick,
        )

        // Here we're drawing what looks like a TopAppBar

        // We're using Material3 version of Jetpack Compose, which at the time of building
        // this example app, has a lot of missing/experimental APIs. TopAppBar is one of
        // those experimental APIs, and it may change in the future, so it's better to just
        // draw what we want manually for now.
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "參數設定",
                fontSize = 25.sp,
            )
        }
        HorizontalDivider()

        // Next is a series of rows, each describing the UI for
        // controlling an object detector option state

        // First, we have the threshold controls, which has two buttons to
        // decrease and increase threshold value within [0, 0.8] range
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "臨界值",
                modifier = Modifier
                    .weight(1f),
            )
            // Minus button
            IconButton(
                onClick = {
                    // Transforming threshold value to an integer before updating it to
                    // avoid accumulating floating point errors
                    val newThreshold = ((threshold * 10).toInt() - 1).toDouble() / 10

                    setThreshold(
                        max(
                            newThreshold.toFloat(),
                            0.0f,
                        )
                    )
                },
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_minus),
                    contentDescription = null,
                    tint = Turquoise
                )
            }
            Box(
                modifier = Modifier.width(50.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$threshold".substring(IntRange(0, 2)),
                )
            }
            IconButton(
                onClick = {
                    val newThreshold = ((threshold * 10).toInt() + 1).toDouble() / 10
                    setThreshold(
                        min(
                            newThreshold.toFloat(),
                            0.8f,
                        )
                    )
                },
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    tint = Turquoise
                )
            }
        }

        // Secondly, similar to threshold controls, we have the maxResults controls,
        // which has two buttons to decrease and increase maxResults value within [1, 5] range
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "最大結果數",
                modifier = Modifier
                    .weight(1f),
            )
            IconButton(
                onClick = {
                    setMaxResults(
                        max(
                            maxResults - 1,
                            1,
                        )
                    )
                },
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_minus),
                    contentDescription = null,
                    tint = Turquoise
                )
            }
            Box(
                modifier = Modifier.width(50.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$maxResults",
                )
            }
            IconButton(
                onClick = {
                    setMaxResults(
                        min(
                            maxResults + 1,
                            10,
                        )
                    )
                },
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    tint = Turquoise
                )
            }
        }

        // Thirdly, we have the delegate controls which is a dropdown to select one of the
        // two options: DELEGATE_CPU and DELEGATE_GPU
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "硬體",
                modifier = Modifier
                    .weight(1f),
            )
            Text(
                text = when (delegate) {
                    ObjectDetectorHelper.DELEGATE_CPU -> "CPU"
                    else -> "GPU"
                },
            )
            IconButton(
                onClick = {
                    delegateDropdownExpanded = true
                },
            ) {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = Turquoise
                )
                DropdownMenu(
                    expanded = delegateDropdownExpanded,
                    onDismissRequest = { delegateDropdownExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "CPU") },
                        onClick = {
                            setDelegate(ObjectDetectorHelper.DELEGATE_CPU)
                            delegateDropdownExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = "GPU") },
                        onClick = {
                            setDelegate(ObjectDetectorHelper.DELEGATE_GPU)
                            delegateDropdownExpanded = false
                        },
                    )
                }
            }
        }
        // Lastly, similar to delegate controls, we have the mlModel controls which is a dropdown to
        // select one of the two options: MODEL_EFFICIENTDETV0 and MODEL_EFFICIENTDETV2
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "模型",
                modifier = Modifier
                    .weight(1f),
            )
            Text(
                text = when (mlModel) {
                    ObjectDetectorHelper.DRONE_MOBILENET_V2 -> "Drone_MobileNet_V2"
                    ObjectDetectorHelper.DRONE_MOBILENET_V2_FP16 -> "Drone_MobileNet_V2_FP16"
                    else -> "LifeStuff_MobileNet_V1"
                },
            )
            IconButton(
                onClick = {
                    mlModelDropdownExpanded = true
                },
            ) {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = Turquoise
                )
                DropdownMenu(
                    expanded = mlModelDropdownExpanded,
                    onDismissRequest = { mlModelDropdownExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Drone_MobileNet_V2") },
                        onClick = {
                            setMlModel(ObjectDetectorHelper.DRONE_MOBILENET_V2)
                            mlModelDropdownExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Drone_MobileNet_V2_FP16") },
                        onClick = {
                            setMlModel(ObjectDetectorHelper.DRONE_MOBILENET_V2_FP16)
                            mlModelDropdownExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = "LifeStuff_MobileNet_V1") },
                        onClick = {
                            setMlModel(ObjectDetectorHelper.LIFESTUFF_MOBILENET_V1)
                            mlModelDropdownExpanded = false
                        },
                    )
                }
            }
        }
    }
}