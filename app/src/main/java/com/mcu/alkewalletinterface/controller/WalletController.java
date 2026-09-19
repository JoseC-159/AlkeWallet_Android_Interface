package com.mcu.alkewalletinterface.controller;

import com.mcu.alkewalletinterface.model.Cuenta;
import com.mcu.alkewalletinterface.model.Usuario;
import com.mcu.alkewalletinterface.model.Transaccion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletController {

    private static final List<Usuario> usuarios = new ArrayList<>();
    public static List<Cuenta> cuentas = new ArrayList<>();

    public void inicializarDatosPrueba() {
        if (usuarios.isEmpty()) {
            Usuario usuarioPrueba = new Usuario(generarId(), "Usuario Prueba", "example@mail.com", "1234", 54);
            Cuenta cuentaPrueba = new Cuenta(usuarioPrueba, 50000.0, "CLP");

            usuarios.add(usuarioPrueba);
            cuentas.add(cuentaPrueba);
        }
    }

    private static int contadorUsuarios = 0;

    private static int transaccionIdCounter = 1;
    private static boolean verifUsuario = false;
    private static Usuario usuarioActivo;
    public static Cuenta cuentaActiva;

    private static final Map<String, Double> tasasCambio = new HashMap<>();

    static {
        tasasCambio.put("USD", 1.0);
        tasasCambio.put("CLP", 965.50);
        tasasCambio.put("JPY", 148.72);
        tasasCambio.put("RUB", 89.15);
        tasasCambio.put("EUR", 0.9185);
    }

    public int generarId() {
        contadorUsuarios += 1;
        return contadorUsuarios;
    }

    public static Cuenta obtenerCuentaActiva() {
        return cuentaActiva;
    }

    public Usuario obtenerUsuarioActivo() {
        return usuarioActivo;
    }

    //Función pensada para futuras entregas
    public void crearCuenta(Usuario nuevoUsuario) {
        Cuenta nuevaCuenta = new Cuenta(nuevoUsuario, 0, "CLP");
        usuarios.add(nuevoUsuario);
        cuentas.add(nuevaCuenta);
    }

    public boolean iniciarSesion(String usuario, String contrasenia) {
        for (Usuario user : usuarios) {
            if (user.obtenerNombre().equals(usuario) && user.obtenerContraseña().equals(contrasenia)) {
                verifUsuario = true;
                usuarioActivo = user;
                cuentaActiva = buscarCuentaPorUsuario(user);
                return true;
            }
        }
        return false;
    }

    public void cerrarSesion() {
        verifUsuario = false;
        usuarioActivo = null;
        cuentaActiva = null;
    }

    public Cuenta buscarCuentaPorUsuario(Usuario usuario) {
        for (Cuenta account : cuentas) {
            if (account.obtenerUsuario().equals(usuario)) {
                cuentaActiva = account;
                return account;
            }
        }
        return null;
    }

    public double mostrarBalance() {
        return (cuentaActiva != null) ? cuentaActiva.obtenerBalance() : 0.0;
    }

    public boolean enviarDinero(double monto, Cuenta cuentaDestino, String concepto) {
        Cuenta cuentaActiva = obtenerCuentaActiva();

        // Verificar fondos suficientes y que la cuenta exista
        if (cuentaActiva != null && cuentaDestino != null && cuentaActiva.obtenerBalance() >= monto) {

            // Descontar y registrar en cuenta ORIGEN
            cuentaActiva.setBalance(cuentaActiva.obtenerBalance() - monto);
            Transaccion txEnvio = new Transaccion(
                    transaccionIdCounter++,
                    "ENVIO",
                    monto,
                    concepto,
                    cuentaDestino.obtenerUsuario().obtenerNombre()
            );
            cuentaActiva.agregarTransaccion(txEnvio);

            // Acreditar y registrar en cuenta 'DESTINO'
            cuentaDestino.setBalance(cuentaDestino.obtenerBalance() + monto);
            Transaccion txRecepcion = new Transaccion(
                    transaccionIdCounter++,
                    "RECEPCION",
                    monto,
                    concepto,
                    cuentaActiva.obtenerUsuario().obtenerNombre()
            );
            cuentaDestino.agregarTransaccion(txRecepcion);

            return true;
        }
        return false; // Fondos insuficientes o cuenta nula
    }

    public boolean recibirDinero(double monto, String concepto) {
        Cuenta cuentaActiva = obtenerCuentaActiva();
        if (cuentaActiva != null) {
            cuentaActiva.setBalance(cuentaActiva.obtenerBalance() + monto);

            // Crear registro
            String notaFinal = (concepto != null && !concepto.isEmpty()) ? concepto : "Ingreso a billetera";
            Transaccion nuevaTx = new Transaccion(
                    transaccionIdCounter++,
                    "INGRESO",
                    monto,
                    notaFinal,
                    "Cuenta Propia"
            );

            // Guardar en el historial de la cuenta
            cuentaActiva.agregarTransaccion(nuevaTx);
            return true;
        }
        return false;
    }

    public void crearSesionDePrueba() {
        usuarios.clear();
        cuentas.clear();

        Usuario usuarioPrueba = new Usuario(1, "José", "jose@ejemplo.cl", "123456", 25);
        Cuenta cuentaPrueba = new Cuenta(usuarioPrueba, 50000.0, "CLP");

        usuarioActivo = usuarioPrueba;
        cuentaActiva = cuentaPrueba;

        usuarios.add(usuarioPrueba);
        cuentas.add(cuentaPrueba);

        Usuario amigo = new Usuario(2, "Carlos", "carlos@mail.com", "123456", 42);
        Cuenta cuentaAmigo = new Cuenta(amigo, 10000.0, "CLP");

        usuarios.add(amigo);
        cuentas.add(cuentaAmigo);

        Usuario amiga = new Usuario(3, "María", "maria@mail.com", "123456", 30);
        Cuenta cuentaAmiga = new Cuenta(amiga, 25000.0, "CLP");

        usuarios.add(amiga);
        cuentas.add(cuentaAmiga);
    }

    public static List<Cuenta> obtenerCuentasDisponiblesParaEnvio() {
        List<Cuenta> disponibles = new ArrayList<>();
        Cuenta cuentaActiva = obtenerCuentaActiva();

        for (Cuenta c : cuentas) {
            // Añadir todas las cuentas (excepto la del usuario activo)
            if (cuentaActiva == null || c.obtenerUsuario().obtenerId() != cuentaActiva.obtenerUsuario().obtenerId()) {
                disponibles.add(c);
            }
        }
        return disponibles;
    }

    public boolean cambiarMoneda(String nuevaMoneda) {
        if (cuentaActiva == null) return false;

        String monedaActual = cuentaActiva.obtenerTipoMoneda();
        if (monedaActual.equalsIgnoreCase(nuevaMoneda)) return false;

        double saldoActual = cuentaActiva.obtenerSaldo();
        double nuevoSaldo = saldoActual;

        // Tasas de cambio simulada (respecto al CLP)
        double tasaUSD = 955.0;
        double tasaEUR = 1000.0;

        // Convertir entero a CLP primero (como moneda base) para facilitar el cálculo
        double saldoEnCLP = saldoActual;
        if (monedaActual.equalsIgnoreCase("USD")) {
            saldoEnCLP = saldoActual * tasaUSD;
        } else if (monedaActual.equalsIgnoreCase("EUR")) {
            saldoEnCLP = saldoActual * tasaEUR;
        }

        // Convertir de CLP a la moneda solicitada
        if (nuevaMoneda.equalsIgnoreCase("CLP")) {
            nuevoSaldo = saldoEnCLP;
        } else if (nuevaMoneda.equalsIgnoreCase("USD")) {
            nuevoSaldo = saldoEnCLP / tasaUSD;
        } else if (nuevaMoneda.equalsIgnoreCase("EUR")) {
            nuevoSaldo = saldoEnCLP / tasaEUR;
        }

        cuentaActiva.setSaldo(nuevoSaldo);
        cuentaActiva.setTipoMoneda(nuevaMoneda);
        return true;
    }
}
