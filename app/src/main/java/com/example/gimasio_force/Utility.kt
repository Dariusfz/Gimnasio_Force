package com.example.gimasio_force


import android.animation.ObjectAnimator
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

object Utility {
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
}