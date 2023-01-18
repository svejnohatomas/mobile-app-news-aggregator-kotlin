package io.github.svejnohatomas.swansea.newsaggregator.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.databinding.ActivityRegisterBinding
import io.github.svejnohatomas.swansea.newsaggregator.ui.login.LoginActivity
import io.github.svejnohatomas.swansea.newsaggregator.ui.main.MainActivity

/**
 * A register activity.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class RegisterActivity : AppCompatActivity() {
    /** A FirebaseAuth instance for the activity. */
    private lateinit var auth: FirebaseAuth
    /** A binding to the activity's layout. */
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val passwordConfirmation = binding.registerPasswordConfirmation.text.toString()

            when {
                email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty() -> {
                    Toast.makeText(
                        baseContext,
                        binding.root.context.getString(R.string.enter_email_and_password),
                        Toast.LENGTH_SHORT).show()
                }
                password != passwordConfirmation -> {
                    Toast.makeText(
                        baseContext,
                        binding.root.context.getString(R.string.passwords_do_not_match),
                        Toast.LENGTH_SHORT).show()
                }
                else -> {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startActivity(MainActivity.getIntent(this))
                        } else {
                            Toast.makeText(
                                baseContext,
                                binding.root.context.getString(R.string.registration_failed),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.registerLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}