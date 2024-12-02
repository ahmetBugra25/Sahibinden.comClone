package com.example.sahibinden.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sahibinden.R
import com.example.sahibinden.adapter.KatagoriAdapter
import com.example.sahibinden.adapter.PopulerUrunler
import com.example.sahibinden.databinding.FragmentMainPageBinding
import com.example.sahibinden.model.Urun
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainPageFragment : Fragment() {
    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth
    private lateinit var db :FirebaseFirestore
    private val populerUrunList :ArrayList<Urun> = arrayListOf()
    private var UrunAdapter : PopulerUrunler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth
        db=Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populerUrunList.clear()
        PopulerUrunListele()
        UrunAdapter = PopulerUrunler(populerUrunList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = UrunAdapter
        KatagorileriGetir()
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.nav_home ->{
                    Toast.makeText(requireContext(),"Anasayfasınız...",Toast.LENGTH_SHORT).show()
                }
                R.id.nav_addfeed ->{
                    ProductGecis(view)
                }
                R.id.nav_profile ->{
                    ProfilGecis(view)
                }
                R.id.nav_appout ->{
                    CıkısYap(view)
                }
            }
            true
        }

    }
    private fun KatagorileriGetir(){
        val recyclerView = binding.RecylerViewKatagori
        val katagoriList= listOf("Ev/Konut","Araç/Vasıta","YedekParça","Elektronik","Ev & Yaşam")
        val adapter = KatagoriAdapter(katagoriList)
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
        recyclerView.adapter=adapter
    }
    private fun PopulerUrunListele(){

        db.collection("Urunler").get().addOnSuccessListener { documents ->
            if (documents != null ){
                for (document in documents){
                    val email = document.getString("urunSahibiEmail")
                    if (email != null){
                        db.collection("Profile").whereEqualTo("email",email).addSnapshotListener{ value,eror->
                            if(eror != null){
                                Toast.makeText(requireContext(),eror.localizedMessage,Toast.LENGTH_SHORT).show()
                            }else{
                                if (value != null){
                                    val documentsProfile = value.documents
                                    for (documentProfile in documentsProfile){
                                        val premiumDurumu = documentProfile.getBoolean("Premium")
                                        if (premiumDurumu == true){
                                            val urunName = document.getString("urunName")
                                            val urunGorselUri = document.getString("urunGorselUri")
                                            if(urunName!!.isNotEmpty() && urunGorselUri!!.isNotEmpty()){

                                                val urun = Urun(email,urunName,urunGorselUri)
                                                populerUrunList.add(urun)
                                            }
                                            UrunAdapter?.notifyDataSetChanged()

                                        }
                                    }

                                }
                            }



                        }
                    }else{
                        Toast.makeText(requireContext(),"urun bulunamadı",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }
    private fun CıkısYap(view: View){
        auth.signOut()
        val action = MainPageFragmentDirections.actionMainPageFragmentToKarsilamaFragment()
        Navigation.findNavController(view).navigate(action)

    }
    private fun ProfilGecis(view: View){
        val action = MainPageFragmentDirections.actionMainPageFragmentToProfilFragment()
        Navigation.findNavController(view).navigate(action)
    }
    private fun ProductGecis(view: View){
        val action = MainPageFragmentDirections.actionMainPageFragmentToNewProductFragment("MainPage","new",auth.currentUser!!.email.toString())
        Navigation.findNavController(view).navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}