package com.example.tiendavirtualapp_kotlin2.Modelos

class ModeloProductoOrden {

    var nombre : String = ""
    var precio : String = ""
    var cantidad : Int = 0
    var precioFinal : String = ""

    constructor()

    constructor(nombre: String, precio: String, cantidad: Int , precioFinal : String) {
        this.nombre = nombre
        this.precio = precio
        this.cantidad = cantidad
        this.precioFinal = precioFinal
    }

    override fun toString(): String {
        return "ModeloProductoOrden(nombre='$nombre', precio='$precio', cantidad=$cantidad)"
    }


}