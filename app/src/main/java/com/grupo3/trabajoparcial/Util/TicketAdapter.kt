package com.grupo3.trabajoparcial.Util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.grupo3.trabajoparcial.Model.Ticket
import com.grupo3.trabajoparcial.R
import com.grupo3.trabajoparcial.TecnicoActivity
import org.json.JSONObject

class TicketAdapter(private val tickets: MutableList<Ticket>, private val rolUsuario: String)
    : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtId: TextView = itemView.findViewById(R.id.txtTicketId)
        val txtEstadoFecha: TextView = itemView.findViewById(R.id.txtEstado)
        val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        val txtVerDetalles: TextView = itemView.findViewById(R.id.txtVerDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.txtId.text = "#${ticket.id_ticket}"
        holder.txtEstadoFecha.text = "${ticket.estado} - ${formatearFecha(ticket.fecha)}"
        holder.txtCategoria.text = "🖥 CATEGORÍA: ${ticket.categoria}"
        holder.txtVerDetalles.setOnClickListener {

            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ticket_detalle, null)
            val bottomSheetDialog = BottomSheetDialog(context)
            bottomSheetDialog.setContentView(dialogView)

            dialogView.findViewById<TextView>(R.id.txtDetalleCategoria).text = "Categoría: ${ticket.categoria}"
            dialogView.findViewById<TextView>(R.id.txtDetalleEstado).text = "Estado: ${ticket.estado}"
            dialogView.findViewById<TextView>(R.id.txtDetalleFecha).text = "Fecha: ${formatearFecha(ticket.fecha)}"
            dialogView.findViewById<TextView>(R.id.txtDetalleDescripcion).text = ticket.descripcion

            val btnCambiarEstado = dialogView.findViewById<Button>(R.id.btnCambiarEstado)
            val spinnerEstados = dialogView.findViewById<Spinner>(R.id.spinnerCambiarEstado)

            if (rolUsuario == "Tecnico") {
                btnCambiarEstado.visibility = View.VISIBLE
                spinnerEstados.visibility = View.VISIBLE
            } else {
                btnCambiarEstado.visibility = View.GONE
                spinnerEstados.visibility = View.GONE
            }


            val estados = listOf("Pendiente", "En curso", "Finalizado")
            val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, estados)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEstados.adapter = spinnerAdapter
            spinnerEstados.setSelection(estados.indexOf(ticket.estado))

            // Botón cambiar estado
            dialogView.findViewById<Button>(R.id.btnCambiarEstado).setOnClickListener {
                val nuevoEstado = spinnerEstados.selectedItem.toString()
                // Llama función para actualizar el estado en backend
                actualizarEstadoTicket(context, ticket.id_ticket, nuevoEstado) {
                    bottomSheetDialog.dismiss()
                    // Opcional: recarga la lista en la actividad principal
                    if (context is TecnicoActivity) {
                        context.cargarTickets()
                    }
                }
            }

            bottomSheetDialog.show()
        }
    }

    fun actualizarEstadoTicket(
        context: Context,
        idTicket: Int,
        nuevoEstado: String,
        onSuccess: () -> Unit
    ) {
        val url = "https://2zyejm3aj1.execute-api.us-east-1.amazonaws.com/v1/tickets"
        val jsonBody = JSONObject()
        jsonBody.put("IdTicket", idTicket)
        jsonBody.put("NuevoEstado", nuevoEstado)

        Log.i("ACTUALIZA_ESTADO", "JSON enviado: $jsonBody")

        val request = JsonObjectRequest(
            Request.Method.PATCH, url, jsonBody,

            { response ->
                Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show()
                onSuccess()
            },
            { error ->
                Toast.makeText(context, "Error al actualizar estado: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        Volley.newRequestQueue(context).add(request)
    }


    override fun getItemCount(): Int = tickets.size

    fun updateData(newTickets: List<Ticket>) {
        tickets.clear()
        tickets.addAll(newTickets)
        notifyDataSetChanged()
    }

    fun formatearFecha(fechaOriginal: String): String {
        return try {
            val formatoEntrada = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", java.util.Locale.getDefault())
            val formatoSalida = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val fecha = formatoEntrada.parse(fechaOriginal)
            formatoSalida.format(fecha!!)
        } catch (e: Exception) {
            fechaOriginal // Si hay error, muestra la original
        }
    }

}

