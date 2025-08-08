package com.example.gimasio_force

data class Carreras(
    var fecha: String ?= null,
    var inicioTiempo: String ?= null,
    var usuario: String ?= null,
    var duracion: String ?= null,

    var intervalMode: Boolean ?= null,
    var intervalDuration: Int ?= null,
    var tiempoCorriendo: String ?= null,
    var tiempoCaminando: String ?= null,

    var challengeDuration: String ?= null,
    var challengeDistance: Double ?= null,

    var distancia: Double ?= null,
    var maxSpeed: Double ?= null,
    var avgSpeed: Double ?= null,

    var maxAltitude: Double ?= null,
    var maxLatitude: Double ?= null,
    var maxLongitude: Double ?= null,
    var minAltitude: Double ?= null,
    var minLatitude: Double ?= null,
    var minLongitude: Double ?= null,

    var centerLatitude: Double ?= null,
    var centerLongitude: Double ?= null,

    var deporte: String ?= null,
    var activatedGPS: Boolean ?= null,

    var medalDistance: String ?= null,
    var medalAvgSpeed: String ?= null,
    var medalMaxSpeed: String ?= null,

    var countFotos: Int ?= null,
    var ultimaFoto: String ?= null


)
