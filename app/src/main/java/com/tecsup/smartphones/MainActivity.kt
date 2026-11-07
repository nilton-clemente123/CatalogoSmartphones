package com.tecsup.smartphones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tecsup.smartphones.ui.lista.ListaScreen
import com.tecsup.smartphones.ui.navigation.AppNav
import com.tecsup.smartphones.ui.theme.CatalogoSmartphonesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppContainer.inicializar(applicationContext)

        setContent {
            CatalogoSmartphonesTheme {
                AppNav()
            }
        }
    }
}
