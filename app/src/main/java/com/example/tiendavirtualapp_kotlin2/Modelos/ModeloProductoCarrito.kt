package com.example.tiendavirtualapp_kotlin2.Modelos

class ModeloProductoCarrito {

    var idProducto : String = ""
    var nombre : String = ""
    var precio : String = ""
    var precioFinal : String = ""
    var precioDescuento : String = ""
    var cantidad : Int = 0

    constructor()
    constructor(
        idProducto: String,
        nombre: String,
        precio: String,
        precioFinal: String,
        precioDescuento: String,
        cantidad: Int
    ) {
        this.idProducto = idProducto
        this.nombre = nombre
        this.precio = precio
        this.precioFinal = precioFinal
        this.precioDescuento = precioDescuento
        this.cantidad = cantidad
    }


}