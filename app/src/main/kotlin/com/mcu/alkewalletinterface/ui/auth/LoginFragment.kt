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

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private var txtEmail: TextInputEditText? = null
    private var txtPassword: TextInputEditText? = null
    private var btnLogin: Button? = null
    private var progressBarLogin: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtEmail = view.findViewById(R.id.txtEmail)
        txtPassword = view.findViewById(R.id.txtPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        progressBarLogin = view.findViewById(R.id.progressBarLogin)

        btnLogin?.setOnClickListener {
            val email = txtEmail?.text?.toString()?.trim() ?: ""
            val password = txtPassword?.text?.toString()?.trim() ?: ""
            viewModel.login(email, password)
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
                            progressBarLogin?.visibility = View.VISIBLE
                            btnLogin?.isEnabled = false
                        }
                        is AuthUiState.Success -> {
                            progressBarLogin?.visibility = View.GONE
                            btnLogin?.isEnabled = true
                            val intent = Intent(requireActivity(), HomeActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        is AuthUiState.Error -> {
                            progressBarLogin?.visibility = View.GONE
                            btnLogin?.isEnabled = true
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
