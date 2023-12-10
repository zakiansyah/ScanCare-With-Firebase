package com.dicoding.scancare.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.scancare.databinding.ActivityLoginBinding
import com.dicoding.scancare.ui.main.MainActivity
import com.dicoding.scancare.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val PREFS_NAME = "LoginPrefs"
    private val KEY_USER_ID = "userId"
    private val KEY_LOGIN_STATUS = "loginStatus"
    private val KEY_USER_NAME = "userName"
    private val KEY_USER_ADDRESS = "userAddress"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding dan Firebase
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        // Pengecekan sesi login
        if (isUserLoggedIn()) {
            // Pengguna sudah login, arahkan ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Pengguna belum login, lanjutkan dengan proses login
            setupLoginButton()
        }
    }

    private fun setupLoginButton() {
        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmailInputEditText.text.toString()
            val password = binding.loginPasswordInputEditText.text.toString()

            // Validasi email
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.loginEmailInputEditText.error = "Email tidak valid"
                binding.loginEmailInputEditText.requestFocus()
                return@setOnClickListener
            }

            // Validasi password
            if (password.isEmpty() || password.length < 8) {
                binding.loginPasswordInputEditText.error = "Password minimal 8 karakter"
                binding.loginPasswordInputEditText.requestFocus()
                return@setOnClickListener
            }

            // Lakukan login ke Firebase
            loginFirebase(email, password)
        }
    }

    private fun loginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Ambil data pengguna dari Firebase setelah login berhasil
                        getUserDataFromFirebase(userId)
                    }
                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getUserDataFromFirebase(userId: String) {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userName = snapshot.child("name").value.toString()
                    val userAddress = snapshot.child("address").value.toString()

                    // Simpan sesi login
                    saveLoginSession(userId, userName, userAddress)

                    Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()

                    // Intent ke MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveLoginSession(userId: String, userName: String, userAddress: String) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.putBoolean(KEY_LOGIN_STATUS, true)
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_ADDRESS, userAddress)
        editor.apply()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_LOGIN_STATUS, false)
    }
}





//package com.dicoding.scancare.ui.login
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Patterns
//import android.widget.Toast
//import com.dicoding.scancare.R
//import com.dicoding.scancare.databinding.ActivityLoginBinding
//import com.dicoding.scancare.ui.main.MainActivity
//import com.dicoding.scancare.ui.register.RegisterActivity
//import com.google.firebase.auth.FirebaseAuth
//
//class LoginActivity : AppCompatActivity() {
//
//    lateinit var binding: ActivityLoginBinding
//    lateinit var auth: FirebaseAuth
//
//    private val PREFS_NAME = "LoginPrefs"
//    private val KEY_USER_ID = "userId"
//    private val KEY_LOGIN_STATUS = "loginStatus"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//
//        // Pengecekan sesi login
//        if (isUserLoggedIn()) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        } else {
//            // Setelah pengecekan sesi login, lanjutkan kode onCreate yang ada
//            binding = ActivityLoginBinding.inflate(layoutInflater)
//            setContentView(binding.root)
//
//            auth = FirebaseAuth.getInstance()
//
//            binding.tvRegisterNow.setOnClickListener {
//                val intent = Intent(this, RegisterActivity::class.java)
//                startActivity(intent)
//            }
//
//
//        auth = FirebaseAuth.getInstance()
//
//
//        binding.tvRegisterNow.setOnClickListener {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.btnLogin.setOnClickListener {
//            val email = binding.loginEmailInputEditText.text.toString()
//            val password = binding.loginPasswordInputEditText.text.toString()
//
//            //validasi email
//            if (email.isEmpty()){
//                binding.loginEmailInputEditText.error = "email harus diisi"
//                binding.loginEmailInputEditText.requestFocus()
//                return@setOnClickListener
//            }
//
//            //validasi email tidak sesuai
//            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//                binding.loginEmailInputEditText.error = "email tidak valid"
//                binding.loginEmailInputEditText.requestFocus()
//                return@setOnClickListener
//            }
//
//            //validasi password
//            if (password.isEmpty()){
//                binding.loginPasswordInputEditText.error = "password harus diisi"
//                binding.loginPasswordInputEditText.requestFocus()
//                return@setOnClickListener
//            }
//
//            //validasi panjang password
//
//            if (password.length < 8){
//                binding.loginPasswordInputEditText.error = "password minimal mengandung 8 karakter"
//                binding.loginPasswordInputEditText.requestFocus()
//                return@setOnClickListener
//            }
//
//            LoginFirebase(email,password)
//        }
//    }
//
//
////    override fun onCreate(savedInstanceState: Bundle?) {
////        binding = ActivityLoginBinding.inflate(layoutInflater)
////        super.onCreate(savedInstanceState)
////        setContentView(binding.root)
////
////        auth = FirebaseAuth.getInstance()
////
////
////        binding.tvRegisterNow.setOnClickListener {
////            val intent = Intent(this, RegisterActivity::class.java)
////            startActivity(intent)
////        }
////
////        binding.btnLogin.setOnClickListener {
////            val email = binding.loginEmailInputEditText.text.toString()
////            val password = binding.loginPasswordInputEditText.text.toString()
////
////            //validasi email
////            if (email.isEmpty()){
////                binding.loginEmailInputEditText.error = "email harus diisi"
////                binding.loginEmailInputEditText.requestFocus()
////                return@setOnClickListener
////            }
////
////            //validasi email tidak sesuai
////            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
////                binding.loginEmailInputEditText.error = "email tidak valid"
////                binding.loginEmailInputEditText.requestFocus()
////                return@setOnClickListener
////            }
////
////            //validasi password
////            if (password.isEmpty()){
////                binding.loginPasswordInputEditText.error = "password harus diisi"
////                binding.loginPasswordInputEditText.requestFocus()
////                return@setOnClickListener
////            }
////
////            //validasi panjang password
////
////            if (password.length < 8){
////                binding.loginPasswordInputEditText.error = "password minimal mengandung 8 karakter"
////                binding.loginPasswordInputEditText.requestFocus()
////                return@setOnClickListener
////            }
////
////            LoginFirebase(email,password)
////        }
////    }
//
//    // Di dalam class LoginActivity
//    private fun LoginFirebase(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val userId = auth.currentUser?.uid
//                    if (userId != null) {
//                        // Simpan sesi login
//                        saveLoginSession(userId)
//
//                        Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
//
//                        // Intent ke MainActivity
//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                } else {
//                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
////    private fun LoginFirebase(email: String, password: String) {
////        auth.signInWithEmailAndPassword(email,password)
////            .addOnCompleteListener(this){
////                if (it.isSuccessful){
////                    Toast.makeText(this,"Login Berhasil", Toast.LENGTH_SHORT).show()
////                    val intent = Intent(this,MainActivity::class.java)
////                    startActivity(intent)
////                }else{
////                    Toast.makeText(this,"${it.exception?.message}", Toast.LENGTH_SHORT).show()
////                }
////            }
////    }
//
//    // Fungsi untuk menyimpan sesi login
//    private fun saveLoginSession(userId: String) {
//        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString(KEY_USER_ID, userId)
//        editor.putBoolean(KEY_LOGIN_STATUS, true)
//        editor.apply()
//    }
//
//    private fun isUserLoggedIn(): Boolean {
//        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
//        return sharedPreferences.getBoolean(KEY_LOGIN_STATUS, false)
//    }
//
//}