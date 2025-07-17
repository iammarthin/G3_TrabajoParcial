package com.grupo3.trabajoparcial

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.grupo3.trabajoparcial.Model.Ticket
import com.grupo3.trabajoparcial.Util.TicketAdapter
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TecnicoActivity : AppCompatActivity() {

    private lateinit var adapter: TicketAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tecnico)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tecnico)) { v, insets ->
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

        val rolUsuario = intent.getStringExtra("rol_usuario") ?: "usuario"
        val nombreUsuario = intent.getStringExtra("nombre_usuario") ?: "Usuario"
        supportActionBar?.title = "Hola, $nombreUsuario 👋 $rolUsuario"



        spinner = findViewById(R.id.spinnerFiltro)
        recyclerView = findViewById(R.id.recyclerViewTickets)

        adapter = TicketAdapter(mutableListOf(), rolUsuario)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val estados = listOf("Todos", "Pendiente", "En curso", "Finalizado")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        cargarTickets()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                cargarTickets()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


    }

    fun cargarTickets() {
        val idUsuario = intent.getIntExtra("id_usuario", 0)
        val estado = spinner.selectedItem.toString()
        val url = "https://2zyejm3aj1.execute-api.us-east-1.amazonaws.com/v1/tickets?IdUsuario=$idUsuario&Estado=$estado"

        val stringRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Verifica si existe el campo "data" y no es null
                    if (response.has("data") && !response.isNull("data")) {
                        val jsonArray = response.getJSONArray("data")
                        if (jsonArray.length() > 0) {
                            val items = mutableListOf<Ticket>()
                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                items.add(
                                    Ticket(
                                        id_ticket = obj.getInt("IdTicket"),
                                        estado = obj.getString("Estado"),
                                        fecha = obj.getString("FechaCreacion"),
                                        categoria = obj.getString("Categoria"),
                                        descripcion = obj.getString("Descripcion")
                                    )
                                )
                            }
                            adapter.updateData(items)
                        } else {
                            // El array está vacío
                            adapter.updateData(listOf())
                            Toast.makeText(this, "No hay tickets disponibles", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // El campo "data" no existe o es null
                        adapter.updateData(listOf())
                        Toast.makeText(this, "No se encontró información", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    adapter.updateData(listOf())
                    Toast.makeText(this, "Error en el formato de datos", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                adapter.updateData(listOf())
                Toast.makeText(this, "Error al conectar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_cerrar -> {
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}