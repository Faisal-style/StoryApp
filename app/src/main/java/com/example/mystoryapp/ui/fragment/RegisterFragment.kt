package com.example.mystoryapp.ui.fragment

import android.content.Context
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
import com.example.mystoryapp.R
import com.example.mystoryapp.api.ApiConfig
import com.example.mystoryapp.databinding.FragmentRegisterBinding
import com.example.mystoryapp.response.RegisterResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment(), View.OnClickListener, View.OnTouchListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtToLogin.setOnClickListener(this)
        binding.registerFragment.setOnTouchListener(this)

        binding.edtPasswordReg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnReg.isEnabled = !(s != null && s.length < 8)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnReg.setOnClickListener(this)
    }

    private fun postRegister(name: String, email: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().postRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    showLoading(false)
                    view?.let {
                        Snackbar.make(it, responseBody.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
                if (response.code() == 400) {
                    showLoading(false)
                    view?.let {
                        Snackbar.make(it, "Email already taken", Snackbar.LENGTH_SHORT).show()
                    }
                    return
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("RegisterResponse", "Error : ${t.message}")
            }

        })

    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBarLayout.visibility = View.VISIBLE
            binding.progressBarRegister.visibility =View.VISIBLE

        }else{
            binding.progressBarLayout.visibility = View.INVISIBLE
            binding.progressBarRegister.visibility = View.INVISIBLE

        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnReg -> {
                val name = binding.edtNameReg.text.toString()
                val email = binding.edtEmailReg.text.toString()
                val password = binding.edtPasswordReg.text.toString()

                postRegister(name, email, password)
            }
            R.id.txtToLogin -> {
                val loginFragment = LoginFragment()
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction().apply {
                    replace(
                        R.id.fragment_container,
                        loginFragment,
                        LoginFragment::class.java.simpleName
                    )
                    commit()
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v != null && event != null && event.action == MotionEvent.ACTION_DOWN) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            binding.edtNameReg.clearFocus()
            binding.edtEmailReg.clearFocus()
            binding.edtPasswordReg.clearFocus()
        }
        return false
    }

}