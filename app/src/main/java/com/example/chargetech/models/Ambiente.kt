package com.example.chargetech.models

data class Ambiente (
    val id_ambiente: Int,
    val id_usuario: Int,
    val nome: String,
    val descricao: String,
    val dispositivos: List<Dispositivo> = emptyList()
)