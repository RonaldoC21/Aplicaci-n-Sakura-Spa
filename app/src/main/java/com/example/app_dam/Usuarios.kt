package com.example.app_dam

data class Usuario(
    var nombre: String = "",
    var apellido: String = "",
    var fechaNacimiento: String = "",
    var sexo: String = "",
    var email: String = "",
    val imagenBase64: String? = null
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "", "")
}
