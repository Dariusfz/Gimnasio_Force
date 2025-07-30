package com.example.gimasio_force


import android.animation.ObjectAnimator
import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.example.gimasio_force.MainActivity.Companion.totalesDeporteSeleccionado
import com.example.gimasio_force.MainActivity.Companion.totalesBicicleta
import com.example.gimasio_force.MainActivity.Companion.carrerasTotales
import com.example.gimasio_force.LoginActivity.Companion.useremail
import com.example.gimasio_force.MainActivity.Companion.activatedGPS
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.concurrent.TimeUnit

object Utility {
    private var totalesVerificados: Int =0
//formateo de dia, mes y anio en segundos
    fun getFormattedTotalTime(secs: Long): String {
        var seconds: Long = secs
        var total: String =""

        //1 dia = 86400s
        //1 mes (30 dias) = 2592000s
        //365 dias = 31536000s

        var years: Int = 0
        while (seconds >=  31536000) { years++; seconds-=31536000; }

        var months: Int = 0
        while (seconds >=  2592000) { months++; seconds-=2592000; }

        var days: Int = 0
        while (seconds >=  86400) { days++; seconds-=86400; }

        if (years > 0) total += "${years}y "
        if (months > 0) total += "${months}m "
        if (days > 0) total += "${days}d "

        total += getFormattedStopWatch(seconds*1000)

        return total
    }

    //funcion para formatear las horas, minutos y segundos
fun getSecFromWatch (watch: String): Int{

    var secs = 0
    var w: String = watch
    if (w.length == 5) w= "00:" + w

    // 00:00:00
    secs += w.subSequence(0,2).toString().toInt() * 3600 //hora
    secs += w.subSequence(3,5).toString().toInt() * 60 //minuto
    secs += w.subSequence(6,8).toString().toInt() //segundo

    return secs
}

/* FUNCIONES DE ANIMACION Y CAMBIOS DE ATRIBUTOS */
fun setHeightLinearLayout(ly: LinearLayout, value: Int){
    val params: LinearLayout.LayoutParams = ly.layoutParams as LinearLayout.LayoutParams //casteo de linearlayout
    params.height = value
    ly.layoutParams = params
}
    //funcion de animacion
fun animateViewofInt(v: View, attr: String, value: Int, time: Long){
    ObjectAnimator.ofInt(v, attr, value).apply{
        duration = time
        start()
    }
}
fun animateViewofFloat(v: View, attr: String, value: Float, time: Long){
    ObjectAnimator.ofFloat(v, attr, value).apply{
        duration = time
        start()
    }
}
    //funcion para calcular las horas, minutos y segundos en texto
    fun getFormattedStopWatch(ms: Long): String{
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }
//funcion de redondeo para no mostrar tantos decimales
    fun roundNumber(data: String, decimals: Int) : String{
        var d : String = data
        var p= d.indexOf(".", 0)

        if (p != null){
            var limit: Int = p+decimals +1
            if (d.length <= p+decimals+1) limit = d.length //-1
            d = d.subSequence(0, limit).toString()
        }

        return d
    }

    // FUNCIONES DE BORRADO DE CARRERA
    fun borrarCarreraYDatosVinculados(idRun: String, sport: String, ly: LinearLayout, cr: Carreras){

        if (activatedGPS) borrarUbicaciones(idRun, useremail)
        //si habia fotos, borramos todas las fotos
        actualizarTotales(cr)
        verificarRecords(cr, sport, useremail)
        borrarCarrera(idRun, sport, ly)
    }
//borramos la coleccion de ubicaciones en la base de datos de firebase
    private fun borrarUbicaciones(idCarrera: String, usuario: String){
        var idUbicaciones = idCarrera.subSequence(usuario.length, idCarrera.length).toString()

        var dbUbicaciones = FirebaseFirestore.getInstance()
        dbUbicaciones.collection("ubicacion/$usuario/$idUbicaciones")
            .get()
            .addOnSuccessListener { documents->
                for (docUbicacion in documents){
                    var dbLoc = FirebaseFirestore.getInstance()
                    dbLoc.collection("ubicacion/$usuario/$idUbicaciones").document(docUbicacion.id)//apuntamos a los documentos para borrarlos
                        .delete()
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)

            }
    }

    private fun borrarCarrera(idRun: String, sport: String, ly: LinearLayout){
        var dbRun = FirebaseFirestore.getInstance()
        dbRun.collection("carreras$sport").document(idRun)
            .delete()
            .addOnSuccessListener {
                Snackbar.make(ly, "Registro Borrado", Snackbar.LENGTH_LONG).setAction("OK"){//barra para mostrar el mensaje similar a toask
                    ly.setBackgroundColor(Color.CYAN)//fondo del mensaje
                }.show()
            }
            .addOnFailureListener {//mostrar mensaje en caso de fallo
                Snackbar.make(ly, "Error al borrar el registro", Snackbar.LENGTH_LONG).setAction("OK"){
                    ly.setBackgroundColor(Color.CYAN)
                }.show()
            }
    }

