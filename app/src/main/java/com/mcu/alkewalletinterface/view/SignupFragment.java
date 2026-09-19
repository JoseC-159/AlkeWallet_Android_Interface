package com.mcu.alkewalletinterface.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mcu.alkewalletinterface.R;
import com.mcu.alkewalletinterface.controller.WalletController;

public class SignupFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        Button btnRegister = view.findViewById(R.id.btnRegister);
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {

                //Llamar al controlador
                WalletController walletController = new WalletController();

                //Simular la creación y el inicio de sesión de un usuario de prueba (por ahora)
                walletController.crearSesionDePrueba();

                // Pasar al Home
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }
}