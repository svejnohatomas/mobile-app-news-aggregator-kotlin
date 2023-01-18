package io.github.svejnohatomas.swansea.newsaggregator.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.databinding.ActivityLoginBinding
import io.github.svejnohatomas.swansea.newsaggregator.ui.main.MainActivity
import io.github.svejnohatomas.swansea.newsaggregator.ui.register.RegisterActivity

/**
 * A login activity.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class LoginActivity : AppCompatActivity() {
    /** A FirebaseAuth instance for the activity. */
    private lateinit var auth: FirebaseAuth
    /** A binding to the activity's layout. */
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            when {
                email.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(
                        baseContext,
                        binding.root.context.getString(R.string.enter_email_and_password),
                        Toast.LENGTH_SHORT).show()
                }
                else -> {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                startActivity(MainActivity.getIntent(this))
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    binding.root.context.getString(R.string.authentication_failed),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }


        binding.loginRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null && auth.currentUser?.isAnonymous == false) {
            startActivity(MainActivity.getIntent(this))
        }
    }
}