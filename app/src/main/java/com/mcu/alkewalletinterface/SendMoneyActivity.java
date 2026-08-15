package com.mcu.alkewalletinterface;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SendMoneyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        TextView btnBack = findViewById(R.id.btnBackSend);
        Button btnConfirmSend = findViewById(R.id.btnSubmitSendMoney);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish()); // Regresa a la pantalla anterior
        }

        // 2. Botón de confirmar transferencia -> Muestra mensaje y vuelve al Home
        if (btnConfirmSend != null) {
            btnConfirmSend.setOnClickListener(v -> {
                Toast.makeText(this, "Transferencia realizada con éxito", Toast.LENGTH_SHORT).show();
                finish(); // Cierra la Activity y regresa al Home automáticamente
            });
        }
    }

}