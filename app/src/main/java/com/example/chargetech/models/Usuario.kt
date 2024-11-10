package com.example.chargetech.models

data class Usuario (
    val id_usuario: Int,
    val id_genero: Int,
    val nome: String,
    val email: String,
    val data_nascimento: String,
    val senha: String,
)