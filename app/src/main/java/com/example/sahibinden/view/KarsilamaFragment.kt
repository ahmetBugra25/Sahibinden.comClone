package com.example.sahibinden.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.sahibinden.databinding.FragmentKarsilamaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class KarsilamaFragment : Fragment() {
    private var _binding: FragmentKarsilamaBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKarsilamaBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (auth.currentUser != null){
            val action = KarsilamaFragmentDirections.actionKarsilamaFragmentToMainPageFragment()
            Navigation.findNavController(view).navigate(action)
        }
        binding.GirisYapButton.setOnClickListener { GirisYap(it) }
        binding.KayitOlButton.setOnClickListener { KayitOlButton(it) }
    }
    private fun GirisYap(view: View){
        val action = KarsilamaFragmentDirections.actionKarsilamaFragmentToSignInFragment()
        Navigation.findNavController(view).navigate(action)
    }
    private fun KayitOlButton(view: View){
        val action = KarsilamaFragmentDirections.actionKarsilamaFragmentToSignUpFragment()
        Navigation.findNavController(view).navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}