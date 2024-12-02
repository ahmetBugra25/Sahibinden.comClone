package com.example.sahibinden.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sahibinden.R
import com.example.sahibinden.adapter.FeedAdapter
import com.example.sahibinden.adapter.PopulerUrunler
import com.example.sahibinden.databinding.FragmentFeedBinding
import com.example.sahibinden.databinding.FragmentKarsilamaBinding
import com.example.sahibinden.model.Urun
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var FeedAdapter:FeedAdapter?=null
    private val urunList:ArrayList<Urun> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val katagoriName = FeedFragmentArgs.fromBundle(it).katagoriName.toString()
            urunList.clear()
            VerileriListele(katagoriName)
            FeedAdapter = FeedAdapter(urunList)
            binding.recylerFeed.layoutManager = LinearLayoutManager(requireContext())
            binding.recylerFeed.adapter = FeedAdapter

        }

    }
    private fun VerileriListele(katagoriName:String){
        db.collection("Urunler").whereEqualTo("katagoriName",katagoriName).addSnapshotListener{value,error->
            if (error != null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()

            } else{
              if (value != null){
                  val documents = value.documents
                  for (document in documents){
                      val email = document.getString("urunSahibiEmail")
                      val urunName = document.getString("urunName")
                      val urunGorselUri = document.getString("urunGorselUri")
                      val urun = Urun(email!!,urunName!!,urunGorselUri!!)
                      urunList.add(urun)
                  }
                  FeedAdapter?.notifyDataSetChanged()

              }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}