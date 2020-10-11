package com.aldemir.mesanews.ui.login

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.aldemir.mesanews.R
import com.aldemir.mesanews.Status
import com.aldemir.mesanews.ui.main.MainActivity
import com.aldemir.mesanews.ui.register.RegisterActivity
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {

    private val callbackManager = CallbackManager.Factory.create()

    private val loginViewModel: LoginViewModel by viewModel  {
        parametersOf(this)
    }

    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel.getUserNameSharedPreference()

        verifyIsLoggedFacebook()
        facebook()
        getHash()
        observers()
        setupUi()

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        loading = findViewById<ProgressBar>(R.id.loading)

            username.afterTextChanged {
                loginViewModel.userNameDataChanged(
                    username.text.toString()
                )
            }
            password.apply {
                afterTextChanged {
                    loginViewModel.passwordDataChanged(
                        password.text.toString()
                    )
                }
            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                login.isEnabled = false
                loginViewModel.signIn(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun setupUi() {
        button_screen_register.setOnClickListener {
            startRegisterActivity()
        }
    }

    companion object {
        var isUserValid = false
        var isPasswordValid = false
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("facebookLogin", "requestCode=> $requestCode")
        Log.d("facebookLogin", "resultCode=> $resultCode")
        Log.d("facebookLogin", "data=> ${data.toString()}")
    }

    private fun getHash() {
        try {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("facebookLogin:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

    private fun verifyIsLoggedFacebook() {
        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        Log.d("facebookLogin", "isLogged => $isLoggedIn")

        if (isLoggedIn) {
            startMainActivity()
        }

        val request = GraphRequest.newMeRequest(
            accessToken
        ) { `object`, response ->
            Log.d("facebookLogin", "name e id facebook => ${response}")
        }

        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        request.parameters = parameters
        request.executeAsync()

        getProfileInformation()

    }

    fun getProfileInformation() {
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/family",
            null,
            HttpMethod.GET,
            GraphRequest.Callback {
            }
        ).executeAsync()

        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/3255758034520560/accounts",
            null,
            HttpMethod.GET,
            GraphRequest.Callback { /* handle the result */
                Log.d("facebookLogin", "name e id getProfileInformation => ${it}")
            }
        ).executeAsync()
    }

    private fun facebook() {
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<com.facebook.login.LoginResult> {
                override fun onSuccess(result: com.facebook.login.LoginResult?) {
                    Log.d("facebookLogin", "sucess=> ${result.hashCode()}")
                    startMainActivity()
                }

                override fun onCancel() {
                    Log.d("facebookLogin", "Cancelado!")
                }

                override fun onError(error: FacebookException?) {
                    Log.e("facebookLogin", "Error=> ${error.toString()}")
                }

            })
    }

    private fun startMainActivity() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("key", "value")
        startActivity(intent)

    }

    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("key", "value")
        startActivity(intent)
    }

    private fun observers() {
        loginViewModel.user.observe(this@LoginActivity, Observer {user ->
            if (user != null) {
                startMainActivity()
            }
        })

        loginViewModel.mToken.observe(this@LoginActivity, Observer {token->
            Log.d("facebookLogin: ", "token ==>: ${token}")

            when (token.status) {
                Status.SUCCESS -> {
                    loading.visibility = View.GONE
                    loginViewModel.saveTokenSharedPreference(token.data!!.token)
                    startMainActivity()
                }
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    login.isEnabled = true
                    Toast.makeText(this@LoginActivity, "${token.message}", Toast.LENGTH_SHORT).show()
                    loading.visibility = View.GONE

                }
            }

        })

        loginViewModel.loginForm.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            login.isEnabled = isUserValid && isPasswordValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }else {
                username.error = null
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }else {
                password.error = null
            }
        })
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}