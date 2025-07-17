package com.grupo3.trabajoparcial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
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
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.grupo3.trabajoparcial.Model.Categoria


class ColaboradorActivity : AppCompatActivity() {

    private lateinit var adapter: TicketAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner

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

        val btnAgregar = findViewById<FloatingActionButton>(R.id.fabNuevoTicket)
        btnAgregar.setOnClickListener {
            mostrarDialogoNuevoTicket()
        }


        val fechaActual = Calendar.getInstance().time
        val formato = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "PE"))
        val fechaFormateada = "Hoy es ${formato.format(fechaActual)}"


        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        txtFecha.text = fechaFormateada


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val nombreUsuario = intent.getStringExtra("nombre_usuario") ?: "Usuario"
        supportActionBar?.title = "Hola, $nombreUsuario 👋"

        val rolUsuario = intent.getStringExtra("rol_usuario") ?: "usuario"

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

        // Cuando el usuario cambia el filtro, carga tickets con el nuevo estado
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                cargarTickets()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }



    }

    private fun cargarTickets() {
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

    private fun mostrarDialogoNuevoTicket() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_ticket, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogView)

        val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
        val editDescripcion = dialogView.findViewById<EditText>(R.id.editDescripcion)
        val btnRegistrar = dialogView.findViewById<Button>(R.id.btnRegistrar)

        val categorias = mutableListOf<Categoria>()
        val nombresCategorias = mutableListOf<String>()

        val urlCategorias = "https://2zyejm3aj1.execute-api.us-east-1.amazonaws.com/v1/categorias"

        val request = JsonObjectRequest(
            Request.Method.GET, urlCategorias, null,
            { response ->
                if (response.has("data")) {
                    val array = response.getJSONArray("data")
                    categorias.clear()
                    nombresCategorias.clear()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        categorias.add(
                            Categoria(
                                id = obj.getInt("IdCategoria"),
                                nombre = obj.getString("Nombre"),
                                descripcion = obj.getString("Descripcion")
                            )
                        )
                        nombresCategorias.add(obj.getString("Nombre"))
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresCategorias)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategoria.adapter = adapter
                }
            },
            { error ->
                Toast.makeText(this, "Error cargando categorías", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)

        btnRegistrar.setOnClickListener {
            val posicion = spinnerCategoria.selectedItemPosition
            if (posicion == AdapterView.INVALID_POSITION) {
                Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val idCategoria = categorias[posicion].id
            val categoria = categorias[posicion].nombre
            val descripcion = editDescripcion.text.toString().trim()

            if (descripcion.isEmpty()) {
                Toast.makeText(this, "Completa la descripción", Toast.LENGTH_SHORT).show()
            } else {
                bottomSheetDialog.dismiss()
                registrarNuevoTicket(idCategoria, descripcion, categoria)
            }
        }

        bottomSheetDialog.show()
    }


    private fun registrarNuevoTicket(idCategoria: Int, descripcion: String, categoria: String) {
        val idUsuario = intent.getIntExtra("id_usuario", 0)

        val url = "https://2zyejm3aj1.execute-api.us-east-1.amazonaws.com/v1/tickets"

        val jsonObject = JSONObject()
        try {
            jsonObject.put("IdUsuario", idUsuario)
            jsonObject.put("IdCategoria", idCategoria)
            jsonObject.put("Descripcion", descripcion)
            jsonObject.put("Categoria", categoria)


            val jsonObjReq = object : JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                Response.Listener {
                        response -> Log.i("=====>", "exito")
                                    Toast.makeText(this, "Ticket registrado exitosamente", Toast.LENGTH_SHORT).show()
                                    cargarTickets()
                                    Log.i("REGISTRO_TICKET", "Respuesta: $jsonObject")
                },
                Response.ErrorListener {
                        error: VolleyError -> Log.i("=====>", error.message.toString())
                }
            ) {}

            val requestQueue= Volley.newRequestQueue(this)
            requestQueue.add(jsonObjReq)
        } catch (e: JSONException) {
            Log.i("=====>", e.message.toString())
        }

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