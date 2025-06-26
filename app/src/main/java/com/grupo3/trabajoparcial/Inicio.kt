package com.grupo3.trabajoparcial

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Inicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnNuevoTicket = findViewById<Button>(R.id.btnNuevoTicket)
        btnNuevoTicket.setOnClickListener{
            var intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }


        val VerDetalles = findViewById<TextView>(R.id.txtVerDetalles)
        VerDetalles.setOnClickListener{
            var intent = Intent(this, Detalles::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_inicio -> {
                var intent = Intent(this, Inicio::class.java)
                startActivity(intent)
            }
            R.id.menu_registro -> {
                var intent = Intent(this, Registro::class.java)
                startActivity(intent)
            }
            R.id.menu_cerrar -> {
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}