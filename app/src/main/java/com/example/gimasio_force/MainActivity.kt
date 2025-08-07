package com.example.gimasio_force

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gimasio_force.Constantes.INTERVAL_LOCATION
import com.example.gimasio_force.Constantes.LIMITE_DISTANCIA_ACEPTADA_CICLISTA
import com.example.gimasio_force.Constantes.LIMITE_DISTANCIA_ACEPTADA_CORREDOR
import com.example.gimasio_force.Constantes.llave_UsuarioApp
import com.example.gimasio_force.Constantes.llave_autofinalizar
import com.example.gimasio_force.Constantes.llave_deporteSeleccionado
import com.example.gimasio_force.Constantes.llave_desafiodistancia
import com.example.gimasio_force.Constantes.llave_duraciondesafioHH
import com.example.gimasio_force.Constantes.llave_duraciondesafioMM
import com.example.gimasio_force.Constantes.llave_duraciondesafioSS
import com.example.gimasio_force.Constantes.llave_maxCircularSeekBar
import com.example.gimasio_force.Constantes.llave_mododesafio
import com.example.gimasio_force.Constantes.llave_mododesafiodistancia
import com.example.gimasio_force.Constantes.llave_mododesafioduracion
import com.example.gimasio_force.Constantes.llave_mododuracionintervalo
import com.example.gimasio_force.Constantes.llave_modointervalo
import com.example.gimasio_force.Constantes.llave_notificacionesdesafio
import com.example.gimasio_force.Constantes.llave_progressCircularSeekBar
import com.example.gimasio_force.Constantes.llave_proveedor
import com.example.gimasio_force.Constantes.llave_tiempocaminando
import com.example.gimasio_force.Constantes.llave_tiempocorriendo
import com.example.gimasio_force.LoginActivity.Companion.providerSession
import com.example.gimasio_force.LoginActivity.Companion.useremail
import com.example.gimasio_force.Utility.animateViewofFloat
import com.example.gimasio_force.Utility.animateViewofInt
import com.example.gimasio_force.Utility.getFormattedStopWatch
import com.example.gimasio_force.Utility.getFormattedTotalTime
import com.example.gimasio_force.Utility.getSecFromWatch
import com.example.gimasio_force.Utility.roundNumber
import com.example.gimasio_force.Utility.setHeightLinearLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import me.tankery.lib.circularseekbar.CircularSeekBar



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener{

    companion object{
        lateinit var mainContext: Context

        lateinit var totalesDeporteSeleccionado: Totales
        lateinit var totalesBicileta: Totales
        lateinit var carrerasTotales: Totales


        val REQUIRED_PERMISSIONS_GPS =
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,//permisos de ubicacion
                Manifest.permission.ACCESS_FINE_LOCATION) //ubicacion con precision

        var countFotos: Int =0
        var ultimaFoto: String = ""
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    private var mHandler: Handler? = null
    private var mInterval = 1000
    private var timeInSeconds = 0L
    private var rounds: Int = 1 //rondas
    private var startButtonClicked = false //saber si se pulso el boton de iniciar carrera

    private var widthScreenPixels: Int = 0
    private var heightScreenPixels: Int = 0
    private var widthAnimations: Int = 0

    private lateinit var drawer: DrawerLayout

    private lateinit var csbChallengeDistance: CircularSeekBar
    private lateinit var csbCurrentDistance: CircularSeekBar
    private lateinit var csbRecordDistance: CircularSeekBar

    private lateinit var csbCurrentAvgSpeed: CircularSeekBar
    private lateinit var csbRecordAvgSpeed: CircularSeekBar

    private lateinit var csbCurrentSpeed: CircularSeekBar
    private lateinit var csbCurrentMaxSpeed: CircularSeekBar
    private lateinit var csbRecordSpeed: CircularSeekBar

    private lateinit var tvDistanceRecord: TextView
    private lateinit var tvAvgSpeedRecord: TextView
    private lateinit var tvMaxSpeedRecord: TextView

    private lateinit var tvChrono: TextView
    private lateinit var fbCamara: FloatingActionButton


    private lateinit var swIntervalMode: Switch
    private lateinit var swChallenges: Switch


    private lateinit var npChallengeDistance: NumberPicker
    private lateinit var npChallengeDurationHH: NumberPicker
    private lateinit var npChallengeDurationMM: NumberPicker
    private lateinit var npChallengeDurationSS: NumberPicker

    private lateinit var cbNotify: CheckBox
    private lateinit var cbAutoFinish: CheckBox
    //private lateinit var sbNotifyVolume : SeekBar

    private var challengeDistance: Float = 0f
    private var challengeDuration: Int = 0


    private lateinit var npDurationInterval: NumberPicker
    private lateinit var tvRunningTime: TextView
    private lateinit var tvWalkingTime: TextView
    private lateinit var csbRunWalk: CircularSeekBar

    private var ROUND_INTERVAL = 300 //controlar los intervalos por defecto 5 minutos
    private var TIME_RUNNING: Int = 0

    private lateinit var lyPopupRun: LinearLayout

    private var activatedGPS: Boolean = true
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_ID = 42
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private var flagSavedLocation = false

    private lateinit var map: GoogleMap
    private var mapCentered = true
    private lateinit var listPoints: Iterable<LatLng>

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var init_lt: Double = 0.0
    private var init_ln: Double = 0.0

    private var distance: Double = 0.0
    private var maxSpeed: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var speed: Double = 0.0

    private var minAltitude: Double? = null
    private var maxAltitude: Double? = null
    private var minLatitude: Double? = null
    private var maxLatitude: Double? = null
    private var minLongitude: Double? = null
    private var maxLongitude: Double? = null

    private var LIMITE_DISTANCIA_ACEPTADA: Double = 0.0
    private lateinit var deporteSeleccionado : String

    private lateinit var nivelBicicleta: Nivel
    private lateinit var nivelCorriendo: Nivel
    private lateinit var nivelDeporteSeleccionado: Nivel

    private lateinit var listaNivelBicicleta: ArrayList<Nivel>
    private lateinit var listaNivelCarrera: ArrayList<Nivel>

    private var sportsLoaded: Int = 0

    private var dateRun: String = ""
    private var startTimeRun: String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val tvRounds: TextView = findViewById(R.id.tvRounds) as TextView
        tvRounds.text = getString(R.string.rounds)

        mainContext=this
        // 1. Inicializar componentes básicos
        initObjects()
        initToolBar()
        initNavigationView()
        initPermissionsGPS()

        // 2. Cargar preferencias
        initPreferences()
        recuperarPreferencias() // Aquí se establece deporteSeleccionado

        // 3. Cargar datos de Firebase (incluye records)
        cargarDesdeBD()

       /* val toolbar: androidx.appcompat.widget.Toolbar= findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)*/


    }

    private fun initStopWatch() {
        tvChrono.text = getString(R.string.init_stop_watch_value)
    }

    private fun initChrono(){

        tvChrono = findViewById(R.id.tvChrono)
        tvChrono.setTextColor(ContextCompat.getColor( this, R.color.white))
        initStopWatch()
        //capturar el ancho y alto del layout para ir avanzando la barra de carrera y descanso
        widthScreenPixels = resources.displayMetrics.widthPixels
        heightScreenPixels = resources.displayMetrics.heightPixels

        widthAnimations = widthScreenPixels
        Log.d("ProgressBar", "Width animations: $widthAnimations")


        val lyChronoProgressBg = findViewById<LinearLayout>(R.id.lyChronoProgressBg)
        val lyRoundProgressBg = findViewById<LinearLayout>(R.id.lyRoundProgressBg)
        lyChronoProgressBg.translationX = -widthAnimations.toFloat()
        lyRoundProgressBg.translationX = -widthAnimations.toFloat()

        val tvReset: TextView = findViewById(R.id.tvReset)
        tvReset.setOnClickListener { resetClicked()  }

        fbCamara = findViewById(R.id.fbCamera)
        fbCamara.isVisible = false
    }
    //funcion para ocultar layouts cuando se requiera
    private fun hideLayouts(){
        var lyMap = findViewById<LinearLayout>(R.id.lyMap)
        var lyFragmentMap = findViewById<LinearLayout>(R.id.lyFragmentMap)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyChallengesSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenges = findViewById<LinearLayout>(R.id.lyChallenges)
       /* val lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolumes = findViewById<LinearLayout>(R.id.lySettingsVolumes)
        var lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        var lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)*/


        setHeightLinearLayout(lyMap, 0)
        setHeightLinearLayout(lyIntervalModeSpace,0)
        setHeightLinearLayout(lyChallengesSpace,0)
       /* setHeightLinearLayout(lySettingsVolumesSpace,0)
        setHeightLinearLayout(lySoftTrack,0)
        setHeightLinearLayout(lySoftVolume,0)*/

        lyFragmentMap.translationY = -300f
        lyIntervalMode.translationY = -300f
        lyChallenges.translationY = -300f
        //lySettingsVolumes.translationY = -300f
    }
    //controlar las metricas de los circulos bar
    private fun initMetrics(){
        csbCurrentDistance = findViewById(R.id.csbCurrentDistance)
        csbChallengeDistance = findViewById(R.id.csbChallengeDistance)
        csbRecordDistance = findViewById(R.id.csbRecordDistance)

        csbCurrentAvgSpeed = findViewById(R.id.csbCurrentAvgSpeed)
        csbRecordAvgSpeed = findViewById(R.id.csbRecordAvgSpeed)

        csbCurrentSpeed = findViewById(R.id.csbCurrentSpeed)
        csbCurrentMaxSpeed = findViewById(R.id.csbCurrentMaxSpeed)
        csbRecordSpeed = findViewById(R.id.csbRecordSpeed)

        csbCurrentDistance.progress = 0f
        csbChallengeDistance.progress = 0f

        csbCurrentAvgSpeed.progress = 0f

        csbCurrentSpeed.progress = 0f
        csbCurrentMaxSpeed.progress = 0f

        tvDistanceRecord = findViewById(R.id.tvDistanceRecord)
        tvAvgSpeedRecord = findViewById(R.id.tvAvgSpeedRecord)
        tvMaxSpeedRecord = findViewById(R.id.tvMaxSpeedRecord)

        tvDistanceRecord.text = ""
        tvAvgSpeedRecord.text = ""
        tvMaxSpeedRecord.text = ""
    }

    //inicializar los switch
    private fun initSwitchs(){
        swIntervalMode = findViewById(R.id.swIntervalMode)
        swChallenges = findViewById(R.id.swChallenges)
      //  swVolumes = findViewById(R.id.swVolumes)
    }
    //inicializar los objetos del intervalo y duracoion
    private fun initIntervalMode(){
        npDurationInterval = findViewById(R.id.npDurationInterval)
        tvRunningTime = findViewById(R.id.tvRunningTime)
        tvWalkingTime = findViewById(R.id.tvWalkingTime)
        csbRunWalk = findViewById(R.id.csbRunWalk)

        //establecer los parametros de la duracion de intervalos
        npDurationInterval.minValue = 1
        npDurationInterval.maxValue = 60
        npDurationInterval.value = 5
        npDurationInterval.wrapSelectorWheel = true // permitir elegir al usuario el intervalo en forma de rueda
        npDurationInterval.setFormatter(NumberPicker.Formatter { i -> String.format("%02d", i) })//convertir a texto los segundos para el formato 00:00:00

        //indicamos el intervalo del circular circbar como maximo en 60 que son los segundos
        npDurationInterval.setOnValueChangedListener { picker, oldVal, newVal ->
            csbRunWalk.max = (newVal*60).toFloat()
            csbRunWalk.progress = csbRunWalk.max/2


            tvRunningTime.text = getFormattedStopWatch(((newVal*60/2)*1000).toLong()).subSequence(3,8)
            tvWalkingTime.text = tvRunningTime.text

            ROUND_INTERVAL = newVal * 60
            TIME_RUNNING = ROUND_INTERVAL / 2 // poner a la mitad el intervalo por defecto
        }

        csbRunWalk.max = 300f
        csbRunWalk.progress = 150f
        csbRunWalk.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar,progress: Float,fromUser: Boolean) {

                if (fromUser){
                    var STEPS_UX: Int = 15
                    if (ROUND_INTERVAL > 600) STEPS_UX = 60 // si es mayor a 10 minutos el valor cambia de minutos en minuto
                    if (ROUND_INTERVAL > 1800) STEPS_UX = 300 //si es mayor a 30 minutos puede cambiar el intervalo de 3 minutos
                    var set: Int = 0
                    var p = progress.toInt()

                    var limit = 60
                    if (ROUND_INTERVAL > 1800) limit = 300

                    if (p%STEPS_UX != 0 && progress != csbRunWalk.max){
                        while (p >= limit) p -= limit
                        while (p >= STEPS_UX) p -= STEPS_UX
                        if (STEPS_UX-p > STEPS_UX/2) set = -1 * p
                        else set = STEPS_UX-p

                        if (csbRunWalk.progress + set > csbRunWalk.max)
                            csbRunWalk.progress = csbRunWalk.max
                        else
                            csbRunWalk.progress = csbRunWalk.progress + set
                    }
                }

                tvRunningTime.text = getFormattedStopWatch((csbRunWalk.progress.toInt() *1000).toLong()).subSequence(3,8)
                tvWalkingTime.text = getFormattedStopWatch(((ROUND_INTERVAL- csbRunWalk.progress.toInt())*1000).toLong()).subSequence(3,8) //obtener la resta de caminar y correr
                TIME_RUNNING = getSecFromWatch(tvRunningTime.text.toString())
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar) {
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar) {
            }
        })
    }

    //inicializar los objetos de las horas, minutos y segundos
    private fun initChallengeMode(){
        npChallengeDistance = findViewById(R.id.npChallengeDistance)
        npChallengeDurationHH = findViewById(R.id.npChallengeDurationHH)
        npChallengeDurationMM = findViewById(R.id.npChallengeDurationMM)
        npChallengeDurationSS = findViewById(R.id.npChallengeDurationSS)

        npChallengeDistance.minValue = 1
        npChallengeDistance.maxValue = 300
        npChallengeDistance.value = 10
        npChallengeDistance.wrapSelectorWheel = true


        npChallengeDistance.setOnValueChangedListener { picker, oldVal, newVal ->
            challengeDistance = newVal.toFloat()
            csbChallengeDistance.max = newVal.toFloat()
            csbChallengeDistance.progress = newVal.toFloat()
            challengeDuration = 0
            //establecer la distancia segun el record o lo propuesto por el usuario
            if (csbChallengeDistance.max > csbRecordDistance.max)
                csbCurrentDistance.max = csbChallengeDistance.max
        }

        npChallengeDurationHH.minValue = 0
        npChallengeDurationHH.maxValue = 23
        npChallengeDurationHH.value = 1
        npChallengeDurationHH.wrapSelectorWheel = true
        npChallengeDurationHH.setFormatter(NumberPicker.Formatter { i -> String.format("%02d", i) })

        npChallengeDurationMM.minValue = 0
        npChallengeDurationMM.maxValue = 59
        npChallengeDurationMM.value = 0
        npChallengeDurationMM.wrapSelectorWheel = true
        npChallengeDurationMM.setFormatter(NumberPicker.Formatter { i -> String.format("%02d", i) })

        npChallengeDurationSS.minValue = 0
        npChallengeDurationSS.maxValue = 59
        npChallengeDurationSS.value = 0
        npChallengeDurationSS.wrapSelectorWheel = true
        npChallengeDurationSS.setFormatter(NumberPicker.Formatter { i -> String.format("%02d", i) })

        npChallengeDurationHH.setOnValueChangedListener { picker, oldVal, newVal ->
            getChallengeDuration(newVal, npChallengeDurationMM.value, npChallengeDurationSS.value)
        }
        npChallengeDurationMM.setOnValueChangedListener { picker, oldVal, newVal ->
            getChallengeDuration(npChallengeDurationHH.value, newVal, npChallengeDurationSS.value)
        }
        npChallengeDurationSS.setOnValueChangedListener { picker, oldVal, newVal ->
            getChallengeDuration(npChallengeDurationHH.value, npChallengeDurationMM.value, newVal)
        }

        cbNotify = findViewById<CheckBox>(R.id.cbNotify)
        cbAutoFinish = findViewById<CheckBox>(R.id.cbAutoFinish)

    }


    private fun initObjects(){
        initChrono()
        hideLayouts()
        initMetrics()
        initSwitchs()
        initIntervalMode()
        initChallengeMode()
        hidePopUpRun()
        initMap()
        initTotales()
        initNiveles()


        initPreferences()
        recuperarPreferencias()

    }
