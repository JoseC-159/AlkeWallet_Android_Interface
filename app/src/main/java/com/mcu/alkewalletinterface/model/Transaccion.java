package com.mcu.alkewalletinterface.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaccion {
    private int id;
    private String tipo; // Ej: "INGRESO", "ENVIO", "RECEPCION"
    private double monto;
    private String concepto;
    private Date fecha;
    private String nombreContraparte; // A quién se envía o de quién se recibe

    public Transaccion(int id, String tipo, double monto, String concepto, String nombreContraparte) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.concepto = concepto;
        this.nombreContraparte = nombreContraparte;
        this.fecha = new Date();
    }

    // Getters
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public String getConcepto() { return concepto; }
    public String getNombreContraparte() { return nombreContraparte; }

    // Método útil para mostrar la fecha formateada en la interfaz
    public String getFechaFormateada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(fecha);
    }
}