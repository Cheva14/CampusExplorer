package com.cheva.campusexplorer

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity: AppCompatActivity()  {
    private val vm: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val email = findViewById<EditText>(R.id.uEmail)
        val password = findViewById<EditText>(R.id.uPass)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            // hide the soft keyboard
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            vm.login(email.text.toString(), password.text.toString())
        }

        findViewById<Button>(R.id.signupBtn).setOnClickListener {
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            vm.newAccount(email.text.toString(), password.text.toString())
        }
        vm.msg.observe(this) {
            it?.let {
                if (it.isNotEmpty())
                    Snackbar.make(email, "Unable to login $it", Snackbar.LENGTH_LONG).show()
            }
        }
        vm.uid.observe(this) {
            // When the UID is not null, we transition to the main screen
            it?.let {
                val toMain = Intent(this, MainActivity::class.java)
                startActivity(toMain)
            }
        }
    }
}