//iniciar los totales en 0 para el usuario que no tenga registros
    private fun initTotales(){
        totalesBicileta = Totales()

        carrerasTotales = Totales()

        totalesBicileta.totalCarreras = 0
        totalesBicileta.totalDistancia = 0.0
        totalesBicileta.totalTiempo = 0
        totalesBicileta.recordDistancia = 0.0
        totalesBicileta.recordVelocidad = 0.0
        totalesBicileta.recordVelocidadPromedio = 0.0

    carrerasTotales.totalCarreras = 0
    carrerasTotales.totalDistancia = 0.0
    carrerasTotales.totalTiempo = 0
    carrerasTotales.recordDistancia = 0.0
    carrerasTotales.recordVelocidad = 0.0
    carrerasTotales.recordVelocidadPromedio = 0.0

    }
//inicializar las variables con los datos que tiene que tener al inicio
    private fun initNiveles(){
        nivelDeporteSeleccionado = Nivel()
        nivelBicicleta = Nivel()
        nivelCorriendo = Nivel()

        listaNivelBicicleta = arrayListOf()
        listaNivelBicicleta.clear()

        listaNivelCarrera = arrayListOf()
        listaNivelCarrera.clear()

        nivelBicicleta.nombre = "tortuga"
        nivelBicicleta.imagen = "level_1"
        nivelBicicleta.objetivoCarreras = 5
        nivelBicicleta.objetivoDeDistancia = 40

        nivelCorriendo.nombre = "tortuga"
        nivelCorriendo.imagen = "level_1"
        nivelCorriendo.objetivoCarreras = 5
        nivelCorriendo.objetivoDeDistancia = 10
    }
    private fun cargarDesdeBD(){
        cargarTodosLosUsuarios()
    }

    private fun cargarTodosLosUsuarios(){
        cargarTodosLosDeportes("Bicicleta")
        cargarTodosLosDeportes("Carrera")

    }
