package com.example.tiendavirtualapp_kotlin2.Modelos

class Categoria {
    var id: String = ""
    var nombre: String = ""
    var uid: String = ""
    var timestamp: Long = 0
    
    constructor()
    
    constructor(id: String, nombre: String, uid: String, timestamp: Long) {
        this.id = id
        this.nombre = nombre
        this.uid = uid
        this.timestamp = timestamp
    }
} 