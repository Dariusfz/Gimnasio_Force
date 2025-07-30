package com.example.gimasio_force

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.GoogleAuthProvider

import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.os.Handler
import android.os.Looper

class LoginActivity : AppCompatActivity() {

    private var RESULT_CODE_GOOGLE_SIGN_IN = 100

    private lateinit var btnFacebook: Button


    companion object{
        lateinit var useremail: String
        lateinit var providerSession: String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var lyTerms: LinearLayout

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        mAuth = FirebaseAuth.getInstance()

        lyTerms = findViewById(R.id.lyTerms)
        lyTerms.visibility = View.INVISIBLE

        btnFacebook= findViewById(R.id.btnSingFacebook)

        btnFacebook.setOnClickListener {
            mensajeFacebook()
        }

        manageButtonLogin()
        etEmail.doOnTextChanged { text, start, before, count ->  manageButtonLogin() }
        etPassword.doOnTextChanged { text, start, before, count ->  manageButtonLogin() }
    }
    //verifica si hay un usuario logeado para iniciar sesion directamente
   /* public override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null)  goHome(currentUser.email.toString(), currentUser.providerId)

    }*/




    private fun mensajeFacebook(){
        Toast.makeText(
            this,
            "Inicio de sesión con Facebook no disponible aún",
            Toast.LENGTH_SHORT
        ).show()
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            goHome(currentUser.email.toString(), currentUser.providerId)
        } else if (currentUser != null) {
            // Usuario logueado pero correo no verificado
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(
                this,
                "Por favor verifica tu correo electrónico antes de iniciar sesión",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    //controlar que al presionar el boton de atras vaya a la pantalla de inicio
    @SuppressLint("SuspiciousIndentation")
    override fun onBackPressed() {
        super.onBackPressed()
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }



    //verifcar si los campos son correctos para iniciar sesion
    private fun manageButtonLogin(){
        var tvLogin = findViewById<TextView>(R.id.tvLogin)
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        if (TextUtils.isEmpty(password) || ValidateEmail.isEmail(email) == false){

            tvLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            tvLogin.isEnabled = false
        }
        else{
            tvLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            tvLogin.isEnabled = true
        }
    }

    fun login(view: View) {
        loginUser()
    }
  /*  private fun loginUser(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful)  goHome(email, "email")
                else{
                    if (lyTerms.visibility == View.INVISIBLE) lyTerms.visibility = View.VISIBLE
                    else{
                        var cbAcept = findViewById<CheckBox>(R.id.cbAcept)
                        if (cbAcept.isChecked) register()
                    }
                }
            }

    }*/
//funcion para manejar el comportamiento del usuario
    private fun loginUser() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        mAuth.signInWithEmailAndPassword(email, password)//verificamos la autenticacion del usuario con email y contrasenia
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user?.isEmailVerified == true) {
                        goHome(email, "email")
                    } else {
                        // Correo no verificado
                        mAuth.signOut() // Cerrar sesión automáticamente
                        Toast.makeText(
                            this,
                            "Por favor verifica tu correo electrónico primero",
                            Toast.LENGTH_LONG
                        ).show()

                        // Opcional: Reenviar correo de verificación
                        enviarCorreoVerificacion()
                    }
                } else {
                    // Manejo de errores existente
                    if (lyTerms.visibility == View.INVISIBLE) {
                        lyTerms.visibility = View.VISIBLE
                    } else {
                        val cbAcept = findViewById<CheckBox>(R.id.cbAcept)
                        if (cbAcept.isChecked) register()
                    }
                }
            }
    }
//recibimos el email del usuario y el proveedor correo o inicio de sesion de google
    private fun goHome(email: String, provider: String){

        useremail = email
        providerSession = provider

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

 /*   private fun register(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){

                    var dateRegister = SimpleDateFormat("dd/MM/yyyy").format(Date())
                    var dbRegister = FirebaseFirestore.getInstance()
                    dbRegister.collection("users").document(email).set(hashMapOf(
                        "user" to email,
                        "dateRegister" to dateRegister
                    ))//crear tabla de usuarios y enviar el usuario y fecha de registro

                    goHome(email, "email")
                }
                else Toast.makeText(this, "Error, algo ha ido mal :(", Toast.LENGTH_SHORT).show()
            }
    }*/
//funcion para registrar el usuario
    private fun register() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)//creamos el usuario con correo y contrasenia
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {//si los valors son correctos mandamos un correo de verificacion
                    // Enviar correo de verificación
                    enviarCorreoVerificacion()

                    // Guardar datos del usuario en la bd
                    val dateRegister = SimpleDateFormat("dd/MM/yyyy").format(Date())
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(email)
                        .set(hashMapOf(
                            "user" to email,
                            "dateRegister" to dateRegister,
                            "emailVerified" to false // Añadir campo de verificación
                        ))
                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
//si el usuario no esta registrado se envia un correo de verificacion, revisar correo en spam ya que google asi lo reconoce
    private fun enviarCorreoVerificacion() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Se ha enviado un correo de verificación a $email",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Error al enviar correo de verificación: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
//funcion para ir a revisar los terminos y condiciones
    fun goTerms(v: View){
        val intent = Intent(this, TermsActivity::class.java)
        startActivity(intent)
    }
//llama a la funcion para restablecer contrasenia
    fun forgotPassword(view: View) {
        //startActivity(Intent(this, ForgotPasswordActivity::class.java))
        resetPassword()
    }
    private fun resetPassword(){
        var e = etEmail.text.toString()
        if (!TextUtils.isEmpty(e)){
            mAuth.sendPasswordResetEmail(e)//funcion para resetear contrasenia
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) Toast.makeText(this, "Email Enviado a $e", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this, "No se encontró el usuario con este correo", Toast.LENGTH_SHORT).show()
                }
        }
        else Toast.makeText(this, "Indica un email", Toast.LENGTH_SHORT).show()
    }
    //iniciar sesion con google
    fun callSignInGoogle (view:View){
        signInGoogle()
    }
//inicio de sesion con credenciales de google
    private fun signInGoogle(){
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        var googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        startActivityForResult(googleSignInClient.signInIntent, RESULT_CODE_GOOGLE_SIGN_IN)

    }

  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                if (account != null){
                    email = account.email!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    mAuth.signInWithCredential(credential).addOnCompleteListener{
                        if (it.isSuccessful) goHome(email, "Google")
                        else Toast.makeText(this, "Error en la conexión con Google", Toast.LENGTH_SHORT)

                    }
                }


            } catch (e: ApiException) {
                Toast.makeText(this, "Error en la conexión con Google", Toast.LENGTH_SHORT)
            }
        }

    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)!!

                if (account != null) {
                    email = account.email!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    mAuth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Con Google no necesitamos verificación de email
                            goHome(email, "Google")
                        } else {
                            Toast.makeText(this, "Error en la conexión con Google", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error en la conexión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}