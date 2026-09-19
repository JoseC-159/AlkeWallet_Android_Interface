package com.mcu.alkewalletinterface.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mcu.alkewalletinterface.R;
import com.mcu.alkewalletinterface.controller.WalletController;

public class LoginFragment extends Fragment {

    private TextInputEditText txtEmail;
    private TextInputEditText txtPassword;
    private Button btnLogin;
    private WalletController walletController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        walletController = new WalletController();
        walletController.inicializarDatosPrueba();

        txtEmail = view.findViewById(R.id.txtEmail);
        txtPassword = view.findViewById(R.id.txtPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        if (txtEmail != null) {
            txtEmail.setOnClickListener(v -> mostrarTeclado(txtEmail));
            txtEmail.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    mostrarTeclado(txtEmail);
                }
            });
        }

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> procesarLogin());
        }
    }

    private void mostrarTeclado(View view) {
        view.requestFocus();
        view.postDelayed(() -> {
            if (isAdded() && getContext() != null) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
    }

    private void procesarLogin() {
        String email = txtEmail != null && txtEmail.getText() != null ? txtEmail.getText().toString().trim() : "";
        String password = txtPassword != null && txtPassword.getText() != null ? txtPassword.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa tu email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean esValido = walletController.iniciarSesion(email, password);

        if (esValido) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else {
            Toast.makeText(getContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}