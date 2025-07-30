plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.gimasio_force"
    compileSdk = 35

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("androidx.core:core-ktx:1.16.0")

    // Import the Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))  // Usa la última versión estable

// Declare the dependencies for Firebase without version numbers
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")  // Si la necesitas

    // Import the Firebase BoM
 /*   implementation(platform("com.google.firebase:firebase-bom:29.2.0"))
    implementation ("com.google.firebase:firebase-auth:21.0.2")
//implementation 'com.google.firebase:firebase-analytics'
    implementation ("com.google.firebase:firebase-database:20.0.4")


    implementation(libs.firebase.analytics)
    // Import the BoM for the Firebase platform
   // implementation(libs.firebase.bom)


    implementation(libs.google.firebase.auth)
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.1")*/
    implementation ("com.google.android.gms:play-services-auth:20.1.0")
    implementation ("com.google.firebase:firebase-firestore:24.0.2")
    implementation ("com.google.firebase:firebase-appcheck")


    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    //librerias para personalizar circulos y progress bar
    implementation ("me.tankery.lib:circularSeekBar:1.3.2")
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.3.0")

    //geolocalizacion
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.android.gms:play-services-maps:19.2.0")



}