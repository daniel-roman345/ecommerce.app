// MainActivity.kt — la primera pantalla que abre la app. Aquí "montamos" Compose.
package com.ecommerce.ecommerceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent   // puente entre Android y Compose
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ecommerce.ecommerceapp.ui.theme.EcommerceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent define la interfaz de la Activity USANDO Compose.
        setContent {
            EcommerceAppTheme {  // aplica los colores/estilos del proyecto
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()  // mostramos nuestra pantalla de login
                }
            }
        }
    }
}
