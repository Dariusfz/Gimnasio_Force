package com.example.gimasio_force

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_record)
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_record)// declaramos el toolbar creado
        setSupportActionBar(toolbar)

        toolbar.title = getString(R.string.bar_title_record) // establecememos un titulo al menu accediedo de los recursos string
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.gray_dark)) //establecemos un color al titulo
        toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_light))// color de fondo

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

    }


    //ir hacia atras cuando el usuario presione el boton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


     //inflamos el recurso de order by al menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_records_by, menu)

        return true
    }
    //capturar la seleccion del usuario en el menu de records
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.orderby_date ->{
                if(item.title==getString(R.string.orderby_dateZA)){
                    item.title= getString(R.string.orderby_dateAZ)
                }else{
                    item.title= getString(R.string.orderby_dateZA)
                }
                return true
            }
            R.id.orderby_duration ->{
                var opcion = getString(R.string.orderby_durationZA)
                if(item.title==getString(R.string.orderby_durationZA)){
                    item.title= getString(R.string.orderby_durationAZ)
                }else{
                    item.title= getString(R.string.orderby_durationZA)
                }
                return true
            }
            R.id.orderby_distance ->{
                var opcion = getString(R.string.orderby_distanceZA)
                if(item.title==opcion){
                    item.title= getString(R.string.orderby_distanceAZ)
                }else{
                    item.title= getString(R.string.orderby_distanceZA)
                }
                return true
            }
            R.id.orderby_avgspeed ->{
                var opcion = getString(R.string.orderby_avgspeedZA)
                if(item.title==getString(R.string.orderby_avgspeedZA)){
                    item.title= getString(R.string.orderby_avgspeedAZ)
                }else{
                    item.title= getString(R.string.orderby_avgspeedZA)
                }
                return true
            }

            R.id.orderby_maxspeed ->{
                var opcion = getString(R.string.orderby_maxspeedZA)
                if(item.title==getString(R.string.orderby_maxspeedZA)){
                    item.title= getString(R.string.orderby_maxspeedAZ)
                }else{
                    item.title= getString(R.string.orderby_maxspeedZA)
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun callHome(v: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }




}