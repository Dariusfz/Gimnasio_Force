plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.gimasio_force"
    compileSdk = 35

    buildFeatures{
        viewBinding = true
    }
    dataBinding{
        enable=true
    }

    defaultConfig {
        applicationId = "com.example.gimasio_force"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.androidx.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation (libs.firebase.auth)
//implementation 'com.google.firebase:firebase-analytics'
    implementation (libs.firebase.database)
    implementation (libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    // Import the BoM for the Firebase platform
    implementation(libs.firebase.bom)


    implementation(libs.google.firebase.auth)
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")


    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    //librerias para personalizar circulos y progress bar
    implementation ("me.tankery.lib:circularSeekBar:1.3.2")
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.3.0")

    //geolocalizacion
    implementation ("com.google.android.gms:play-services-location:19.0.1")
    implementation ("com.google.android.gms:play-services-maps:19.2.0")

    //camara
    implementation ("androidx.camera:camera-view:1.0.0-alpha23")
    implementation("androidx.camera:camera-core:1.0.1")
    implementation("androidx.camera:camera-camera2:1.0.1")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")



}