package com.example.gimasio_force

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gimasio_force.LoginActivity.Companion.useremail
import com.example.gimasio_force.MainActivity.Companion.mainContext
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RecordActivity : AppCompatActivity() {

    private var deporteSeleccionado : String = "Carrera"
    private lateinit var ivBike : ImageView
    private lateinit var ivRunning: ImageView

    private lateinit var recyclerView: RecyclerView
    private lateinit var runsArrayList : ArrayList<Carreras>
    private lateinit var myAdapter: RunsAdapter
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

        ivBike = findViewById(R.id.ivBike)
        ivRunning = findViewById(R.id.ivRunning)

        recyclerView = findViewById(R.id.rvRecords)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        runsArrayList = arrayListOf()
        myAdapter = RunsAdapter(runsArrayList)
        recyclerView.adapter = myAdapter

    }


    //ir hacia atras cuando el usuario presione el boton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadRecyclerView("fecha", Query.Direction.DESCENDING)
    }
    override fun onPause() {
        super.onPause()
        runsArrayList.clear()
    }


     //inflamos el recurso de order by al menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_records_by, menu)

        return true
    }
    //capturar la seleccion del usuario en el menu de records
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        var order: Query.Direction = Query.Direction.DESCENDING

        when (item.itemId){
            R.id.orderby_date -> {
                if (item.title == getString(R.string.orderby_dateZA)){
                    item.title = getString(R.string.orderby_dateAZ)
                    order = Query.Direction.DESCENDING
                }
                else{
                    item.title = getString(R.string.orderby_dateZA)
                    order = Query.Direction.ASCENDING
                }
                loadRecyclerView("fecha", order)
                return true
            }
            R.id.orderby_duration ->{
                var option = getString(R.string.orderby_durationZA)
                if (item.title == getString(R.string.orderby_durationZA)){
                    item.title = getString(R.string.orderby_durationAZ)
                    order = Query.Direction.DESCENDING
                }
                else{
                    item.title = getString(R.string.orderby_durationZA)
                    order = Query.Direction.ASCENDING
                }
                loadRecyclerView("duracion", order)
                return true
            }

            R.id.orderby_distance ->{
                var option = getString(R.string.orderby_distanceZA)
                if (item.title == option){
                    item.title = getString(R.string.orderby_distanceAZ)
                    order = Query.Direction.ASCENDING
                }
                else{
                    item.title = getString(R.string.orderby_distanceZA)
                    order = Query.Direction.DESCENDING
                }
                loadRecyclerView("distancia", order)
                return true
            }

            R.id.orderby_avgspeed ->{
                var option = getString(R.string.orderby_avgspeedZA)
                if (item.title == getString(R.string.orderby_avgspeedZA)){
                    item.title = getString(R.string.orderby_avgspeedAZ)
                    order = Query.Direction.ASCENDING
                }
                else{
                    item.title = getString(R.string.orderby_avgspeedZA)
                    order = Query.Direction.DESCENDING
                }
                loadRecyclerView("avgSpeed", order)
                return true
            }

            R.id.orderby_maxspeed ->{
                var option = getString(R.string.orderby_maxspeedZA)
                if (item.title == getString(R.string.orderby_maxspeedZA)){
                    item.title = getString(R.string.orderby_maxspeedAZ)
                    order = Query.Direction.ASCENDING
                }
                else{
                    item.title = getString(R.string.orderby_maxspeedZA)
                    order = Query.Direction.DESCENDING
                }
                loadRecyclerView("maxSpeed", order)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun callHome(v: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun loadRunsBike(v: View){
        deporteSeleccionado = "Bicicleta"
        ivBike.setBackgroundColor(ContextCompat.getColor(mainContext, R.color.verde_oscuro))

        ivRunning.setBackgroundColor(ContextCompat.getColor(mainContext, R.color.gray_medium))

        loadRecyclerView("fecha", Query.Direction.DESCENDING)
    }

    fun loadRunsRunning(v: View){
        deporteSeleccionado = "Carrera"
        ivBike.setBackgroundColor(ContextCompat.getColor(mainContext, R.color.gray_medium))

        ivRunning.setBackgroundColor(ContextCompat.getColor(mainContext, R.color.verde_oscuro))

        loadRecyclerView("fecha", Query.Direction.DESCENDING)
    }

    private fun loadRecyclerView(field: String, order: Query.Direction){
        runsArrayList.clear()

        var dbRuns = FirebaseFirestore.getInstance()
        dbRuns.collection("carreras$deporteSeleccionado").orderBy(field, order)
            .whereEqualTo("usuario", useremail)
            .get()
            .addOnSuccessListener { documents ->
                for (run in documents)
                    runsArrayList.add(run.toObject(Carreras::class.java))

                myAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents WHERE EQUAL TO: ", exception)
            }

    }




}