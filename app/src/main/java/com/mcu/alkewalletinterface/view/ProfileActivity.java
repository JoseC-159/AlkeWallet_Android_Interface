package com.mcu.alkewalletinterface.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mcu.alkewalletinterface.R;
import com.mcu.alkewalletinterface.controller.WalletController;
import com.mcu.alkewalletinterface.model.Cuenta;
import com.mcu.alkewalletinterface.model.Usuario;

public class ProfileActivity extends AppCompatActivity {

    private TextView btnBackProfile;
    private TextView txtProfileName;
    private Button btnLogoutProfile;
    private Spinner spinnerCurrency;

    private WalletController walletController;
    private Cuenta cuentaActiva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            v.setPadding(0, statusBarHeight, 0, navigationBarHeight);
            return insets;
        });

        walletController = new WalletController();
        cuentaActiva = walletController.obtenerCuentaActiva();

        btnBackProfile = findViewById(R.id.btnBackProfile);
        txtProfileName = findViewById(R.id.txtProfileName);
        btnLogoutProfile = findViewById(R.id.btnLogoutProfile);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);

        //Cargar nombre del usuario activo
        Usuario usuarioActivo = walletController.obtenerUsuarioActivo();
        if (usuarioActivo != null && txtProfileName != null) {
            txtProfileName.setText(usuarioActivo.obtenerNombre());
        }

        TextView txtInfoFullName = findViewById(R.id.txtInfoFullName);
        TextView txtInfoEmail = findViewById(R.id.txtInfoEmail);

        txtInfoFullName.setText("Nombre: " + usuarioActivo.obtenerNombre());
        if (txtInfoEmail != null) {
            txtInfoEmail.setText("Email: " + usuarioActivo.obtenerEmail());
        }

        configurarSpinnerMoneda();

        configurarAcordeones();

        //Flecha para volver atrás
        if (btnBackProfile != null) {
            btnBackProfile.setOnClickListener(v -> finish());
        }

        //Cierre de sesión y retorno a Login
        if (btnLogoutProfile != null) {
            btnLogoutProfile.setOnClickListener(v -> {
                walletController.cerrarSesion();
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, LoginSignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }

    private void configurarAcordeones() {
        // Mi información
        LinearLayout btnMyInfo = findViewById(R.id.btnMyInfo);
        LinearLayout layoutMyInfoDetails = findViewById(R.id.layoutMyInfoDetails);
        TextView arrowMyInfo = findViewById(R.id.arrowMyInfo);
        btnMyInfo.setOnClickListener(v -> toggleSeccion(layoutMyInfoDetails, arrowMyInfo));

        // Mis Tarjetas
        LinearLayout btnMyCards = findViewById(R.id.btnMyCards);
        LinearLayout layoutMyCardsDetails = findViewById(R.id.layoutMyCardsDetails);
        TextView arrowMyCards = findViewById(R.id.arrowMyCards);
        btnMyCards.setOnClickListener(v -> toggleSeccion(layoutMyCardsDetails, arrowMyCards));

        // Centro de Ayuda
        LinearLayout btnHelpCenter = findViewById(R.id.btnHelpCenter);
        LinearLayout layoutHelpCenterDetails = findViewById(R.id.layoutHelpCenterDetails);
        TextView arrowHelpCenter = findViewById(R.id.arrowHelpCenter);
        btnHelpCenter.setOnClickListener(v -> toggleSeccion(layoutHelpCenterDetails, arrowHelpCenter));
    }

    private void toggleSeccion(View detalle, TextView flecha) {
        if (detalle.getVisibility() == View.VISIBLE) {
            detalle.setVisibility(View.GONE);
            flecha.setText("▼");
        } else {
            detalle.setVisibility(View.VISIBLE);
            flecha.setText("▲");
        }
    }

    private void configurarSpinnerMoneda() {
        if (cuentaActiva == null || spinnerCurrency == null) return;

        String[] monedas = {"USD", "EUR", "CLP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monedas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);

        String monedaActual = cuentaActiva.obtenerTipoMoneda();
        for (int i = 0; i < monedas.length; i++) {
            if (monedas[i].equals(monedaActual)) {
                spinnerCurrency.setSelection(i);
                break;
            }
        }

        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String monedaSeleccionada = monedas[position];
                if (!monedaSeleccionada.equals(cuentaActiva.obtenerTipoMoneda())) {
                    boolean exito = walletController.cambiarMoneda(monedaSeleccionada);
                    if (exito) {
                        Toast.makeText(ProfileActivity.this, "Moneda cambiada a " + monedaSeleccionada, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}