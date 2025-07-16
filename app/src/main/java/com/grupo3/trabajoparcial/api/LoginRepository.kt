package com.grupo3.trabajoparcial.api

import android.content.Context
import com.grupo3.trabajoparcial.Model.Usuario

class LoginRepository {
    fun autenticar(context: Context, usuario: String, clave: String, callback: (Boolean, Usuario?) -> Unit) {
        ApiService.login(context, usuario, clave, callback)
    }
}
