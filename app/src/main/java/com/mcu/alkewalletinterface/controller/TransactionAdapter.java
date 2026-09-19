package com.mcu.alkewalletinterface.controller;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mcu.alkewalletinterface.R;
import com.mcu.alkewalletinterface.model.Transaccion;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaccion> transacciones;

    public TransactionAdapter(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }

    // Permite actualizar la lista dinámicamente cuando el usuario vuelva al Home
    public void setTransacciones(List<Transaccion> nuevasTransacciones) {
        this.transacciones = nuevasTransacciones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaccion tx = transacciones.get(position);

        holder.txtTxDate.setText(tx.getFechaFormateada() + " - " + tx.getConcepto());

        // Lógica visual dependiendo del tipo de transacción
        if (tx.getTipo().equals("ENVIO")) {
            holder.txtTxTitle.setText("A: " + tx.getNombreContraparte());
            holder.txtTxAmount.setText(String.format("-$%.2f", tx.getMonto()));
            holder.txtTxAmount.setTextColor(Color.parseColor("#3A404A")); // Oscuro para salidas
        } else {
            // INGRESO o RECEPCION
            holder.txtTxTitle.setText("De: " + tx.getNombreContraparte());
            holder.txtTxAmount.setText(String.format("+$%.2f", tx.getMonto()));
            holder.txtTxAmount.setTextColor(Color.parseColor("#4CAF50")); // Verde para entradas
        }
    }

    @Override
    public int getItemCount() {
        return transacciones == null ? 0 : transacciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTxTitle, txtTxDate, txtTxAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTxTitle = itemView.findViewById(R.id.txtTxTitle);
            txtTxDate = itemView.findViewById(R.id.txtTxDate);
            txtTxAmount = itemView.findViewById(R.id.txtTxAmount);
        }
    }
}