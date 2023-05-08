package com.example.mystoryapp.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.api.ApiConfig
import com.example.mystoryapp.databinding.FragmentLoginBinding
import com.example.mystoryapp.datastores.UserSession
import com.example.mystoryapp.response.LoginResponse
import com.example.mystoryapp.response.LoginResult
import com.example.mystoryapp.ui.home.HomeActivity
import com.example.mystoryapp.ui.home.dataStore
import com.example.mystoryapp.ui.viewmodel.LoginViewModel
import com.example.mystoryapp.ui.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment(), View.OnClickListener, View.OnTouchListener {
    private lateinit var loginViewModel: LoginViewModel


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    companion object {
        fun newInstance() = LoginFragment()
        private const val DURATION = 200L
        private const val ALPHA = 1f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtRegister.setOnClickListener(this)
        binding.loginFragment.setOnTouchListener(this)
        binding.edtPasswordLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnLogin.isEnabled = !(s != null && s.length < 8)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnLogin.setOnClickListener(this)
        val pref = UserSession.getInstance(requireContext().dataStore)
        loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]
    }

    private fun postLogin(email: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    showLoading(false)
                    view?.let {
                        Snackbar.make(it, responseBody.message, Snackbar.LENGTH_SHORT)
                            .show()
                        if (responseBody.message == "success"){
                            saveSession(responseBody.loginResult)
                        }
                    }
                }
                if (response.code() == 401 || response.code() == 400) {
                    showLoading(false)
                    view?.let {
                        Snackbar.make(
                            it,
                            "Invalid Password Atau Email Tidak ditemukan",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    return
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Log.e("LoginResponse", "Error : ${t.message}")
            }

        })
    }

    private fun saveSession(loginResult: LoginResult) {
        showLoading(false)
        loginViewModel.saveToken(loginResult.token as String)
        val i = Intent(requireActivity(), HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        requireActivity().finish()
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarLayoutLogin.visibility = View.VISIBLE
            binding.progressBarLogin.visibility = View.VISIBLE

        } else {
            binding.progressBarLayoutLogin.visibility = View.INVISIBLE
            binding.progressBarLogin.visibility = View.INVISIBLE

        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.txtRegister -> {
                replaceFragment()
            }
            R.id.btnLogin -> {
                val email = binding.edtEmailLogin.text.toString()
                val password = binding.edtPasswordLogin.text.toString()
                postLogin(email, password)
            }
        }
    }
    private fun replaceFragment(){
        val registerFragment = RegisterFragment()
        val fragmentManager = parentFragmentManager

        val transaction = fragmentManager.beginTransaction()

        transaction.setCustomAnimations(
            R.anim.slide_from_right,
            R.anim.slide_to_left,
            R.anim.slide_from_right,
            R.anim.slide_to_left
        )

        transaction.replace(
            R.id.fragment_container,
            registerFragment,
            RegisterFragment::class.java.simpleName
        )
        transaction.addToBackStack(RegisterFragment::class.java.simpleName)
        transaction.commit()
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v != null && event != null && event.action == MotionEvent.ACTION_DOWN) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            binding.edtEmailLogin.clearFocus()
            binding.edtPasswordLogin.clearFocus()
        }
        return false
    }

    private fun playAnimation() {
        val appTitle =
            ObjectAnimator.ofFloat(binding.appTitleLoginFragment, View.ALPHA, ALPHA).setDuration(DURATION)
        val loginTitle =
            ObjectAnimator.ofFloat(binding.txtLoginTitle, View.ALPHA, ALPHA).setDuration(DURATION)
        val etEmail =
            ObjectAnimator.ofFloat(binding.edtEmailLogin, View.ALPHA, ALPHA).setDuration(DURATION)
        val etPassword =
            ObjectAnimator.ofFloat(binding.edtPasswordLogin, View.ALPHA, ALPHA).setDuration(DURATION)
        val loginButton =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, ALPHA).setDuration(DURATION)
        val tvAccount =
            ObjectAnimator.ofFloat(binding.txtRegister, View.ALPHA, ALPHA).setDuration(DURATION)

        AnimatorSet().apply {
            playSequentially(appTitle, loginTitle, etEmail, etPassword, loginButton, tvAccount)
            activity?.runOnUiThread {
                start()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        playAnimation()
    }
}