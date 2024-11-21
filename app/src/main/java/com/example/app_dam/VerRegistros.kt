package com.example.app_dam

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class VerRegistros : AppCompatActivity() {

    private lateinit var listView: ListView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_registros)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        listView = findViewById(R.id.listView)


        db.collection("usuarios")
            .get()
            .addOnSuccessListener { result ->
                val usuarios = mutableListOf<String>()


                for (document in result) {
                    val usuario = document.toObject(Usuarios::class.java)

                    val usuarioInfo = "Nombre: ${usuario.nombre}\nApellido: ${usuario.apellido}\nSexo: ${usuario.sexo}\nEmail: ${usuario.email}\nFecha nac: ${usuario.fechaNacimiento}"
                    usuarios.add(usuarioInfo)
                }


                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, usuarios)
                listView.adapter = adapter
            }
            .addOnFailureListener { e ->

                e.printStackTrace()
            }


        val btnNuevoRegistro = findViewById<Button>(R.id.button6)
        btnNuevoRegistro.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        val btnVolverInicio = findViewById<Button>(R.id.button7)
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, Home1::class.java)
            startActivity(intent)
        }
    }
}
