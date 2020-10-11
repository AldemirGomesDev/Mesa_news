package com.aldemir.mesanews.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.aldemir.mesanews.ui.main.MainActivity
import com.aldemir.mesanews.R
import com.aldemir.mesanews.Status
import com.aldemir.mesanews.ui.login.afterTextChanged
import kotlinx.android.synthetic.main.content_register.*
import kotlinx.android.synthetic.main.content_register.loading
import kotlinx.android.synthetic.main.content_register.password
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(findViewById(R.id.toolbar))

        observers()

        edit_text_name.afterTextChanged {
            registerViewModel.userNameChanged(
                edit_text_name.text.toString()
            )
        }

        edit_text_email.afterTextChanged {
            registerViewModel.userEmailChanged(
                edit_text_email.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                registerViewModel.passwordDataChanged(
                    password.text.toString()
                )
            }
        }

        password_confirm.apply {
            afterTextChanged {
                registerViewModel.passwordConfirmDataChanged(
                    password.text.toString(),
                    password_confirm.text.toString()
                )
            }
        }

        button_register.setOnClickListener {
            loading.visibility = View.VISIBLE
            button_register.isEnabled = false
            registerViewModel.signUp(
                edit_text_name.text.toString(),
                edit_text_email.text.toString(),
                password.text.toString()
            )
        }
    }

    private fun observers() {

        registerViewModel.loginForm.observe(this@RegisterActivity, Observer {
            val registerState = it ?: return@Observer

            button_register.isEnabled = isEmailValid && isNameValid && isPasswordValid && isPasswordConfirm

            if (registerState.usernameError != null) {
                edit_text_name.error = getString(registerState.usernameError)
            }else {
                edit_text_name.error = null
            }
            if (registerState.usernameError != null) {
                edit_text_email.error = getString(registerState.usernameError)
            }else {
                edit_text_email.error = null
            }
            if (registerState.passwordError != null) {
                password.error = getString(registerState.passwordError)
            }else {
                password.error = null
            }
            if (registerState.passwordConfirmError != null) {
                password_confirm.error = getString(registerState.passwordConfirmError)
            }else {
                password_confirm.error = null
            }
        })

        registerViewModel.mToken.observe(this@RegisterActivity, Observer {token->
            Log.d("RegisterViewModel: ", "token ==>: ${token.status}")

            when (token.status) {
                Status.SUCCESS -> {
                    loading.visibility = View.GONE
                    registerViewModel.saveToken(token.data!!.token)
                    startMainActivity()
                }
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    button_register.isEnabled = true
                    Toast.makeText(this@RegisterActivity, "${token.message}", Toast.LENGTH_SHORT).show()
                    loading.visibility = View.GONE

                }
            }

        })
    }

    private fun startMainActivity() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("key", "value")
        startActivity(intent)

    }

    companion object {
        var isNameValid = false
        var isEmailValid = false
        var isPasswordValid = false
        var isPasswordConfirm = false
    }
}