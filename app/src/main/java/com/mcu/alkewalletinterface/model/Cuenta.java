package com.mcu.alkewalletinterface.model;

import java.util.ArrayList;
import java.util.List;

public class Cuenta {

    private Usuario usuario;
    private double balance = 0;
    private String tipoMoneda = "CLP";

    private final List<Transaccion> historialTransacciones;

    public void depositar(double cantidad) {
        this.balance += cantidad;
    }

    public void retirar(double cantidad) {
        if (this.balance >= cantidad) {
            this.balance -= cantidad;
        } else {
            System.out.println("Saldo insuficiente.");
        }
    }

    public Cuenta( Usuario usuario, double balance, String tipoMoneda) {
        this.usuario = usuario;
        this.balance = balance;
        this.tipoMoneda = tipoMoneda;
        this.historialTransacciones = new ArrayList<>();
    }

    public Usuario obtenerUsuario() { return usuario;}
    public double obtenerBalance() { return balance;}
    public String obtenerTipoMoneda() { return tipoMoneda;}

    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
    public void setBalance(double balance) {this.balance = balance;}
    public void setTipoMoneda(String tipoMoneda) {this.tipoMoneda = tipoMoneda;}

    public void agregarTransaccion(Transaccion transaccion) {
        this.historialTransacciones.add(transaccion);
    }

    public List<Transaccion> obtenerHistorial() {
        return this.historialTransacciones;
    }

    public void setSaldo(double v) {
        this.balance = v;
    }

    public double obtenerSaldo() {
        return this.balance;
    }
}
