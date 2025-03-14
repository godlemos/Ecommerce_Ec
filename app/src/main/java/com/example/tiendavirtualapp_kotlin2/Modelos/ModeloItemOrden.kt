package com.example.tiendavirtualapp_kotlin2.Modelos

class ModeloItemOrden {
    var idProducto: String = ""
    var nombre: String = ""
    var costo: String = ""
    var cantidad: String = ""
    var precioFinal: String = ""
    var uidVendedor: String = ""
    
    constructor()
    
    constructor(
        idProducto: String,
        nombre: String,
        costo: String,
        cantidad: String,
        precioFinal: String,
        uidVendedor: String
    ) {
        this.idProducto = idProducto
        this.nombre = nombre
        this.costo = costo
        this.cantidad = cantidad
        this.precioFinal = precioFinal
        this.uidVendedor = uidVendedor
    }
} 