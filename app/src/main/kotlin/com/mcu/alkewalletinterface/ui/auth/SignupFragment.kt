package com.mcu.alkewalletinterface.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.mcu.alkewalletinterface.R
import com.mcu.alkewalletinterface.view.HomeActivity
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private var txtName: TextInputEditText? = null
    private var txtLastName: TextInputEditText? = null
    private var txtEmail: TextInputEditText? = null
    private var txtPassword: TextInputEditText? = null
    private var txtConfirmPassword: TextInputEditText? = null
    private var btnRegister: Button? = null
    private var progressBarSignup: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtName = view.findViewById(R.id.txtSignupName)
        txtLastName = view.findViewById(R.id.txtSignupLastName)
        txtEmail = view.findViewById(R.id.txtSignupEmail)
        txtPassword = view.findViewById(R.id.txtSignupPassword)
        txtConfirmPassword = view.findViewById(R.id.txtSignupConfirmPassword)
        btnRegister = view.findViewById(R.id.btnRegister)
        progressBarSignup = view.findViewById(R.id.progressBarSignup)

        btnRegister?.setOnClickListener {
            val name = txtName?.text?.toString()?.trim() ?: ""
            val lastName = txtLastName?.text?.toString()?.trim() ?: ""
            val email = txtEmail?.text?.toString()?.trim() ?: ""
            val password = txtPassword?.text?.toString()?.trim() ?: ""
            val confirmPassword = txtConfirmPassword?.text?.toString()?.trim() ?: ""

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullName = if (lastName.isNotEmpty()) "$name $lastName" else name
            viewModel.register(fullName, email, password, null)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AuthUiState.Idle -> {}
                        is AuthUiState.Loading -> {
                            progressBarSignup?.visibility = View.VISIBLE
                            btnRegister?.isEnabled = false
                        }
                        is AuthUiState.Success -> {
                            progressBarSignup?.visibility = View.GONE
                            btnRegister?.isEnabled = true
                            val intent = Intent(requireActivity(), HomeActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        is AuthUiState.Error -> {
                            progressBarSignup?.visibility = View.GONE
                            btnRegister?.isEnabled = true
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
