package com.example.tiendavirtualapp_kotlin2.Modelos

class ModeloProducto {
    var id: String = ""
    var nombre: String = ""
    var descripcion: String = ""
    var categoria: String = ""
    var precio: String = ""
    var precioDescuento: String = ""
    var descuento: Int = 0
    var imagenUrl: String = ""
    var imagenes: ArrayList<String> = ArrayList()
    var timestamp: Long = 0
    var uid: String = ""
    var favorito: Boolean = false
    
    constructor()
    
    constructor(
        id: String,
        nombre: String,
        descripcion: String,
        categoria: String,
        precio: String,
        precioDescuento: String,
        descuento: Int,
        imagenUrl: String,
        imagenes: ArrayList<String>,
        timestamp: Long,
        uid: String,
        favorito: Boolean = false
    ) {
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
        this.categoria = categoria
        this.precio = precio
        this.precioDescuento = precioDescuento
        this.descuento = descuento
        this.imagenUrl = imagenUrl
        this.imagenes = imagenes
        this.timestamp = timestamp
        this.uid = uid
        this.favorito = favorito
    }
}