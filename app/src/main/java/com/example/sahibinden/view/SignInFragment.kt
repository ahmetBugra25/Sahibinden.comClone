package com.example.sahibinden.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.sahibinden.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.GirisYapBtn.setOnClickListener {
            it.isEnabled=false
            GirisYap(it)
            it.isEnabled=true
        }
        binding.SifremiUnuttumBtn.setOnClickListener {
            it.isEnabled=false
            SifremiUnuttum(it)
            it.isEnabled=true
        }
    }
    private fun GirisYap(view: View){
        val email= binding.editTextEmailAdress.text.toString()
        val pass=binding.editTextPassword.text.toString()
        if (email.isNotEmpty() && pass.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener { task ->
                val action = SignInFragmentDirections.actionSignInFragmentToMainPageFragment()
                Navigation.findNavController(view).navigate(action)
            }.addOnFailureListener { Exception ->
                Toast.makeText(requireContext(),Exception.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(),"Email ve password alanlarını doldurunuz...",Toast.LENGTH_SHORT).show()
        }

    }
    private fun SifremiUnuttum(view: View){
        val email = binding.editTextEmailAdress.text.toString()
        if (email.isNotEmpty()){
            auth.sendPasswordResetEmail(email).addOnSuccessListener { task->
                Toast.makeText(requireContext(),"Şifre yenileme linki mail adresinize gönderilmiştir...",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { Exception ->
                Toast.makeText(requireContext(),Exception.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(),"Email Adres alanını doldurunuz ve tekrar deneyiniz...",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}