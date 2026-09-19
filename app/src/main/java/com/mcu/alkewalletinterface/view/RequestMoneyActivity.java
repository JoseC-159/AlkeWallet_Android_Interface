package com.mcu.alkewalletinterface.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.mcu.alkewalletinterface.R;
import com.mcu.alkewalletinterface.controller.WalletController;
import com.mcu.alkewalletinterface.model.Cuenta;
import com.mcu.alkewalletinterface.model.Usuario;

public class RequestMoneyActivity extends AppCompatActivity {

    private WalletController walletController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);

        walletController = new WalletController();

        TextView txtRequestUserName = findViewById(R.id.txtRequestUserName);
        TextView txtRequestUserEmail = findViewById(R.id.txtRequestUserEmail);
        EditText editRequestAmount = findViewById(R.id.editRequestAmount);
        EditText editRequestNotes = findViewById(R.id.editRequestNotes);
        Button btnSubmitRequestMoney = findViewById(R.id.btnSubmitRequestMoney);
        TextView btnBackRequest = findViewById(R.id.btnBackRequest);

        // Cargar datos dinámicos del usuario activo
        Usuario usuarioActivo = walletController.obtenerUsuarioActivo();
        Cuenta cuentaActiva = walletController.obtenerCuentaActiva();

        if (usuarioActivo != null) {
            txtRequestUserName.setText(usuarioActivo.obtenerNombre());
            String emailGenerado = usuarioActivo.obtenerNombre().toLowerCase().replace(" ", ".") + "@mail.com";
            txtRequestUserEmail.setText(emailGenerado);
        }

        // Indicador dinámico de moneda en el campo de monto
        String monedaActual = (cuentaActiva != null) ? cuentaActiva.obtenerTipoMoneda() : "CLP";
        editRequestAmount.setHint("$ 0.00 " + monedaActual);

        // Volver atrás
        if (btnBackRequest != null) {
            btnBackRequest.setOnClickListener(v -> finish());
        }

        // Procesar el ingreso de dinero
        if (btnSubmitRequestMoney != null) {
            btnSubmitRequestMoney.setOnClickListener(v -> {
                String amountStr = editRequestAmount.getText() != null ? editRequestAmount.getText().toString().trim() : "";

                if (amountStr.isEmpty()) {
                    Toast.makeText(this, "Por favor ingresa un monto", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double monto = Double.parseDouble(amountStr);

                    String notas = editRequestNotes.getText() != null ? editRequestNotes.getText().toString().trim() : "";

                    if (walletController.recibirDinero(monto, notas)) {
                        Toast.makeText(this, "¡Ingreso exitoso! +" + monto + " " + monedaActual, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "El monto ingresado no es válido", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}