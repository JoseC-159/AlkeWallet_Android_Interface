package com.mcu.alkewalletinterface;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RequestMoneyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_money);

        TextView btnBack = findViewById(R.id.btnBackRequest);
        Button btnConfirmRequest = findViewById(R.id.btnSubmitRequestMoney); // Botón de solicitar/ingresar

        // 1. Botón regresar (Flecha ←)
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 2. Botón de confirmar ingreso -> Muestra mensaje y vuelve al Home
        if (btnConfirmRequest != null) {
            btnConfirmRequest.setOnClickListener(v -> {
                Toast.makeText(this, "Dinero ingresado a tu cuenta", Toast.LENGTH_SHORT).show();
                finish(); // Cierra la Activity y regresa al Home
            });
        }
    }
}