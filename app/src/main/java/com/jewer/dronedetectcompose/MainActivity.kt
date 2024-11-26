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
package com.jewer.dronedetectcompose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jewer.dronedetectcompose.home.HomeScreen
import com.jewer.dronedetectcompose.objectdetector.ObjectDetectorHelper
import com.jewer.dronedetectcompose.options.OptionsScreen
import com.jewer.dronedetectcompose.ui.theme.DroneDetectComposeTheme

//Entry point of our example app
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ObjectDetectionExampleApp()
        }
    }
}

// Root component of our app components tree
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ObjectDetectionExampleApp() {
    // Here we're first defining the object detector parameters states

    // We're defining them at the top of the components tree so that they
    // are accessible to all the app components, and any change of these
    // states will be reflected across the entire app, ensuring consistency

    // We're using "rememberSaveable" rather than "remember" so that the state
    // is preserved when the app change its orientation.

    // Since using a data class with "rememberSaveable" requires additional
    // configuration, we'll just define each option state individually as
    // "rememberSaveable" works with primitive values out of the box

    var threshold by rememberSaveable {
        mutableFloatStateOf(0.4f)
    }
    var maxResults by rememberSaveable {
        mutableIntStateOf(5)
    }
    var delegate by rememberSaveable {
        mutableIntStateOf(ObjectDetectorHelper.DELEGATE_CPU)
    }
    var mlModel by rememberSaveable {
        mutableIntStateOf(ObjectDetectorHelper.DRONE_MOBILENET_V2)
    }

    DroneDetectComposeTheme(darkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            // Here we handle navigation between Home screen and Options screen
            // Nothing too fancy, we only have two screens.

            // We define a controller first and provide it to NavHost
            // We will later use it to navigate between screens
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "Home",
            ) {
                // Here we associate a route name with each screen
                // We also provide a callback function to each screen
                // to navigate to the other one
                composable(route = "Home") {
                    HomeScreen(
                        onOptionsButtonClick = {
                            navController.navigate("Options")
                        },
                        threshold = threshold,
                        maxResults = maxResults,
                        delegate = delegate,
                        mlModel = mlModel,
                    )
                }
                composable(route = "Options") {
                    OptionsScreen(
                        onBackButtonClick = {
                            navController.popBackStack()
                        },
                        threshold = threshold, setThreshold = { threshold = it },
                        maxResults = maxResults, setMaxResults = { maxResults = it },
                        delegate = delegate, setDelegate = { delegate = it },
                        mlModel = mlModel, setMlModel = { mlModel = it },
                    )
                }
            }
        }
    }
}