package com.example.phoneapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phoneapp.routing.MyPhonesRouter
import com.example.phoneapp.routing.Screen
import com.example.phoneapp.screens.PhonesScreen
import com.example.phoneapp.screens.SavePhoneScreen
import com.example.phoneapp.ui.theme.MyPhonesTheme
import com.example.phoneapp.ui.theme.MyPhonesThemeSettings
import com.example.phoneapp.viewmodel.MainViewModel
import com.example.phoneapp.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPhonesTheme(darkTheme = MyPhonesThemeSettings.isDarkThemeEnabled) {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(LocalContext.current.applicationContext as Application)
                )
                MainActivityScreen(viewModel)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainActivityScreen(viewModel: MainViewModel) {
    Surface {
        when (MyPhonesRouter.currentScreen) {
            is Screen.Phones -> PhonesScreen(viewModel)
            is Screen.SavePhone -> SavePhoneScreen(viewModel)
        }
    }
}