package com.mcu.alkewalletinterface;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnSend = findViewById(R.id.btnSendMoneyHome);
        Button btnRequest = findViewById(R.id.btnRequestMoneyHome);
        ImageView imgProfile = findViewById(R.id.imgProfileHome);
        TextView txtSeeProfile = findViewById(R.id.txtSeeProfile);

        // Navegación a Enviar Dinero
        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SendMoneyActivity.class);
                startActivity(intent);
            });
        }

        // Navegación a Solicitar/Ingresar Dinero
        if (btnRequest != null) {
            btnRequest.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, RequestMoneyActivity.class);
                startActivity(intent);
            });
        }

        // Navegación al Perfil
        if (imgProfile != null) {
            imgProfile.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        if (txtSeeProfile != null) {
            txtSeeProfile.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }
}