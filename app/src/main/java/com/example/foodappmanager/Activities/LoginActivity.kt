package com.example.foodappmanager.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.foodappmanager.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.*
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var signClientGoogle: GoogleSignInClient
    private lateinit var db: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("Users")

        // Cấu hình đăng nhập Google
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.foodappmanager.R.string.default_web_client_id))
            .requestEmail()
            .build()

        signClientGoogle = GoogleSignIn.getClient(this, googleSignInOptions)

        // Đảm bảo đăng xuất khỏi Google để chọn lại tài khoản
        signClientGoogle.signOut()

        controls()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Nếu đã đăng nhập rồi thì chuyển luôn sang MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun controls() {
        binding.txtsignup.setOnClickListener {
            startActivity(Intent(this, SignActivity::class.java))
        }

        binding.btnlogin.setOnClickListener {
            email = binding.edtemail.text.toString().trim()
            password = binding.edtpassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                binding.edtemail.error = "Email is required"
                binding.edtpassword.error = "Password is required"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnggle.setOnClickListener {
            val signInIntent = signClientGoogle.signInIntent
            launcher.launch(signInIntent)
        }
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                        updateUI(authTask.result?.user)
                    } else {
                        Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Google sign in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
