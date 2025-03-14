package com.example.tiendavirtualapp_kotlin2.Modelos

class ModeloImgSlider {

    var id : String = ""
    var imagenUrl : String = ""

    constructor()

    constructor(id: String, imagenUrl: String) {
        this.id = id
        this.imagenUrl = imagenUrl
    }


}