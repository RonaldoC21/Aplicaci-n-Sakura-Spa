package com.example.app_dam

import android.Manifest
import android.app.DatePickerDialog
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.provider.MediaStore
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream
import android.util.Base64

class MainActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var spinner: Spinner
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var imageButton: ImageButton
    private lateinit var btnVolverInicio: Button
    private var selectedImageUri: Uri? = null

    private val REQUEST_CODE_GALLERY = 100
    private val REQUEST_CODE_PERMISSION = 101


    private val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        editTextDate = findViewById(R.id.editTextDate)
        spinner = findViewById(R.id.spinner)
        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        saveButton = findViewById(R.id.btnGuardar)
        imageButton = findViewById(R.id.imageButton)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)


        val sexoOptions = arrayOf("Seleccione...", "Hombre", "Mujer", "Otro")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sexoOptions)
        spinner.adapter = spinnerAdapter


        editTextDate.setOnClickListener {
            showDatePickerDialog()
        }


        saveButton.setOnClickListener {
            if (isValidForm()) {
                saveDataToFirestore()
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }


        imageButton.setOnClickListener {
            checkPermissionAndOpenGallery()
        }


        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, Home1::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun isValidForm(): Boolean {
        val nombre = etNombre.text.toString().trim()
        if (TextUtils.isEmpty(nombre)) {
            etNombre.error = "El campo Nombres es obligatorio"
            etNombre.requestFocus()
            return false
        }

        val apellido = etApellido.text.toString().trim()
        if (TextUtils.isEmpty(apellido)) {
            etApellido.error = "El campo Apellidos es obligatorio"
            etApellido.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(editTextDate.text)) {
            editTextDate.error = "La fecha de nacimiento es obligatoria"
            editTextDate.requestFocus()
            return false
        }

        val email = emailEditText.text.toString().trim()
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Correo electrónico inválido"
            emailEditText.requestFocus()
            return false
        }

        if (spinner.selectedItemPosition == 0) {
            Toast.makeText(this, "Por favor, seleccione un sexo", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            editTextDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
        }, year, month, day)

        datePickerDialog.show()
    }


    private fun encodeImageToBase64(uri: Uri): String? {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    private fun saveDataToFirestore() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val fechaNacimiento = editTextDate.text.toString().trim()
        val sexo = spinner.selectedItem.toString()
        val email = emailEditText.text.toString().trim()

        val usuario = Usuarios(nombre, apellido, fechaNacimiento, sexo, email)

        if (selectedImageUri != null) {
            val encodedImage = encodeImageToBase64(selectedImageUri!!)
            val userWithImage = usuario.copy(imagenBase64 = encodedImage)

            db.collection("usuarios")
                .add(userWithImage)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario guardado en Firestore", Toast.LENGTH_SHORT).show()
                    goToDatosGuardados(encodedImage) // Pasamos la imagen Base64 al ir a la siguiente actividad
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            db.collection("usuarios")
                .add(usuario)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario guardado en Firestore", Toast.LENGTH_SHORT).show()
                    goToDatosGuardados(null) // Si no hay imagen, pasamos null
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun goToDatosGuardados(imagenBase64: String?) {
        val intent = Intent(this, DatosGuardados::class.java)
        intent.putExtra("imagenBase64", imagenBase64) // Pasar imagen en Base64 a DatosGuardados
        startActivity(intent)
        finish()
    }


    private fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
                Toast.makeText(this, "Permiso necesario para seleccionar imágenes.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_PERMISSION)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            selectedImageUri?.let { uri ->
                imageButton.setImageURI(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a la galería.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


data class Usuarios(
    val nombre: String = "",
    val apellido: String = "",
    val fechaNacimiento: String = "",
    val sexo: String = "",
    val email: String = "",
    val imagenBase64: String? = null 
)
