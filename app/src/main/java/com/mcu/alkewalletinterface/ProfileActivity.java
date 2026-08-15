package com.mcu.alkewalletinterface;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView btnBack = findViewById(R.id.btnBackProfile);
        Button btnLogout = findViewById(R.id.btnLogoutProfile);

        // Volver al Home con la flecha o botón atrás
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Al presionar "Cerrar Sesión", redirige a la pantalla de Login y borra el historial
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, LoginSignupActivity.class);
                // Limpia todas las pantallas previas para un cierre de sesión seguro
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}