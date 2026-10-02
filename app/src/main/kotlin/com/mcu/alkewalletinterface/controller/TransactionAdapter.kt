package com.mcu.alkewalletinterface.controller

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcu.alkewalletinterface.R
import com.mcu.alkewalletinterface.model.Transaccion

class TransactionAdapter(
    private var transacciones: List<Transaccion>
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    fun setTransacciones(nuevasTransacciones: List<Transaccion>) {
        this.transacciones = nuevasTransacciones
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tx = transacciones[position]

        holder.txtTxDate.text = "${tx.getFechaFormateada()} - ${tx.concepto}"

        if (tx.tipo == "ENVIO") {
            holder.txtTxTitle.text = "A: ${tx.nombreContraparte}"
            holder.txtTxAmount.text = String.format("-$%.2f", tx.monto)
            holder.txtTxAmount.setTextColor(Color.parseColor("#3A404A"))
        } else {
            holder.txtTxTitle.text = "De: ${tx.nombreContraparte}"
            holder.txtTxAmount.text = String.format("+$%.2f", tx.monto)
            holder.txtTxAmount.setTextColor(Color.parseColor("#4CAF50"))
        }
    }

    override fun getItemCount(): Int = transacciones.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTxTitle: TextView = itemView.findViewById(R.id.txtTxTitle)
        val txtTxDate: TextView = itemView.findViewById(R.id.txtTxDate)
        val txtTxAmount: TextView = itemView.findViewById(R.id.txtTxAmount)
    }
}