//accedemos a la base de datos para conseguir los valores correspondientes
    private fun cargarTodosLosDeportes(deporte: String){
        var collection = "totales$deporte"
        var dbUsuariosTotales = FirebaseFirestore.getInstance()//obtenemos la instancia de firebase
        dbUsuariosTotales.collection(collection).document(useremail)//obtenemos los totales del usuario para saber los records y carreras
            .get()
            .addOnSuccessListener { document ->
                if (document.data?.size != null){//si el documento tiene datos lo guardamos
                    var total = document.toObject(Totales::class.java)
                    when (deporte){
                        "Bicicleta" -> totalesBicileta = total!!
                        "Carrera" -> carrerasTotales = total!!
                    }

                }
                else{//si no hay datos los creamos como nuevo usuario
                    val dbTotal: FirebaseFirestore = FirebaseFirestore.getInstance()//creamos una instancia para agregar datos
                    dbTotal.collection(collection).document(useremail).set(hashMapOf(
                        "recordVelocidadPromedio" to 0.0,
                        "recordDistancia" to 0.0,
                        "recordVelocidad" to 0.0,
                        "totalDistancia" to 0.0,
                        "totalCarreras" to 0,
                        "totalTiempo" to 0
                    ))
                }
                sportsLoaded++
                establecerNivelDeporte(deporte)
                if (sportsLoaded == 2) seleccionarDeporte(deporteSeleccionado)

            }//en caso de algun fallo se genera un log
            .addOnFailureListener { exception ->
                Log.d("ERROR loadTotalsUser", "get failed with ", exception)
            }

    }


//capturar los niveles de la aplicacion
    private fun establecerNivelDeporte(deporte: String){
        val dbNiveles: FirebaseFirestore = FirebaseFirestore.getInstance()
        dbNiveles.collection("nivel$deporte")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    when (deporte){
                        "Bicicleta" -> listaNivelBicicleta.add(document.toObject(Nivel::class.java))
                        "Carrera" -> listaNivelCarrera.add(document.toObject(Nivel::class.java))
                    }

                }
                when (deporte){
                    "Bicicleta" -> setNivelBicicleta()
                    "Carrera" -> setNivelCarrera()
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }



    //establecer el nivel de bicicleta
    private fun setNivelBicicleta(){
        var lyNavLevelBike = findViewById<LinearLayout>(R.id.lyNavLevelBike)
        if (totalesBicileta.totalTiempo!! == 0) setHeightLinearLayout(lyNavLevelBike, 0)//si el tiempo es 0 ocultamos el layout
        else{//si hay valores lo mostramos con sus valores correspondientes
            setHeightLinearLayout(lyNavLevelBike, 300)
            for (nivel in listaNivelBicicleta){
                if (totalesBicileta.totalCarreras!! < nivel.objetivoCarreras!!//verificar los requisitos de carrera
                    || totalesBicileta.totalDistancia!! < nivel.objetivoDeDistancia!!){

                    nivelBicicleta.nombre = nivel.nombre!!
                    nivelBicicleta.imagen = nivel.imagen!!
                    nivelBicicleta.objetivoCarreras = nivel.objetivoCarreras!!
                    nivelBicicleta.objetivoDeDistancia = nivel.objetivoDeDistancia!!

                    break
                }
            }

            var ivLevelBike = findViewById<ImageView>(R.id.ivLevelBike)
            var tvTotalTimeBike = findViewById<TextView>(R.id.tvTotalTimeBike)
            var tvTotalRunsBike = findViewById<TextView>(R.id.tvTotalRunsBike)
            var tvTotalDistanceBike = findViewById<TextView>(R.id.tvTotalDistanceBike)
            var tvNumberLevelBike = findViewById<TextView>(R.id.tvNumberLevelBike)

            var levelText = "${getString(R.string.level)} ${nivelBicicleta.imagen!!.subSequence(6,7).toString()}"//verificamos el nivel en que esta

            tvNumberLevelBike.text = levelText

            var tt = getFormattedTotalTime(totalesBicileta.totalTiempo!!.toLong())
            tvTotalTimeBike.text = tt

            when (nivelBicicleta.imagen){
                "level_1" -> ivLevelBike.setImageResource(R.drawable.tortuga)
                "level_2" -> ivLevelBike.setImageResource(R.drawable.liebre)
                "level_3" -> ivLevelBike.setImageResource(R.drawable.lobo)
                "level_4" -> ivLevelBike.setImageResource(R.drawable.leopardo)
               /* "level_5" -> ivLevelBike.setImageResource(R.drawable.level_5)
                "level_6" -> ivLevelBike.setImageResource(R.drawable.level_6)
                "level_7" -> ivLevelBike.setImageResource(R.drawable.level_7)*/
            }
            tvTotalRunsBike.text = "${totalesBicileta.totalCarreras}/${nivelBicicleta.objetivoCarreras}"
            var porcent = totalesBicileta.totalDistancia!!.toInt() * 100 / nivelBicicleta.objetivoDeDistancia!!.toInt()
            tvTotalDistanceBike.text = "${porcent.toInt()}%"

            var csbDistanceBike = findViewById<CircularSeekBar>(R.id.csbDistanceBike)
            csbDistanceBike.max = nivelBicicleta.objetivoDeDistancia!!.toFloat()
            if (totalesBicileta.totalDistancia!! >= nivelBicicleta.objetivoDeDistancia!!.toDouble())
                csbDistanceBike.progress = csbDistanceBike.max
            else
                csbDistanceBike.progress = totalesBicileta.totalDistancia!!.toFloat()

            var csbRunsBike = findViewById<CircularSeekBar>(R.id.csbRunsBike)
            csbRunsBike.max = nivelBicicleta.objetivoCarreras!!.toFloat()
            if (totalesBicileta.totalCarreras!! >= nivelBicicleta.objetivoCarreras!!.toInt())
                csbRunsBike.progress = csbRunsBike.max
            else
                csbRunsBike.progress = totalesBicileta.totalCarreras!!.toFloat()

        }
    }

    //establecer el nivel de carrera
    private fun setNivelCarrera(){
        var lyNavLevelRunning = findViewById<LinearLayout>(R.id.lyNavLevelRunning)
        if (carrerasTotales.totalTiempo!! == 0) setHeightLinearLayout(lyNavLevelRunning, 0)
        else{

            setHeightLinearLayout(lyNavLevelRunning, 300)
            for (level in listaNivelCarrera){
                if (carrerasTotales.totalCarreras!! < level.objetivoCarreras!!.toInt()
                    || carrerasTotales.totalDistancia!! < level.objetivoDeDistancia!!.toDouble()){

                    nivelCorriendo.nombre = level.nombre!!
                    nivelCorriendo.imagen = level.imagen!!
                    nivelCorriendo.objetivoCarreras = level.objetivoCarreras!!
                    nivelCorriendo.objetivoDeDistancia = level.objetivoDeDistancia!!

                    break
                }
            }

            var ivLevelRunning = findViewById<ImageView>(R.id.ivLevelRunning)
            var tvTotalTimeRunning = findViewById<TextView>(R.id.tvTotalTimeRunning)
            var tvTotalRunsRunning = findViewById<TextView>(R.id.tvTotalRunsRunning)
            var tvTotalDistanceRunning = findViewById<TextView>(R.id.tvTotalDistanceRunning)


            var tvNumberLevelRunning = findViewById<TextView>(R.id.tvNumberLevelRunning)
            var levelText = "${getString(R.string.level)} ${nivelCorriendo.imagen!!.subSequence(6,7).toString()}"
            tvNumberLevelRunning.text = levelText

            var tt = getFormattedTotalTime(carrerasTotales.totalTiempo!!.toLong())
            tvTotalTimeRunning.text = tt

            when (nivelCorriendo.imagen){
                "level_1" -> ivLevelRunning.setImageResource(R.drawable.tortuga)
                "level_2" -> ivLevelRunning.setImageResource(R.drawable.liebre)
                "level_3" -> ivLevelRunning.setImageResource(R.drawable.lobo)
                "level_4" -> ivLevelRunning.setImageResource(R.drawable.leopardo)
               /* "level_5" -> ivLevelRunning.setImageResource(R.drawable.level_5)
                "level_6" -> ivLevelRunning.setImageResource(R.drawable.level_6)
                "level_7" -> ivLevelRunning.setImageResource(R.drawable.level_7)*/
            }

            tvTotalRunsRunning.text = "${carrerasTotales.totalCarreras}/${nivelCorriendo.objetivoCarreras}"
            var porcent = carrerasTotales.totalDistancia!!.toInt() * 100 / nivelCorriendo.objetivoDeDistancia!!.toInt()
            tvTotalDistanceRunning.text = "${porcent.toInt()}%"

            var csbDistanceRunning = findViewById<CircularSeekBar>(R.id.csbDistanceRunning)
            csbDistanceRunning.max = nivelCorriendo.objetivoDeDistancia!!.toFloat()
            if (carrerasTotales.totalDistancia!! >= nivelCorriendo.objetivoDeDistancia!!.toDouble())
                csbDistanceRunning.progress = csbDistanceRunning.max
            else
                csbDistanceRunning.progress = carrerasTotales.totalDistancia!!.toFloat()

            var csbRunsRunning = findViewById<CircularSeekBar>(R.id.csbRunsRunning)
            csbRunsRunning.max = nivelCorriendo.objetivoCarreras!!.toFloat()
            if (carrerasTotales.totalCarreras!! >= nivelCorriendo.objetivoCarreras!!.toInt())
                csbRunsRunning.progress = csbRunsRunning.max
            else
                csbRunsRunning.progress = carrerasTotales.totalCarreras!!.toFloat()

        }
    }

    private fun initPreferences(){
        sharedPreferences = getSharedPreferences("sharedPrefs_$useremail", MODE_PRIVATE)//indicamos que guardaremos en modo privado
        editor = sharedPreferences.edit()
    }

    //recuperamos las preferencias que el usuario ha programado
    private fun recuperarPreferencias(){
        if (sharedPreferences.getString(llave_UsuarioApp, "null") == useremail){
            deporteSeleccionado = sharedPreferences.getString(llave_deporteSeleccionado, "Carrera").toString()

            swIntervalMode.isChecked = sharedPreferences.getBoolean(llave_modointervalo, false)
            if (swIntervalMode.isChecked){
                npDurationInterval.value = sharedPreferences.getInt(llave_mododuracionintervalo, 5)
                ROUND_INTERVAL = npDurationInterval.value*60
                csbRunWalk.progress = sharedPreferences.getFloat(llave_progressCircularSeekBar, 150.0f)
                csbRunWalk.max = sharedPreferences.getFloat(llave_maxCircularSeekBar, 300.0f)
                tvRunningTime.text = sharedPreferences.getString(llave_tiempocorriendo, "2:30")
                tvWalkingTime.text = sharedPreferences.getString(llave_tiempocaminando, "2:30")
                swIntervalMode.callOnClick()
            }

            swChallenges.isChecked = sharedPreferences.getBoolean(llave_mododesafio, false)
            if (swChallenges.isChecked){
                swChallenges.callOnClick()
                if (sharedPreferences.getBoolean(llave_mododesafioduracion, false)){
                    npChallengeDurationHH.value = sharedPreferences.getInt(llave_duraciondesafioHH, 1)
                    npChallengeDurationMM.value = sharedPreferences.getInt(llave_duraciondesafioMM, 0)
                    npChallengeDurationSS.value = sharedPreferences.getInt(llave_duraciondesafioSS, 0)
                    getChallengeDuration(npChallengeDurationHH.value,npChallengeDurationMM.value,npChallengeDurationSS.value)
                    challengeDistance = 0f

                    showChallenge("duration")
                }
                if (sharedPreferences.getBoolean(llave_mododesafiodistancia, false)){
                    npChallengeDistance.value = sharedPreferences.getInt(llave_mododesafio, 10)
                    challengeDistance = npChallengeDistance.value.toFloat()
                    challengeDuration = 0

                    showChallenge("distance")
                }
            }
           /* cbNotify.isChecked = sharedPreferences.getBoolean(key_challengeNofify, true)
            cbAutoFinish.isChecked = sharedPreferences.getBoolean(key_challengeAutofinish, false)

            sbHardVolume.progress = sharedPreferences.getInt(key_hardVol, 100)
            sbSoftVolume.progress = sharedPreferences.getInt(key_softVol, 100)
            sbNotifyVolume.progress = sharedPreferences.getInt(key_notifyVol, 100)*/

        }else deporteSeleccionado = "Carrera"

    }



    //guardamos los datos que el usuario a programado como ser distancias, tiempos, etc
    private fun guardarPreferencias(){
        editor.clear()
        editor.apply{

            putString(llave_UsuarioApp, useremail)
            putString(llave_proveedor, providerSession)

            putString(llave_deporteSeleccionado, deporteSeleccionado)

            putBoolean(llave_modointervalo, swIntervalMode.isChecked)
            putInt(llave_mododuracionintervalo, npDurationInterval.value)
            putFloat(llave_progressCircularSeekBar, csbRunWalk.progress)
            putFloat(llave_maxCircularSeekBar, csbRunWalk.max)
            putString(llave_tiempocorriendo, tvRunningTime.text.toString())
            putString(llave_tiempocaminando, tvWalkingTime.text.toString())

            putBoolean(llave_mododesafio, swChallenges.isChecked)
            putBoolean(llave_mododesafioduracion, !(challengeDuration == 0))
            putInt(llave_duraciondesafioHH, npChallengeDurationHH.value)
            putInt(llave_duraciondesafioMM, npChallengeDurationMM.value)
            putInt(llave_duraciondesafioSS, npChallengeDurationSS.value)
            putBoolean(llave_mododesafiodistancia, !(challengeDistance == 0f))
            putInt(llave_desafiodistancia, npChallengeDistance.value)


            putBoolean(llave_notificacionesdesafio, cbNotify.isChecked)
            putBoolean(llave_autofinalizar, cbAutoFinish.isChecked)


            //putInt(llave_notificacion, sbNotifyVolume.progress)

        }.apply()
    }

    //funcion para mandar una alerta al usuario antes de limpiar las preferencias
    private fun alertaLimpiarPreferencias(){
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.alertClearPreferencesTitle))
            .setMessage(getString(R.string.alertClearPreferencesDescription))
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener{dialgo, which ->
                    llamarLimpiarPreferencias()
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener{dialgo, which ->

                })
            .setCancelable(true)
            .show()
    }

    //mensaje al usuario para decir que los ajustes se restablecieron
    private fun llamarLimpiarPreferencias(){
        editor.clear().apply()
        Toast.makeText(this, "Tus ajustes han sido reestablecidos", Toast.LENGTH_SHORT).show()
    }



    //funcion para el switch de reto
    fun inflateChallenges(v: View){
        val lyChallengesSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenges = findViewById<LinearLayout>(R.id.lyChallenges)
        if (swChallenges.isChecked){
            animateViewofInt(swChallenges, "textColor", ContextCompat.getColor(this, R.color.verde_oscuro), 50)
            setHeightLinearLayout(lyChallengesSpace, 750)
            animateViewofFloat(lyChallenges, "translationY", 0f, 500)
        }
        else{
            swChallenges.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lyChallengesSpace,0)
            lyChallenges.translationY = -300f
            //llevar el control de distancia y tiempo de la carrera
            challengeDistance = 0f
            challengeDuration = 0
        }
    }




    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (lyPopupRun.isVisible) {
            cerrarVentanaCorredor()
        } else {
            super.onBackPressed()
        }
    }