    private fun actualizarTotales(cr: Carreras){
        totalesDeporteSeleccionado.totalDistancia = totalesDeporteSeleccionado.totalDistancia!! - cr.distancia!!
        totalesDeporteSeleccionado.totalCarreras = totalesDeporteSeleccionado.totalCarreras!! - 1
        totalesDeporteSeleccionado.totalTiempo = totalesDeporteSeleccionado.totalTiempo!! - getSecFromWatch(cr.duracion!!)

    }

    private fun verificarRecords(cr: Carreras, deporte: String, usuario: String){
        totalesVerificados = 0

        verificarDistanciaRecord(cr, deporte, usuario)
        verificarVelPromedioRecord(cr, deporte, usuario)
        verificarMaxVelocidadRecord(cr, deporte, usuario)

    }

    private fun verificarDistanciaRecord(cr: Carreras, deporte: String, usuario: String){
        if (cr.distancia!! == totalesDeporteSeleccionado.recordDistancia){//verificar si la carrera actual es el record
            var dbRecords = FirebaseFirestore.getInstance()
            dbRecords.collection("carreras$deporte")
                .orderBy("distancia", Query.Direction.DESCENDING)
                .whereEqualTo("usuario", usuario)
                .get()
                .addOnSuccessListener { documents ->

                    if (documents.size() == 1)  totalesDeporteSeleccionado.recordDistancia = 0.0
                    else  totalesDeporteSeleccionado.recordDistancia = documents.documents[1].get("distancia").toString().toDouble()

                    var collection = "totales$deporte"
                    var dbUpdateTotals = FirebaseFirestore.getInstance()
                    dbUpdateTotals.collection(collection).document(usuario)
                        .update("recordDistancia", totalesDeporteSeleccionado.recordDistancia)

                    totalesVerificados++
                    if (totalesVerificados == 3) refrescarTotalesDeporte(deporte)

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
                }
        }
    }
    private fun verificarVelPromedioRecord(cr: Carreras, sport: String, user: String){
        if (cr.avgSpeed!! == totalesDeporteSeleccionado.recordVelocidadPromedio){
            var dbRecords = FirebaseFirestore.getInstance()
            dbRecords.collection("carreras$sport")
                .orderBy("avgSpeed", Query.Direction.DESCENDING)
                .whereEqualTo("usuario", user)
                .get()
                .addOnSuccessListener { documents ->

                    if (documents.size() == 1)  totalesDeporteSeleccionado.recordVelocidadPromedio = 0.0
                    else  totalesDeporteSeleccionado.recordVelocidadPromedio = documents.documents[1].get("avgSpeed").toString().toDouble()

                    var collection = "totales$sport"
                    var dbUpdateTotals = FirebaseFirestore.getInstance()
                    dbUpdateTotals.collection(collection).document(user)
                        .update("recordVelocidadPromedio", totalesDeporteSeleccionado.recordVelocidadPromedio)

                    totalesVerificados++
                    if (totalesVerificados == 3) refrescarTotalesDeporte(sport)

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
                }
        }
    }
    private fun verificarMaxVelocidadRecord(cr: Carreras, sport: String, user: String){
        if (cr.maxSpeed!! == totalesDeporteSeleccionado.recordVelocidad){
            var dbRecords = FirebaseFirestore.getInstance()
            dbRecords.collection("carreras$sport")
                .orderBy("maxSpeed", Query.Direction.DESCENDING)
                .whereEqualTo("usuario", user)
                .get()
                .addOnSuccessListener { documents ->

                    if (documents.size() == 1)  totalesDeporteSeleccionado.recordVelocidad = 0.0
                    else  totalesDeporteSeleccionado.recordVelocidad = documents.documents[1].get("maxSpeed").toString().toDouble()

                    var collection = "totales$sport"
                    var dbUpdateTotals = FirebaseFirestore.getInstance()
                    dbUpdateTotals.collection(collection).document(user)
                        .update("recordVelocidad", totalesDeporteSeleccionado.recordVelocidad)

                    totalesVerificados++
                    if (totalesVerificados == 3) refrescarTotalesDeporte(sport)

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
                }
        }
    }
    private fun refrescarTotalesDeporte(sport: String){
        when (sport){
            "Bicicleta"-> totalesBicicleta = totalesDeporteSeleccionado
            "Carrera"-> carrerasTotales = totalesDeporteSeleccionado
        }

    }
}