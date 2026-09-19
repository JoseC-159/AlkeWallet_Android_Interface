package com.mcu.alkewalletinterface.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mcu.alkewalletinterface.R;
import com.mcu.alkewalletinterface.model.Cuenta;
import com.mcu.alkewalletinterface.controller.WalletController;

import java.util.ArrayList;
import java.util.List;

public class SendMoneyActivity extends AppCompatActivity {


    private EditText etAmount;
    private EditText editSendNotes;
    private Spinner spinnerDestinatario;
    private Button btnSendMoney;

    private WalletController walletController;
    private List<Cuenta> cuentasDisponibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, statusBarHeight, 0, navigationBarHeight);
            return insets;
        });

        // Inicializar el controlador
        walletController = new WalletController();

        // Vincular vistas
        TextView btnBackSend = findViewById(R.id.btnBackSend);
        spinnerDestinatario = findViewById(R.id.spinnerDestinatario);
        etAmount = findViewById(R.id.etAmount);
        TextView txtAvailableBalance = findViewById(R.id.txtAvailableBalance);
        editSendNotes = findViewById(R.id.editSendNotes);
        btnSendMoney = findViewById(R.id.btnSendMoney);

        // Asignar los clics de manera limpia
        btnBackSend.setOnClickListener(v -> finish());
        btnSendMoney.setOnClickListener(v -> procesarEnvio());

        // Mostrar los datos de la cuenta activa
        Cuenta cuentaActiva = walletController.obtenerCuentaActiva();
        if (cuentaActiva != null) {
            txtAvailableBalance.setText(String.format("Saldo disponible: $%.2f %s",
                    cuentaActiva.obtenerBalance(), cuentaActiva.obtenerTipoMoneda()));
            etAmount.setHint("$ 0.00 " + cuentaActiva.obtenerTipoMoneda());
        }

        // Configurar el menú desplegable (Spinner)
        cuentasDisponibles = WalletController.obtenerCuentasDisponiblesParaEnvio();
        List<String> nombresDestinatarios = new ArrayList<>();

        if (cuentasDisponibles == null || cuentasDisponibles.isEmpty()) {
            nombresDestinatarios.add("No hay otros usuarios disponibles");
            if (btnSendMoney != null) btnSendMoney.setEnabled(false);
        } else {
            for (Cuenta c : cuentasDisponibles) {
                nombresDestinatarios.add(c.obtenerUsuario().obtenerNombre());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresDestinatarios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestinatario.setAdapter(adapter);
    }

    // método que maneja el envío de dinero
    private void procesarEnvio() {
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String notas = editSendNotes.getText() != null ? editSendNotes.getText().toString().trim() : "Transferencia";

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Ingresa un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double monto = Double.parseDouble(amountStr);

            if (cuentasDisponibles == null || cuentasDisponibles.isEmpty()) {
                Toast.makeText(this, "No hay destinatarios disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedIndex = spinnerDestinatario.getSelectedItemPosition();
            Cuenta cuentaDestino = cuentasDisponibles.get(selectedIndex);

            // Llamar a la lógica interna de la transacción
            boolean exito = walletController.enviarDinero(monto, cuentaDestino, notas);

            if (exito) {
                Toast.makeText(this, "Envío exitoso a " + cuentaDestino.obtenerUsuario().obtenerNombre(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Fondos insuficientes o error en la operación", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
        }
    }
}