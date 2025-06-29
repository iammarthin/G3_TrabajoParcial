package com.grupo3.trabajoparcial

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnEnviarTicket = findViewById<Button>(R.id.btnEnviarTicket)
        btnEnviarTicket.setOnClickListener{
            var intent = Intent(this, DetalleActivity::class.java)
            startActivity(intent)
        }


        val categorias = arrayOf("Hardware", "Software", "Redes", "Accesos", "Otros")
        val prioridades = arrayOf("Alta", "Media", "Baja")
        val motivos = arrayOf(
            "Equipo no enciende",
            "Falla de conexión a red",
            "Problema con correo electrónico",
            "No funciona impresora",
            "Acceso denegado al sistema",
            "Solicitud de nuevo usuario",
            "Cambio de contraseña",
            "Otro"
        )

        val spinnerCategoria = findViewById<Spinner>(R.id.spCategoria)
        val spinnerPrioridad = findViewById<Spinner>(R.id.spPrioridad)
        val spinnerMotivo = findViewById<Spinner>(R.id.spMotivo)



        val adapterCategoria = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapterCategoria

        val adapterPrioridad = ArrayAdapter(this, android.R.layout.simple_spinner_item, prioridades)
        adapterPrioridad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPrioridad.adapter = adapterPrioridad

        val adapterMotivo = ArrayAdapter(this, android.R.layout.simple_spinner_item, motivos)
        adapterMotivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMotivo.adapter = adapterMotivo

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
    }


}