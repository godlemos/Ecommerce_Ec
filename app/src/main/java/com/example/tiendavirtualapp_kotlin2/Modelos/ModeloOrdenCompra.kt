package com.example.tiendavirtualapp_kotlin2.Modelos

class ModeloOrdenCompra {


    var idOrden : String = ""
    var ordenadoPor : String = ""
    var tiempoOrden : String = ""
    var costo : String = ""
    var estadoOrden : String = ""

    constructor()
    constructor(
        idOrden: String,
        ordenadoPor: String,
        tiempoOrden: String,
        costo: String,
        estadoOrden: String
    ) {
        this.idOrden = idOrden
        this.ordenadoPor = ordenadoPor
        this.tiempoOrden = tiempoOrden
        this.costo = costo
        this.estadoOrden = estadoOrden
    }


}