//obtener los valores proporcionados por el gps
    private fun initPermissionsGPS(){
        if (allPermissionsGrantedGPS())
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        else
            requestPermissionLocation()
    }
    //obtener todos los permisos del gps
    private fun allPermissionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionLocation(){
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
    }

    //iniciamos el fragmento de navegacion del menu
    private fun initNavigationView() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        val headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false)

        // remover cualquier header existente
        navigationView.removeHeaderView(headerView)

        //añadir el nuevo header
        navigationView.addHeaderView(headerView)

        var tvUser: TextView = headerView.findViewById(R.id.tvUser)
        tvUser.text= useremail
    }

   /* private fun initToolBar() {
        val toolbar: androidx.appcompat.widget.Toolbar= findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer= findViewById(R.id.drawer_layout)
        val toogle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.bar_title,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toogle)
        toogle.syncState()
    }*/

    private fun initToolBar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.bar_title,
            R.string.navigation_drawer_close
        ).apply {

            syncState()
        }

        drawer.addDrawerListener(toggle)
    }

    private fun singOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
    fun showDuration(v: View){
        showChallenge("duration")
    }
    fun showDistance(v:View){
        showChallenge("distance")
    }
    private fun showChallenge(option: String){
        //llamada de los layout que deseamos controlar
        var lyChallengeDuration = findViewById<LinearLayout>(R.id.lyChallengeDuration)
        var lyChallengeDistance = findViewById<LinearLayout>(R.id.lyChallengeDistance)
        var tvChallengeDuration = findViewById<TextView>(R.id.tvChallengeDuration)
        var tvChallengeDistance = findViewById<TextView>(R.id.tvChallengeDistance)

        when (option){
            "duration" ->{
                lyChallengeDuration.translationZ = 5f
                lyChallengeDistance.translationZ = 0f

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this, R.color.verde_oscuro))
                tvChallengeDuration.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_dark))

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvChallengeDistance.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_medium))

                challengeDistance = 0f
                getChallengeDuration(npChallengeDurationHH.value, npChallengeDurationMM.value, npChallengeDurationSS.value)//calcular los segundos
            }
            "distance" -> {
                lyChallengeDuration.translationZ = 0f
                lyChallengeDistance.translationZ = 5f

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvChallengeDuration.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_medium))

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this, R.color.verde_oscuro))
                tvChallengeDistance.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_dark))

                challengeDuration = 0
                challengeDistance = npChallengeDistance.value.toFloat()
            }
        }
    }

    //funcion para calcular las horas, minutos y segundos
    private fun getChallengeDuration(hh: Int, mm: Int, ss: Int){
        var hours: String = hh.toString()
        if (hh<10) hours = "0"+hours
        var minutes: String = mm.toString()
        if (mm<10) minutes = "0"+minutes
        var seconds: String = ss.toString()
        if (ss<10) seconds = "0"+seconds

        challengeDuration = getSecFromWatch("${hours}:${minutes}:${seconds}")
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_record -> llamarRecordActivity()
            R.id.nav_item_clearpreferences -> alertaLimpiarPreferencias()
            R.id.nav_item_signout -> singOut()
        }
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun llamarRecordActivity() {
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }

    //controlar el funcionamiento del switch de los intervalos
    fun inflateIntervalMode(v: View){
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
      /*  var lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        var lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)*/
        var tvRounds = findViewById<TextView>(R.id.tvRounds)

        if (swIntervalMode.isChecked){
            animateViewofInt(swIntervalMode, "textColor", ContextCompat.getColor(this, R.color.verde_oscuro), 50)
            setHeightLinearLayout(lyIntervalModeSpace, 600)
            animateViewofFloat(lyIntervalMode, "translationY", 0f, 500)
            animateViewofFloat (tvChrono, "translationX", -110f, 500)
            tvRounds.setText(R.string.rounds)
            animateViewofInt(tvRounds, "textColor", ContextCompat.getColor(this, R.color.white), 500)

         /*   setHeightLinearLayout(lySoftTrack,120)
            setHeightLinearLayout(lySoftVolume,200)
            if (swVolumes.isChecked){
                var lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
                setHeightLinearLayout(lySettingsVolumesSpace,600)
            }*/
            var tvRunningTime = findViewById<TextView>(R.id.tvRunningTime)
            TIME_RUNNING = getSecFromWatch(tvRunningTime.text.toString())//capturamos los segundos de la propiedad text
        }
        else{
            swIntervalMode.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lyIntervalModeSpace,0)
            lyIntervalMode.translationY = -200f
            animateViewofFloat (tvChrono, "translationX", 0f, 500)
            tvRounds.text = ""
          /*  setHeightLinearLayout(lySoftTrack,0)
            setHeightLinearLayout(lySoftVolume,0)
            if (swVolumes.isChecked){
                var lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
                setHeightLinearLayout(lySettingsVolumesSpace,400)
            }*/
        }
    }

    private var chronometer: Runnable = object : Runnable {
        override fun run() {
            try{
                //verificar si se empieza a correr o si es nueva ronda
                if (swIntervalMode.isChecked){
                  //  checkStopRun(timeInSeconds)
                  //  checkNewRound(timeInSeconds)
                }

                if (activatedGPS && timeInSeconds.toInt() % INTERVAL_LOCATION == 0) manageLocation()

                timeInSeconds += 1
                updateStopWatchView()
              //  updateProgressBarRound(timeInSeconds)
            } finally {
                mHandler!!.postDelayed(this,mInterval.toLong())//intervalo entre ejecuciones
            }
        }
    }
    fun startOrStopButtonClicked (v: View){
       manageStartStop()
    }
