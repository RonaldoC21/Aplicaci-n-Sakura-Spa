package com.example.app_dam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Home1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home1)

        // Configuración de los márgenes con el sistema de barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Encuentra el botón "Ingresar" en el layout
        val ingresarButton: Button = findViewById(R.id.ingresar_button)

        // Establece un OnClickListener para el botón
        ingresarButton.setOnClickListener {
            // Crea un Intent para lanzar MainActivity (actividad con el formulario)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Inicia la actividad
        }
    }
}
