package com.grupo3.trabajoparcial.api

import android.content.Context
import android.util.Log
import android.widget.AdapterView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.grupo3.trabajoparcial.Model.Usuario
import org.json.JSONArray
import org.json.JSONObject

object ApiService {
    //private const val BASE_URL = "https://2zyejm3aj1.execute-api.us-east-1.amazonaws.com/v1/"


    fun login(context: Context, usuario: String, clave: String, callback: (Boolean, Usuario?) -> Unit) {
        //val url = BASE_URL + "login"

        val jsonBody = JSONObject().apply {
            put("usuario", usuario)
            put("clave", clave)
        }


        val url = "https://2zyejm3aj1.execute-api.us-east-1.amazonaws.com/v1/login?usuario=$usuario&clave=$clave"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val statusCode = response.optInt("statusCode", 500)
                    Log.d("LoginStatusCode", statusCode.toString())

                    if (statusCode == 200) {
                        val dataArray = response.getJSONArray("data")
                        val userJson = dataArray.getJSONObject(0)

                        val usuario = Usuario(
                            id = userJson.getInt("id_usuario"),
                            usuario = userJson.getString("usuario"),
                            nombre = userJson.getString("nombre"),
                            clave = "", // nunca almacenar la clave
                            rol = userJson.getString("rol")
                        )
                        callback(true, usuario)
                    } else {
                        callback(false, null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(false, null)
                }
            },
            { error ->
                error.printStackTrace()
                callback(false, null)
            }
        )

        Volley.newRequestQueue(context).add(request)
    }


}