//manejar la localizacion
    private fun manageLocation(){

        if (checkPermission()){

            if (isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
                    &&  ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {


                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        requestNewLocationData()
                    }
                }
            }
            else activationLocation()
        }
        else requestPermissionLocation()
    }
    //verificar que todos los permisos esten aprobados
    private fun checkPermission(): Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }
//obtener los datos de la ubicacion
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(){
        var mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.priority = PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper())


    }

    private val mLocationCallBack = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation : Location = locationResult.lastLocation!!

            init_lt = mLastLocation.latitude
            init_ln = mLastLocation.longitude

            if (timeInSeconds > 0L) registerNewLocation(mLastLocation)
        }
    }
//registramos la nueva ubicacion
private fun registerNewLocation(location: Location){
    var new_latitude: Double = location.latitude
    var new_longitude: Double = location.longitude

    if (flagSavedLocation){
        if (timeInSeconds >= INTERVAL_LOCATION){
            var distanceInterval = calculateDistance(new_latitude, new_longitude)
            //verificar que la distancia no sea la que se acepta por defecto
            if ( distanceInterval <= LIMITE_DISTANCIA_ACEPTADA){
                updateSpeeds(distanceInterval)
                refreshInterfaceData()

                var newPos = LatLng (new_latitude, new_longitude)
                (listPoints as ArrayList<LatLng>).add(newPos)
                crearPolilineas(listPoints)

            }

        }
    }
    latitude = new_latitude
    longitude = new_longitude

    if (mapCentered == true) centrarMapa(latitude, longitude)

    if (minLatitude == null){
        minLatitude = latitude
        maxLatitude = latitude
        minLongitude = longitude
        maxLongitude = longitude
    }
    if (latitude < minLatitude!!) minLatitude = latitude
    if (latitude > maxLatitude!!) maxLatitude = latitude
    if (longitude < minLongitude!!) minLongitude = longitude
    if (longitude > maxLongitude!!) maxLongitude = longitude

    if (location.hasAltitude()){
        if (maxAltitude == null){
            maxAltitude = location.altitude
            minAltitude = location.altitude
        }
        if (location.latitude > maxAltitude!!) maxAltitude = location.altitude
        if (location.latitude < minAltitude!!) minAltitude = location.altitude
    }

}
    //crear polilineas para la ruta del mapa
    private fun crearPolilineas(listPosition: Iterable<LatLng>){
        val polylineOptions = PolylineOptions()
            .width(25f)//ancho de la polilinea
            .color(ContextCompat.getColor(this, R.color.verde_oscuro))
            .addAll(listPosition) //agregar toda la lista de puntos

        val polyline = map.addPolyline(polylineOptions) //pintar la polilinea en el mapa
        polyline.startCap = RoundCap() //disenio de la polilinea

    }
//funcion para calcular la distancia
    private fun calculateDistance(n_lt: Double, n_lg: Double): Double{
        val radioTierra = 6371.0 //en kilómetros

        val dLat = Math.toRadians(n_lt - latitude)
        val dLng = Math.toRadians(n_lg - longitude)
        val sindLat = Math.sin(dLat / 2)
        val sindLng = Math.sin(dLng / 2)
        val va1 =
            Math.pow(sindLat, 2.0) + (Math.pow(sindLng, 2.0)
                    * Math.cos(Math.toRadians(latitude)) * Math.cos(
                Math.toRadians( n_lt  )
            ))
        val va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1))
        var n_distance =  radioTierra * va2

        //if (n_distance < LIMIT_DISTANCE_ACCEPTED) distance += n_distance

        distance += n_distance //acumular la distancia total
        return n_distance
    }

    //actualizamos la distancia y obtenemos la velocidad de lo recorrido
    private fun updateSpeeds(d: Double) {
        //la distancia se calcula en km, asi que la pasamos a metros para el calculo de velocidadr
        //convertirmos m/s a km/h multiplicando por 3.6
        speed = ((d * 1000) / INTERVAL_LOCATION) * 3.6
        if (speed > maxSpeed) maxSpeed = speed
        avgSpeed = ((distance * 1000) / timeInSeconds) * 3.6
    }
