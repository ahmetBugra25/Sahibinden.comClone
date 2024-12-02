package com.example.sahibinden.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sahibinden.databinding.FragmentProfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = auth.currentUser!!.email.toString()
        ProfilVerileriniAl(view,email)
        binding.ProfilGuncelleBtn.setOnClickListener { ProfilGuncelle(it,email) }

    }
    private fun ProfilVerileriniAl(view: View,email:String){

        db.collection("Profile")
            .whereEqualTo("email", email) // 'email' alanı eşleşen belgeleri filtreler
            .addSnapshotListener{value,exception ->
                if (exception != null && value !=null){
                    Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
                }else{
                    val document =value!!.documents
                    for (documents in document){
                        binding.editProfileName.setText(documents.get("name") as String)
                        binding.editProfilSurname.setText(documents.get("surname")as String)
                        binding.editProfilPhoneNumber.setText(documents.get("phoneNumber") as String)
                    }
                }
            }

    }
    private fun ProfilGuncelle(view: View,email:String){
        val name = binding.editProfileName.text.toString()
        val surname=binding.editProfilSurname.text.toString()
        val  phoneNumber = binding.editProfilPhoneNumber.text.toString()
        if (name.isNotEmpty()&&surname.isNotEmpty()&&phoneNumber.isNotEmpty()){

            val profilMap = hashMapOf<String,Any>()
            profilMap.put("name",name)
            profilMap.put("surname",surname)
            profilMap.put("phoneNumber",phoneNumber)

            db.collection("Profile").whereEqualTo("email",email).get().addOnSuccessListener {documents->
                if (documents != null){
                    for (document in documents){
                        db.collection("Profile").document(document.id).update(profilMap).addOnSuccessListener {
                            Toast.makeText(requireContext(),"Bilgileriniz Güncellendi",Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),"Guncelleme işlemi başarısız oldu lütfen sonra tekrar deneyiniz...",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(requireContext(),"Boşlukları doldurduğunuza emin olun ve tekrar deneyiniz...",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}