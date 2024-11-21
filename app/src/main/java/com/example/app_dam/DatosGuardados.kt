package com.example.app_dam

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DatosGuardados : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_guardados)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val imagenBase64 = intent.getStringExtra("imagenBase64")


        if (imagenBase64 != null) {
            Log.d("Base64", imagenBase64)


            val decodedImage = decodeBase64ToBitmap(imagenBase64)


            val imageView: ImageView = findViewById(R.id.imageView)
            imageView.setImageBitmap(decodedImage)
        } else {
            Log.d("Base64", "No se recibió una imagen Base64")
        }


        val btnVolverFormulario = findViewById<Button>(R.id.button4)
        btnVolverFormulario.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        val btnVerTodos = findViewById<Button>(R.id.button5)
        btnVerTodos.setOnClickListener {
            val intent = Intent(this, VerRegistros::class.java)
            startActivity(intent)
        }
    }


    private fun decodeBase64ToBitmap(base64: String): Bitmap {
        val decodedString = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}