//refrescar los datos de la interfaz
    private fun refreshInterfaceData(){
        var tvCurrentDistance = findViewById<TextView>(R.id.tvCurrentDistance)
        var tvCurrentAvgSpeed = findViewById<TextView>(R.id.tvCurrentAvgSpeed)
        var tvCurrentSpeed = findViewById<TextView>(R.id.tvCurrentSpeed)
        tvCurrentDistance.text = roundNumber(distance.toString(), 2)
        tvCurrentAvgSpeed.text = roundNumber(avgSpeed.toString(), 1)
        tvCurrentSpeed.text = roundNumber(speed.toString(), 1)


        csbCurrentDistance.progress = distance.toFloat()

        csbCurrentAvgSpeed.progress = avgSpeed.toFloat()

        csbCurrentSpeed.progress = speed.toFloat()

        if (speed == maxSpeed){
            csbCurrentMaxSpeed.max = csbRecordSpeed.max
            csbCurrentMaxSpeed.progress = speed.toFloat()

            csbCurrentSpeed.max = csbRecordSpeed.max
        }
    }


    //solicitar los permisos de la ubicacion
    private fun manageStartStop(){
        if (timeInSeconds == 0L && isLocationEnabled() == false){//enviar una alerta para pedir al usuario que active el gps
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.alertActivationGPSTitle))
                .setMessage(getString(R.string.alertActivationGPSDescription))
                .setPositiveButton(R.string.aceptActivationGPS,
                    DialogInterface.OnClickListener { dialog, which ->
                        activationLocation()
                    })
                .setNegativeButton(R.string.ignoreActivationGPS,
                    DialogInterface.OnClickListener { dialog, which ->
                        activatedGPS = false
                        manageRun()
                    })
                .setCancelable(true)
                .show()
        }
        else manageRun()
    }
    //verificar si la ubicacion esta habilitada
    private fun isLocationEnabled(): Boolean{
        var locationManager: LocationManager
                = getSystemService(Context.LOCATION_SERVICE) as LocationManager //capturar el servicio que hay en el momentos
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)//pregungar si esta habilitado algun proveedor gps o red
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
    //ir a los ajustes para activar la ubicacion
    private fun activationLocation(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    //funcion para iniciar el mapa
    private fun initMap(){

        listPoints = arrayListOf()
        (listPoints as ArrayList<LatLng>).clear()

        createMapFragment()

        var lyOpenerButton = findViewById<LinearLayout>(R.id.lyOpenerButton)
        if (allPermissionsGrantedGPS()) lyOpenerButton.isEnabled = true
        else  lyOpenerButton.isEnabled = false

    }
//centrar el mapa si el usuario se movio a otra ubicacion y quiere regresar al punto inicial
    fun llamarCentrarMapa(v: View){
        mapCentered = true
        if (latitude == 0.0) centrarMapa(init_lt, init_ln)
        else centrarMapa(latitude, longitude)
    }
//cambiar el tipo de mapa que se visualiza modo hibrido o modo normal
    fun cambiarTipoMapa(v: View){
        var ivTypeMap = findViewById<ImageView>(R.id.ivTypeMap)
        if (map.mapType == GoogleMap.MAP_TYPE_HYBRID){
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            ivTypeMap.setImageResource(R.drawable.mapanormal)
        }
        else{
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            ivTypeMap.setImageResource(R.drawable.maphibrido)
        }

    }

    //manegar los controles de la carrera inicio y detencion
    private fun manageRun(){

        if (timeInSeconds.toInt() == 0){

            fbCamara.isVisible = true
            //deshabilitamos los componentes de la interfaz mientras esta en la carrera
            swIntervalMode.isClickable = false
            npDurationInterval.isEnabled = false
            csbRunWalk.isEnabled = false

            swChallenges.isClickable = false
            npChallengeDistance.isEnabled = false
            npChallengeDurationHH.isEnabled = false
            npChallengeDurationMM.isEnabled = false
            npChallengeDurationSS.isEnabled = false

            tvChrono.setTextColor(ContextCompat.getColor(this, R.color.chrono_running))

            if (activatedGPS){
                flagSavedLocation = false
                manageLocation()
                flagSavedLocation = true
                manageLocation()
            }
        }
        if (!startButtonClicked){
            startButtonClicked = true
            startTime()
            manageEnableButtonsRun(false, true)
        }
        else{
            startButtonClicked = false
            stopTime()
            manageEnableButtonsRun(true, true)
        }
    }

    //controlar los botones para habilitar o deshabilitar las funciones
    private fun manageEnableButtonsRun(e_reset: Boolean, e_run: Boolean){
        val tvReset = findViewById<TextView>(R.id.tvReset)
        val btStart = findViewById<LinearLayout>(R.id.btStart)
        val btStartLabel = findViewById<TextView>(R.id.btStartLabel)
        tvReset.setEnabled(e_reset)
        btStart.setEnabled(e_run)

        if (e_reset){
            tvReset.setBackgroundColor(ContextCompat.getColor(this, R.color.green))//mostramos el fondo verde para finalizar
            animateViewofFloat(tvReset, "translationY", 0f, 500)
        }
        else{
            tvReset.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))//ocultamos el layout y mostramos en gris
            animateViewofFloat(tvReset, "translationY", 150f, 500)
        }

        if (e_run){
            if (startButtonClicked){
                btStart.background = getDrawable(R.drawable.circle_background_topause)//ponemos el color en rojo
                btStartLabel.setText(R.string.stop)
            }
            else{
                btStart.background = getDrawable(R.drawable.circle_background_toplay)//ponemos el color en azul
                btStartLabel.setText(R.string.start)
            }
        }
        else btStart.background = getDrawable(R.drawable.circle_background_todisable)//ponemos el color en gris


    }
    // iniciar la carrera
    private fun startTime(){
        mHandler = Handler(Looper.getMainLooper())
        chronometer.run()
    }
    //detener la carrera
    private fun stopTime(){
        mHandler?.removeCallbacks(chronometer)
    }
    private fun updateStopWatchView(){
        tvChrono.text = getFormattedStopWatch(timeInSeconds * 1000)
    }
    //reiniciamos el cronometro finalizamos la carrera y reiniciamos la interfaz
    private fun resetClicked(){
        guardarPreferencias()
        mostrarVentanaEmergente()
        actualizarTotalesUsuario()
        establecerNivelDeporte(deporteSeleccionado)
        resetTimeView()
        resetInterface()
    }



    private fun actualizarTotalesUsuario(){
        totalesDeporteSeleccionado.totalCarreras = totalesDeporteSeleccionado.totalCarreras!! + 1
        totalesDeporteSeleccionado.totalDistancia = totalesDeporteSeleccionado.totalDistancia!! + distance
        totalesDeporteSeleccionado.totalTiempo = totalesDeporteSeleccionado.totalTiempo!! + timeInSeconds.toInt()

        if (distance > totalesDeporteSeleccionado.recordDistancia!!){
            totalesDeporteSeleccionado.recordDistancia = distance
        }
        if (maxSpeed > totalesDeporteSeleccionado.recordVelocidad!!){
            totalesDeporteSeleccionado.recordVelocidad = maxSpeed
        }
        if (avgSpeed > totalesDeporteSeleccionado.recordVelocidadPromedio!!){
            totalesDeporteSeleccionado.recordVelocidadPromedio = avgSpeed
        }

        totalesDeporteSeleccionado.totalDistancia = roundNumber(totalesDeporteSeleccionado.totalDistancia.toString(),1).toDouble()
        totalesDeporteSeleccionado.recordDistancia = roundNumber(totalesDeporteSeleccionado.recordDistancia.toString(),1).toDouble()
        totalesDeporteSeleccionado.recordVelocidad = roundNumber(totalesDeporteSeleccionado.recordVelocidad.toString(),1).toDouble()
        totalesDeporteSeleccionado.recordVelocidadPromedio = roundNumber(totalesDeporteSeleccionado.recordVelocidadPromedio.toString(),1).toDouble()

        var collection = "totales$deporteSeleccionado"
        var dbUpdateTotals = FirebaseFirestore.getInstance()
        dbUpdateTotals.collection(collection).document(useremail)
            .update("recordVelocidadPromedio", totalesDeporteSeleccionado.recordVelocidadPromedio)
        dbUpdateTotals.collection(collection).document(useremail)
            .update("recordDistancia", totalesDeporteSeleccionado.recordDistancia)
        dbUpdateTotals.collection(collection).document(useremail)
            .update("recordVelocidad", totalesDeporteSeleccionado.recordVelocidad)
        dbUpdateTotals.collection(collection).document(useremail)
            .update("totalDistancia", totalesDeporteSeleccionado.totalDistancia)
        dbUpdateTotals.collection(collection).document(useremail)
            .update("totalCarreras", totalesDeporteSeleccionado.totalCarreras)
        dbUpdateTotals.collection(collection).document(useremail)
            .update("totalTiempo", totalesDeporteSeleccionado.totalTiempo)

        when (deporteSeleccionado){
            "Bicicleta" -> {
                totalesBicileta = totalesDeporteSeleccionado
            }
            "Carrera" -> {
                carrerasTotales = totalesDeporteSeleccionado
            }
        }
    }

    //reiniciar las variales del cronometro para que el usuario vuelva a iniciar la carrera
    private fun resetVariablesRun(){
        timeInSeconds = 0
        rounds = 1
        activatedGPS=true
        challengeDistance=0f
        challengeDuration=0
        flagSavedLocation=false
        minAltitude = null
        maxAltitude = null
        minLatitude = null
        maxLatitude = null
        minLongitude = null
        maxLongitude = null
       (listPoints as ArrayList<LatLng>).clear()

        challengeDistance = 0f
        challengeDuration = 0

        activatedGPS = true
        flagSavedLocation = false
        initStopWatch()

    }

    //reiniciamos la vista del usuario
    private fun resetTimeView(){
        manageEnableButtonsRun(false, true)

        //val btStart: LinearLayout = findViewById(R.id.btStart)
        //btStart.background = getDrawable(R.drawable.circle_background_toplay)
        tvChrono.setTextColor(ContextCompat.getColor(this, R.color.white))
    }
    //reiniciamos la interfaz para que vuelva a estar disponible todos los componentes progress bar, etc
    private fun resetInterface(){

        fbCamara.isVisible = false

        val tvCurrentDistance: TextView = findViewById(R.id.tvCurrentDistance)
        val tvCurrentAvgSpeed: TextView = findViewById(R.id.tvCurrentAvgSpeed)
        val tvCurrentSpeed: TextView = findViewById(R.id.tvCurrentSpeed)


        tvCurrentDistance.text = "0.0"
        tvCurrentAvgSpeed.text = "0.0"
        tvCurrentSpeed.text = "0.0"


        tvDistanceRecord.setTextColor(ContextCompat.getColor(this, R.color.gray_dark))
        tvAvgSpeedRecord.setTextColor(ContextCompat.getColor(this, R.color.gray_dark))
        tvMaxSpeedRecord.setTextColor(ContextCompat.getColor(this, R.color.gray_dark))

        csbCurrentDistance.progress = 0f
        csbCurrentAvgSpeed.progress = 0f
        csbCurrentSpeed.progress = 0f
        csbCurrentMaxSpeed.progress = 0f

        val tvRounds: TextView = findViewById(R.id.tvRounds) as TextView
        tvRounds.text = getString(R.string.rounds)

        val lyChronoProgressBg = findViewById<LinearLayout>(R.id.lyChronoProgressBg)
        val lyRoundProgressBg = findViewById<LinearLayout>(R.id.lyRoundProgressBg)
        lyRoundProgressBg.setBackgroundColor(ContextCompat.getColor(this, R.color.salmon_dark))
        lyChronoProgressBg.translationX = -widthAnimations.toFloat()
        lyRoundProgressBg.translationX = -widthAnimations.toFloat()

        swIntervalMode.isClickable = true
        npDurationInterval.isEnabled = true
        csbRunWalk.isEnabled = true

        swChallenges.isClickable = true
        npChallengeDistance.isEnabled = true
        npChallengeDurationHH.isEnabled = true
        npChallengeDurationMM.isEnabled = true
        npChallengeDurationSS.isEnabled = true

    }
    //cuando se inicia el fragment se crea el mapa
    private fun createMapFragment(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }
