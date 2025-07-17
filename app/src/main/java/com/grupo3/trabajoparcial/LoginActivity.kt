package com.grupo3.trabajoparcial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo3.trabajoparcial.api.LoginRepository

class LoginActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtOlvidoClave = findViewById<TextView>(R.id.txtOlvidoClave)
        txtOlvidoClave.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_recuperacion, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            val btnAceptar = dialogView.findViewById<Button>(R.id.btnAceptarDialog)
            btnAceptar.setOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }


        val txtClave = findViewById<EditText>(R.id.txtClave)

        txtClave.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val drawableWidth = txtClave.compoundDrawables[drawableEnd]?.bounds?.width() ?: 0

                if (event.rawX >= (txtClave.right - drawableWidth)) {
                    val isPasswordVisible = txtClave.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

                    txtClave.inputType = if (isPasswordVisible) {
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    } else {
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    }

                    txtClave.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_lock,
                        0,
                        if (isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye,
                        0
                    )

                    txtClave.setSelection(txtClave.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        val loginRepo = LoginRepository()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtUsuario = findViewById<EditText>(R.id.txtUsuario)

        btnLogin.setOnClickListener {
            val usuario = txtUsuario.text.toString()
            val clave = txtClave.text.toString()


            loginRepo.autenticar(this, usuario, clave) { success, usuario ->
                if (success && usuario != null) {
                    when (usuario.rol.lowercase()) {
                        "admin" -> startActivity(Intent(this, AdminActivity::class.java).apply {
                            putExtra("nombre_usuario", usuario.nombre)
                            putExtra("id_usuario", usuario.id)
                            putExtra("rol_usuario", usuario.rol)
                        })
                        "tecnico" -> startActivity(Intent(this, TecnicoActivity::class.java).apply {
                            putExtra("nombre_usuario", usuario.nombre)
                            putExtra("id_usuario", usuario.id)
                            putExtra("rol_usuario", usuario.rol)
                        })
                        "colaborador" -> startActivity(Intent(this, ColaboradorActivity::class.java).apply {
                            putExtra("nombre_usuario", usuario.nombre)
                            putExtra("id_usuario", usuario.id)
                            putExtra("rol_usuario", usuario.rol)
                        })
                        else -> Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "" +success, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}