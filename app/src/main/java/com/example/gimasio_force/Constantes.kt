package com.example.gimasio_force

object Constantes {
    const val INTERVAL_LOCATION= 4
    const val LIMITE_DISTANCIA_ACEPTADA_CICLISTA = 0.04 * INTERVAL_LOCATION
    //LIMIT_DISTANCE_ACCEPTED correcto sería 0.04 (40m)
    //40m son 144km/h. Un ciclista profesional en descenso pudiera alcanzar 130

    const val LIMITE_DISTANCIA_ACEPTADA_CORREDOR = 0.012 * INTERVAL_LOCATION
    //LIMIT_DISTANCE_ACCEPTED correcto sería 0.012 (12 m)
    //12m son 43.2km/h. Usain Bolt alcanza los 42km/h


    const val llave_UsuarioApp = "USUARIO_LLAVE"
    const val llave_proveedor = "PROVEEDOR_LLAVE"

    const val llave_deporteSeleccionado = "DEPORTESELECCIONADO_LLAVE"

    const val llave_modointervalo = "MODOINTERVALO_LLAVE"
    const val llave_mododuracionintervalo = "DURACIONINTERVALO_LLAVE"
    const val llave_progressCircularSeekBar = "PROGRESSCIRCULARSEEKBAR_LLAVE"
    const val llave_maxCircularSeekBar = "MAXCIRCULARSEEKBAR_LLAVE"
    const val llave_tiempocorriendo = "TIEMPOCORRIENDO_LLAVE"
    const val llave_tiempocaminando = "TIEMPOCAMINANDO_LLAVE"

    const val llave_mododesafio = "MODODESAFIO_LLAVE"
    const val llave_mododesafioduracion = "MODODESAFIODURACION_LLAVE"
    const val llave_duraciondesafioHH = "DURACIONDESAFIOHH_LLAVE"
    const val llave_duraciondesafioMM = "DURACIONDESAFIOMM_LLAVE"
    const val llave_duraciondesafioSS = "DURACIONDESAFIOSS_LLAVE"
    const val llave_mododesafiodistancia = "MODODESAFIODISTANCIA_LLAVE"
    const val llave_desafiodistancia = "DISTANCIADSAFIO_LLAVE"

    const val llave_notificacionesdesafio = "NOTIFICACIONES_LLAVE"
    const val llave_autofinalizar = "AUTOFINALIZAR_LLAVE"


    const val llave_notificacion = "NOTIFICACIONVOLUM_LLAVE"

    const val key_userApp = "USERAPP_KEY"
    const val key_provider = "PROVIDER_KEY"

    const val key_selectedSport = "SELECTEDSPORT_KEY"

    const val key_modeInterval = "MODEINTERVAL_KEY"
    const val key_intervalDuration = "INTERVALDURATION_KEY"
    const val key_progressCircularSeekBar = "PROGRESSCIRCULARSEEKBAR_KEY"
    const val key_maxCircularSeekBar = "MAXCIRCULARSEEKBAR_KEY"
    const val key_runningTime = "RUNNINGTIME_KEY"
    const val key_walkingTime = "WALKINGTIME_KEY"

    const val key_modeChallenge = "MODECHALLENGE_KEY"
    const val key_modeChallengeDuration = "MODECHALLENGEDURATION_KEY"
    const val key_challengeDurationHH = "CHALLENGEDURATIONHH_KEY"
    const val key_challengeDurationMM = "CHALLENGEDURATIONMM_KEY"
    const val key_challengeDurationSS = "CHALLENGEDURATIONSS_KEY"
    const val key_modeChallengeDistance = "MODECHALLENGEDISTANCE_KEY"
    const val key_challengeDistance = "CHALLENGEDISTANCE_KEY"

    const val key_challengeNofify = "NOTIFYCHALLENGE_KEY"
    const val key_challengeAutofinish = "AUTOFINISH_KEY"

    const val key_hardVol = "HARDVOL_KEY"
    const val key_softVol = "SOFTVOL_KEY"
    const val key_notifyVol = "MICDVOL_KEY"
}