//funcion por defecto del mapa para poner a la escucha el mapa
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID //seleccionamos el tipo de mapa
        habilitarMiUbicacion()
        map.setOnMyLocationButtonClickListener (this)
        map.setOnMyLocationClickListener(this)
        map.setOnMapLongClickListener {  mapCentered = false }
        map.setOnMapClickListener { mapCentered = false  }

        manageLocation()
        centrarMapa (init_lt ,init_ln)//centrar el mapa con las latitud y longitud

    }

    //preguntar por permisos si estan habilitados
    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            LOCATION_PERMISSION_REQ_CODE -> {
                var lyOpenerButton = findViewById<LinearLayout>(R.id.lyOpenerButton)

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    lyOpenerButton.isEnabled = true
                else{
                    var lyMap = findViewById<LinearLayout>(R.id.lyMap)
                    if (lyMap.height > 0){
                        setHeightLinearLayout(lyMap, 0)

                        var lyFragmentMap = findViewById<LinearLayout>(R.id.lyFragmentMap)
                        lyFragmentMap.translationY= -300f

                        var ivOpenClose = findViewById<ImageView>(R.id.ivOpenClose)
                        ivOpenClose.setRotation(0f)
                    }

                    lyOpenerButton.isEnabled = false

                }
            }
        }
    }
//habilitamos nuestra ubicacion
    private fun habilitarMiUbicacion(){
        if (!::map.isInitialized) return //salimos si el mapa no esta habilitado
    //verificar si los dos permisos estan aprobados
        if (ActivityCompat.checkSelfPermission(  this,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLocation()
            return
        }
        else map.isMyLocationEnabled = true

    }
    //funcion para centrar el mampa
    private fun centrarMapa(lt: Double, ln: Double){
        val posMap = LatLng(lt, ln) //metodo donde pasamos latitud y longitud
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(posMap, 16f), 1000, null)//indicamos la animacion del mapa, zoom, 1 segundo de tiempo

    }

    fun seleccionarBicicleta(v: View){
        if (timeInSeconds.toInt() == 0) seleccionarDeporte("Bicicleta")
    }

    fun seleccionarCorrer(v: View){
        if (timeInSeconds.toInt() == 0) seleccionarDeporte("Carrera")
    }

   private fun seleccionarDeporte(deporte: String){

        deporteSeleccionado = deporte

        var lySportBike = findViewById<LinearLayout>(R.id.lySportBike)
        var lySportRunning = findViewById<LinearLayout>(R.id.lySportRunning)

        when (deporte) {
            "Bicicleta" -> {
                LIMITE_DISTANCIA_ACEPTADA = LIMITE_DISTANCIA_ACEPTADA_CICLISTA

                lySportBike.setBackgroundColor(
                    ContextCompat.getColor(
                        mainContext,
                        R.color.verde_oscuro
                    )
                )
                lySportRunning.setBackgroundColor(
                    ContextCompat.getColor(
                        mainContext,
                        R.color.gray_medium
                    )
                )

                nivelDeporteSeleccionado = nivelBicicleta
                totalesDeporteSeleccionado = totalesBicileta
            }

            "Carrera" -> {
                LIMITE_DISTANCIA_ACEPTADA = LIMITE_DISTANCIA_ACEPTADA_CORREDOR

                lySportBike.setBackgroundColor(
                    ContextCompat.getColor(
                        mainContext,
                        R.color.gray_medium
                    )
                )
                lySportRunning.setBackgroundColor(
                    ContextCompat.getColor(
                        mainContext,
                        R.color.verde_oscuro
                    )
                )

                nivelDeporteSeleccionado = nivelCorriendo
                totalesDeporteSeleccionado = carrerasTotales
            }
        }

        refrescarCBSDeporte()
        refrescarRecords()
    }

//refrescar los records que va obteniendo el usuario
    private fun refrescarRecords(){
        if (totalesDeporteSeleccionado.recordDistancia!! > 0)
            tvDistanceRecord.text = totalesDeporteSeleccionado.recordDistancia.toString()
        else
            tvDistanceRecord.text = ""
        if (totalesDeporteSeleccionado.recordVelocidadPromedio!! > 0)
            tvAvgSpeedRecord.text = totalesDeporteSeleccionado.recordVelocidadPromedio.toString()
        else
            tvAvgSpeedRecord.text = ""
        if (totalesDeporteSeleccionado.recordVelocidad!! > 0)
            tvMaxSpeedRecord.text = totalesDeporteSeleccionado.recordVelocidad.toString()
        else
            tvMaxSpeedRecord.text = ""
    }

