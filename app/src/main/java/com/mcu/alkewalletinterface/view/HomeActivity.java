package com.mcu.alkewalletinterface.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcu.alkewalletinterface.R;
import com.mcu.alkewalletinterface.controller.TransactionAdapter;
import com.mcu.alkewalletinterface.controller.WalletController;
import com.mcu.alkewalletinterface.model.Cuenta;
import com.mcu.alkewalletinterface.model.Transaccion;
import com.mcu.alkewalletinterface.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView txtUserNameHome;
    private TextView txtBalanceAmount;
    private ScrollView layoutTransactionsList;
    private LinearLayout layoutEmptyState;
    private WalletController walletController;
    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        walletController = new WalletController();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, statusBarHeight, 0, navigationBarHeight);
            return insets;
        });

        txtUserNameHome = findViewById(R.id.txtUserNameHome);
        txtBalanceAmount = findViewById(R.id.txtBalanceAmount);
        layoutTransactionsList = findViewById(R.id.layoutTransactionsList);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        rvTransactions = findViewById(R.id.rvTransactions);

        Button btnSend = findViewById(R.id.btnSendMoneyHome);
        Button btnRequest = findViewById(R.id.btnRequestMoneyHome);
        ImageView imgProfile = findViewById(R.id.imgProfileHome);
        Button btnViewProfileHeader = findViewById(R.id.btnViewProfileHeader);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        rvTransactions.setAdapter(transactionAdapter);

        ImageView btnCampanita = findViewById(R.id.icNotification);

        if (btnCampanita != null) {
            btnCampanita.setOnClickListener(v -> {
                View popupView = getLayoutInflater().inflate(R.layout.layout_notification, null);

                // Convertir 260dp a píxeles según la densidad de la pantalla
                int widthInPx = (int) (260 * getResources().getDisplayMetrics().density);

                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        widthInPx,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                );

                popupWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                popupWindow.setElevation(12f);

                // Muestra el popup campana.
                popupWindow.showAsDropDown(btnCampanita, -200, 10);
            });
        }

        // Configuración de Navegación
        if (btnSend != null) {
            btnSend.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SendMoneyActivity.class)));
        }

        if (btnRequest != null) {
            btnRequest.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, RequestMoneyActivity.class)));
        }

        View.OnClickListener goToProfile = v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        if (imgProfile != null) imgProfile.setOnClickListener(goToProfile);
        if (btnViewProfileHeader != null) btnViewProfileHeader.setOnClickListener(goToProfile);

        actualizarDatosUsuario();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatosUsuario();
    }

    private void actualizarDatosUsuario() {
        Usuario usuario = walletController.obtenerUsuarioActivo();
        Cuenta cuenta = walletController.obtenerCuentaActiva();

        //Nombre dinámico con fallback
        if (txtUserNameHome != null) {
            if (usuario != null) {
                txtUserNameHome.setText(usuario.obtenerNombre() + "!");
            } else {
                txtUserNameHome.setText("Invitado (Falta Login)"); //para debug
            }
        }

        //Balance y Moneda dinámicos
        if (txtBalanceAmount != null) {
            double balance = walletController.mostrarBalance();
            String moneda = (cuenta != null && cuenta.obtenerTipoMoneda() != null)
                    ? cuenta.obtenerTipoMoneda() : "CLP";

            txtBalanceAmount.setText(String.format("$%.2f %s", balance, moneda));
        }

        //Evaluar visualización del estado de transacciones de forma definitiva
        if (cuenta != null) {
            List<Transaccion> historial = cuenta.obtenerHistorial();

            if (historial != null && !historial.isEmpty()) {
                // Si hay transacciones, mostrar la lista y ocultar el empty state
                layoutTransactionsList.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);

                // Actualizar los datos del adaptador
                transactionAdapter.setTransacciones(historial);
            } else {
                // Si no hay transacciones, muestra el msj de lista vacia
                layoutTransactionsList.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        } else {
            // Medida de seguridad: si la cuenta es nula, no se muestra nada
            layoutTransactionsList.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
    }
}