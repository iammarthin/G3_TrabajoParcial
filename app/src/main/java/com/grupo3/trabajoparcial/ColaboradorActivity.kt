package com.grupo3.trabajoparcial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.grupo3.trabajoparcial.Model.Ticket
import com.grupo3.trabajoparcial.Util.TicketAdapter
import com.grupo3.trabajoparcial.api.ApiService
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ColaboradorActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_colaborador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fechaActual = Calendar.getInstance().time
        val formato = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "PE"))
        val fechaFormateada = "Hoy es ${formato.format(fechaActual)}"


        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        txtFecha.text = fechaFormateada


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Obtener nombre del usuario desde el intent
        val nombreUsuario = intent.getStringExtra("nombre_usuario") ?: "Usuario"
        supportActionBar?.title = "Hola, $nombreUsuario 👋"



    }

    fun registrar(view: View) {
        var intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("WrongViewCast")
    fun buscar(view: View) {

        val idUsuario = intent.getIntExtra("id_usuario", 0)
        val spinner = findViewById<Spinner>(R.id.spinnerFiltro)

        val url = "https://t46hw8syf0.execute-api.us-east-1.amazonaws.com/v1/productos?IdUsuario=$idUsuario&Estado=$spinner"

        val stringRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            {
                    response ->
                try {
                    val jsonArray = response.getJSONArray("data")
                    Log.i("=====>", jsonArray.toString())

                    val items = mutableListOf<String>()
                    for (i in 0 until jsonArray.length()) {
                        val producto = jsonArray.getJSONObject(i)
                        items.add("${producto.getString("categoria")} - ${producto.getString("nombre")}")
                    }

                    val lstProductos = findViewById<ListView>(R.id.lstTickets)
                    val adaptador = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        items
                    )
                    lstProductos.adapter = adaptador
                } catch (e: JSONException) {
                    Log.i("=====>", e.message.toString())
                }
            },
            {
                    error ->
                Log.i("=====>", error.toString())
            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

   /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_inicio -> {
                var intent = Intent(this, ColaboradorActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_registro -> {
                var intent = Intent(this, RegistroActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_cerrar -> {
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
}