//refrescar los circulos para saber los valores que el usuario ha generado
    private fun refrescarCBSDeporte() {
        csbRecordDistance.max = totalesDeporteSeleccionado.recordDistancia?.toFloat()!!
        csbRecordDistance.progress = totalesDeporteSeleccionado.recordDistancia?.toFloat()!!

        csbRecordAvgSpeed.max = totalesDeporteSeleccionado.recordVelocidadPromedio?.toFloat()!!
        csbRecordAvgSpeed.progress = totalesDeporteSeleccionado.recordVelocidadPromedio?.toFloat()!!

        csbRecordSpeed.max = totalesDeporteSeleccionado.recordVelocidad?.toFloat()!!
        csbRecordSpeed.progress = totalesDeporteSeleccionado.recordVelocidad?.toFloat()!!

        csbCurrentDistance.max = csbRecordDistance.max
        csbCurrentAvgSpeed.max = csbRecordAvgSpeed.max
        csbCurrentSpeed.max = csbRecordSpeed.max
        csbCurrentMaxSpeed.max = csbRecordSpeed.max
        csbCurrentMaxSpeed.progress = 0f


    }


    //funcion para desplegar el mapa cuando se le de click a la pestania
     fun llamarVistaMapa(v: View){
         //verificar si los permisos estan habilitados antes de mostrar el mapa
        if (allPermissionsGrantedGPS()){
            var lyMap = findViewById<LinearLayout>(R.id.lyMap)
            var lyFragmentMap = findViewById<LinearLayout>(R.id.lyFragmentMap)
            var ivOpenClose = findViewById<ImageView>(R.id.ivOpenClose)
            //verificar si el mapa esta desplegado o si necesita visualizarse
            if (lyMap.height == 0){
                setHeightLinearLayout(lyMap, 1157)
                animateViewofFloat(lyFragmentMap, "translationY", 0f, 0)
                ivOpenClose.setRotation(180f)
            }
            else{
                //ocultamos el mapa en caso de que este desplegado
                setHeightLinearLayout(lyMap, 0)
                lyFragmentMap.translationY= -300f
                ivOpenClose.setRotation(0f)
            }

        }
        else requestPermissionLocation()
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        TODO("Not yet implemented")
    }

    //funcion para crear la ventana emergente
    private fun hidePopUpRun(){
        var lyWindow = findViewById<LinearLayout>(R.id.lyWindow)
        lyWindow.translationX = 400f //sacar fuera de la pantalla el layout
        lyPopupRun = findViewById(R.id.lyPopupRun)
        lyPopupRun.isVisible = false
    }

    //funcion para mostrar la ventana cuando se finalice la carera
    private fun mostrarVentanaEmergente(){
        var rlMain = findViewById<RelativeLayout>(R.id.rlMain)
        rlMain.isEnabled = false

        lyPopupRun.isVisible = true

        var lyWindow = findViewById<LinearLayout>(R.id.lyWindow)
        ObjectAnimator.ofFloat(lyWindow, "translationX", 0f ).apply {
            duration = 200L
            start()
        }

        cargarDatosVentanaEmergente()
    }
    //cargamos los datos de la ventana emergente
    private fun cargarDatosVentanaEmergente(){
        showHeaderPopUp()
        showMedals()
        mostrarDatosCarrera()
    }
    private fun showHeaderPopUp(){

        var csbRunsLevel = findViewById<CircularSeekBar>(R.id.csbRunsLevel)
        var csbDistanceLevel = findViewById<CircularSeekBar>(R.id.csbDistanceLevel)
        var tvTotalRunsLevel = findViewById<TextView>(R.id.tvTotalRunsLevel)
        var tvTotalDistanceLevel = findViewById<TextView>(R.id.tvTotalDistanceLevel)


        var ivSportSelected = findViewById<ImageView>(R.id.ivSportSelected)
        var ivCurrentLevel = findViewById<ImageView>(R.id.ivCurrentLevel)
        var tvTotalDistance = findViewById<TextView>(R.id.tvTotalDistance)
        var tvTotalTime = findViewById<TextView>(R.id.tvTotalTime)

        when (deporteSeleccionado){
            "Bicicleta" ->{
                nivelDeporteSeleccionado = nivelBicicleta
                setNivelBicicleta()
                ivSportSelected.setImageResource(R.mipmap.bike)
            }

            "Carrera" -> {
                nivelDeporteSeleccionado = nivelCorriendo
                setNivelCarrera()
                ivSportSelected.setImageResource(R.mipmap.running)
            }
        }

        var tvNumberLevel = findViewById<TextView>(R.id.tvNumberLevel)
        var levelText = "${getString(R.string.level)} ${nivelDeporteSeleccionado.imagen!!.subSequence(6,7).toString()}"
        tvNumberLevel.text = levelText

        csbRunsLevel.max = nivelDeporteSeleccionado.objetivoCarreras!!.toFloat()
        csbRunsLevel.progress = totalesDeporteSeleccionado.totalCarreras!!.toFloat()
        if (totalesDeporteSeleccionado.totalCarreras!! > nivelDeporteSeleccionado.objetivoCarreras!!.toInt()){
            csbRunsLevel.max = nivelDeporteSeleccionado.objetivoCarreras!!.toFloat()
            csbRunsLevel.progress = csbRunsLevel.max
        }

        csbDistanceLevel.max = nivelDeporteSeleccionado.objetivoDeDistancia!!.toFloat()
        csbDistanceLevel.progress = totalesDeporteSeleccionado.totalDistancia!!.toFloat()
        if (totalesDeporteSeleccionado.totalDistancia!! > nivelDeporteSeleccionado.objetivoDeDistancia!!.toInt()){
            csbDistanceLevel.max = nivelDeporteSeleccionado.objetivoDeDistancia!!.toFloat()
            csbDistanceLevel.progress = csbDistanceLevel.max
        }

        tvTotalRunsLevel.text = "${totalesDeporteSeleccionado.totalCarreras!!}/${nivelDeporteSeleccionado.objetivoCarreras!!}"

        var td = totalesDeporteSeleccionado.totalDistancia!!
        var td_k: String = td.toString()
        if (td > 1000) td_k = (td/1000).toInt().toString() + "K"
        var ld = nivelDeporteSeleccionado.objetivoDeDistancia!!.toDouble()
        var ld_k: String = ld.toInt().toString()
        if (ld > 1000) ld_k = (ld/1000).toInt().toString() + "K"
        tvTotalDistance.text = "${td_k}/${ld_k} kms"

        var porcent = (totalesDeporteSeleccionado.totalDistancia!!.toDouble() *100 / nivelDeporteSeleccionado.objetivoDeDistancia!!.toDouble()).toInt()
        tvTotalDistanceLevel.text = "$porcent%"

        when (nivelDeporteSeleccionado.imagen){
            "level_1" -> ivCurrentLevel.setImageResource(R.drawable.tortuga)
            "level_2" -> ivCurrentLevel.setImageResource(R.drawable.liebre)
            "level_3" -> ivCurrentLevel.setImageResource(R.drawable.lobo)
            "level_4" -> ivCurrentLevel.setImageResource(R.drawable.leopardo)

        }

        var formatedTime = getFormattedTotalTime(totalesDeporteSeleccionado.totalTiempo!!.toLong())
        tvTotalTime.text = getString(R.string.PopUpTotalTime) + formatedTime
    }
    private fun showMedals(){
        var lyMedalsRun = findViewById<LinearLayout>(R.id.lyMedalsRun)

        // Hide medals block
        if(activatedGPS){
            //lyMedalsRun.visibility = View.VISIBLE
            lyMedalsRun.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT // LayoutParams: android.view.ViewGroup.LayoutParams
        }
        else {
            //lyMedalsRun.visibility = View.GONE
            lyMedalsRun.getLayoutParams().height = 0
        }
        lyMedalsRun.requestLayout() //It is necesary to refresh the screen
    }
    fun cerrarVentanaEmergente(v: View){
        cerrarVentanaCorredor()
    }
    private fun cerrarVentanaCorredor(){
        hidePopUpRun()
        var rlMain = findViewById<RelativeLayout>(R.id.rlMain)
        rlMain.isEnabled = true//habilitamos la ventana principal

        resetVariablesRun()
    }
//registramos los datos de la carrera para mostrarlos
    private fun mostrarDatosCarrera(){
        var tvDurationRun = findViewById<TextView>(R.id.tvDurationRun)
        var lyChallengeDurationRun = findViewById<LinearLayout>(R.id.lyChallengeDurationRun)
        var tvChallengeDurationRun = findViewById<TextView>(R.id.tvChallengeDurationRun)
        var lyIntervalRun = findViewById<LinearLayout>(R.id.lyIntervalRun)
        var tvIntervalRun = findViewById<TextView>(R.id.tvIntervalRun)
        var tvDistanceRun = findViewById<TextView>(R.id.tvDistanceRun)
        var lyChallengeDistancePopUp = findViewById<LinearLayout>(R.id.lyChallengeDistancePopUp)
        var tvChallengeDistanceRun = findViewById<TextView>(R.id.tvChallengeDistanceRun)
        var lyUnevennessRun = findViewById<LinearLayout>(R.id.lyUnevennessRun)
        var tvMaxUnevennessRun = findViewById<TextView>(R.id.tvMaxUnevennessRun)
        var tvMinUnevennessRun = findViewById<TextView>(R.id.tvMinUnevennessRun)
        var tvAvgSpeedRun = findViewById<TextView>(R.id.tvAvgSpeedRun)
        var tvMaxSpeedRun = findViewById<TextView>(R.id.tvMaxSpeedRun)
    var lyCurrentDatas = findViewById<LinearLayout>(R.id.lyCurrentDatas)

    if(activatedGPS){
        //lyCurrentDatas.visibility = View.VISIBLE
        lyCurrentDatas.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT 
    }
    else {
        //lyCurrentDatas.visibility = View.GONE
        lyCurrentDatas.getLayoutParams().height = 0
    }
    lyCurrentDatas.requestLayout() //necesario para refrescar la pantalla

    tvDurationRun.setText(tvChrono.text)
    if (challengeDuration > 0){
        setHeightLinearLayout(lyChallengeDurationRun, 120)
        tvChallengeDurationRun.setText(getFormattedStopWatch((challengeDuration*1000).toLong()))
    }
    else  setHeightLinearLayout(lyChallengeDurationRun, 0)

    if (swIntervalMode.isChecked){
        setHeightLinearLayout(lyIntervalRun, 120)
        var details: String = "${npDurationInterval.value}mins. ("
        details += "${tvRunningTime.text} / ${tvWalkingTime.text})"

        tvIntervalRun.setText(details)
    }
    else setHeightLinearLayout(lyIntervalRun, 0)

    tvDistanceRun.setText(roundNumber(distance.toString(), 2))
    if (challengeDistance > 0f){
        setHeightLinearLayout(lyChallengeDistancePopUp, 120)
        tvChallengeDistanceRun.setText(challengeDistance.toString())
    }
    else setHeightLinearLayout(lyChallengeDistancePopUp, 0)

    if (maxAltitude == null) setHeightLinearLayout(lyUnevennessRun, 0)
    else{
        setHeightLinearLayout(lyUnevennessRun, 120)
        tvMaxUnevennessRun.setText(maxAltitude!!.toInt().toString())
        tvMinUnevennessRun.setText(minAltitude!!.toInt().toString())
    }

    tvAvgSpeedRun.setText(roundNumber(avgSpeed.toString(), 1))
    tvMaxSpeedRun.setText(roundNumber(maxSpeed.toString(), 1))

    }

    fun tomarFoto(v: View) {
        val intent = Intent(this, Camara::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("dateRun", dateRun)
            putExtra("startTimeRun", startTimeRun)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_run -> {
                Toast.makeText(this, "Correr seleccionado", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_history -> {
                Toast.makeText(this, "Historial seleccionado", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_premium -> {
                Toast.makeText(this, "Hazte Premium seleccionado", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_clear_prefs -> {
                Toast.makeText(this, "Borrar preferencias", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_logout -> {
                singOut()
                return true
            }
            R.id.action_ad_settings -> {
                Toast.makeText(this, "Configuración de anuncios", Toast.LENGTH_SHORT).show()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }



}