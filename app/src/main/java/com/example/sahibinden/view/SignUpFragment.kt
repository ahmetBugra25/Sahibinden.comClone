package com.example.sahibinden.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.sahibinden.databinding.FragmentSignUpBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private  lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.KayitOl.setOnClickListener {
            binding.KayitOl.isEnabled=false
            KayitOl(it)
            binding.KayitOl.isEnabled=true
        }

    }

    private fun KayitOl(view: View){
        val name = binding.editNametext.text.toString()
        val surname = binding.editTextSoyad.text.toString()
        val phoneNumber = binding.editTextPhone.text.toString()
        val email = binding.editEmailAddress.text.toString()
        val password = binding.editPassText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener { taskSigIn->
                    val profileMap = hashMapOf<String,Any>()
                    profileMap.put("name",name)
                    profileMap.put("surname",surname)
                    profileMap.put("phoneNumber",phoneNumber)
                    profileMap.put("email",email)
                    profileMap.put("RegisterDate",Timestamp.now())
                    profileMap.put("Premium",false)
                    db.collection("Profile").add(profileMap).addOnSuccessListener { documentReferance->
                        val action = SignUpFragmentDirections.actionSignUpFragmentToMainPageFragment()
                        Navigation.findNavController(view).navigate(action)
                        Toast.makeText(requireContext(),"Kayıt Başarılı",Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener { Exception->
                        Toast.makeText(requireContext(),Exception.localizedMessage,Toast.LENGTH_SHORT).show()
                    }
                }

            }.addOnFailureListener { excaption ->
                Toast.makeText(requireContext(),excaption.localizedMessage,Toast.LENGTH_SHORT).show()

            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}