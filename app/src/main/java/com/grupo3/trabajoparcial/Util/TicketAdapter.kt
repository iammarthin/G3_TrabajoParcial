package com.grupo3.trabajoparcial.Util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.grupo3.trabajoparcial.Model.Ticket
import com.grupo3.trabajoparcial.R

class TicketAdapter(private val tickets: List<Ticket>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtId = itemView.findViewById<TextView>(R.id.txtIdTicket)
        val txtEstadoFecha = itemView.findViewById<TextView>(R.id.txtEstadoFecha)
        val txtCategoria = itemView.findViewById<TextView>(R.id.txtCategoria)
        val txtVerDetalles = itemView.findViewById<TextView>(R.id.txtVerDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.txtId.text = "#${ticket.id_ticket}"
        holder.txtEstadoFecha.text = "${ticket.estado} - ${ticket.fecha}"
        holder.txtCategoria.text = "🖥 CATEGORÍA: ${ticket.categoria}"
        holder.txtVerDetalles.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Detalles de ticket ${ticket.id_ticket}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = tickets.size
}
