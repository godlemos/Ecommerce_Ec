package com.dinocode.tiendavirtualapp_kotlin.Modelos

class ModeloCalificacion {

    var idResenia : String = ""
    var resenia : String = ""
    var uidUsuario : String = ""
    var calificacion : Int = 0

    constructor()

    constructor(idResenia: String, resenia: String, uidUsuario: String, calificacion: Int) {
        this.idResenia = idResenia
        this.resenia = resenia
        this.uidUsuario = uidUsuario
        this.calificacion = calificacion
    }


}