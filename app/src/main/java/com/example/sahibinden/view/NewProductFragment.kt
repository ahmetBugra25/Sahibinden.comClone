package com.example.sahibinden.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.sahibinden.R
import com.example.sahibinden.databinding.FragmentNewProductBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.UUID


class NewProductFragment : Fragment() {
    private var _binding: FragmentNewProductBinding? = null
    private val binding get() = _binding!!

    private  lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var storeage : FirebaseStorage

    private lateinit var  activityResultLauncher : ActivityResultLauncher<Intent>
    private  lateinit var  permissionLauncher: ActivityResultLauncher<String>

    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        auth = Firebase.auth
        db = Firebase.firestore
        storeage=Firebase.storage


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewProductBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SpinnerIlIcerigi()
        SpinnerKatagoriIcerigi()
        SpinnerDurum()
        arguments?.let{
            val nerdenGeldi = NewProductFragmentArgs.fromBundle(it).GeldigiYerBilgisi.toString()
            val urunSahibiEmail = NewProductFragmentArgs.fromBundle(it).urunSahibiEmail.toString()
            val urunName = NewProductFragmentArgs.fromBundle(it).urunName.toString()
            if (nerdenGeldi =="MainPage"){
                binding.imageView.setOnClickListener { GorselSec(it) }
                binding.urunYukleBtn.setOnClickListener { UrunYukle(it) }
            }
            else if(nerdenGeldi == "adapter" && auth.currentUser!!.email != urunSahibiEmail){
                UrunVerileriniCek(urunSahibiEmail,urunName)
                binding.editUrunName.isEnabled = false
                binding.editMarkaName.isEnabled = false
                binding.editUrunFiyat.isEnabled = false
                binding.editUrunBaslik.isEnabled = false
                binding.IlSpinner.isEnabled=false
                binding.DurumSpinner.isEnabled = false
                binding.katagoriSpinner.isEnabled =false
                binding.urunYukleBtn.setText("Geri")
                binding.urunYukleBtn.setOnClickListener { findNavController().popBackStack() }
            }
            else if (nerdenGeldi =="adapter" && auth.currentUser!!.email == urunSahibiEmail){
                UrunVerileriniCek(urunSahibiEmail,urunName)
                binding.urunYukleBtn.setText("Güncelle")
                binding.imageView.setOnClickListener { GorselSec(it) }
                binding.urunYukleBtn.setOnClickListener { UrunGuncelle(it) }
            }
        }

    }
    private fun  UrunGuncelle(view: View){
        val uuid = UUID.randomUUID()
        val gorselAdi = "${uuid}.jpg"
        val reference = storeage.reference
        val gorselReferans=reference.child("images").child(gorselAdi)
        val email = auth.currentUser!!.email.toString()
        val urunBaslikk = binding.editUrunBaslik.text.toString()
        val urunName = binding.editUrunName.text.toString()
        val katagoriName= binding.katagoriSpinner.selectedItem.toString()
        val urunMarka = binding.editMarkaName.text.toString()
        val urunDurumu = binding.DurumSpinner.selectedItem.toString()
        val urunFiyat =binding.editUrunFiyat.text.toString()
        val urunKonumu = binding.IlSpinner.selectedItem.toString()
        if (secilenGorsel != null){
            gorselReferans.putFile(secilenGorsel!!).addOnSuccessListener { uploadTask ->
                gorselReferans.downloadUrl.addOnSuccessListener { uri->
                    val downloadUrl = uri.toString()
                    val productMap = hashMapOf<String,Any>()
                    productMap.put("urunSahibiEmail",email)
                    productMap.put("urunBaslik",urunBaslikk)
                    productMap.put("urunName",urunName)
                    productMap.put("katagoriName",katagoriName)
                    productMap.put("urunMarka",urunMarka)
                    productMap.put("UrunDurumu",urunDurumu)
                    productMap.put("urunFiyat",urunFiyat)
                    productMap.put("urunKonumu",urunKonumu)
                    productMap.put("urunKayitTarihi", com.google.firebase.Timestamp.now())
                    productMap.put("urunGorselUri",downloadUrl)

                    db.collection("Urunler").whereEqualTo("urunSahibiEmail",email).get().addOnSuccessListener { documents->
                        if (documents != null){
                            for (document in documents){
                                db.collection("Urunler").document(document.id).update(productMap).addOnSuccessListener {
                                    Toast.makeText(requireContext(),"Urun Güncellendi!!!",Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener { exception->
                                    Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }
                }
            }
        }

    }
    private fun UrunYukle(view: View){
        val uuid = UUID.randomUUID()
        val gorselAdi = "${uuid}.jpg"
        val reference = storeage.reference
        val gorselReferans=reference.child("images").child(gorselAdi)
        val email = auth.currentUser!!.email.toString()
        val urunBaslikk = binding.editUrunBaslik.text.toString()
        val urunName = binding.editUrunName.text.toString()
        val katagoriName= binding.katagoriSpinner.selectedItem.toString()
        val urunMarka = binding.editMarkaName.text.toString()
        val urunDurumu = binding.DurumSpinner.selectedItem.toString()
        val urunFiyat =binding.editUrunFiyat.text.toString()
        val urunKonumu = binding.IlSpinner.selectedItem.toString()

        if (secilenGorsel != null){
            gorselReferans.putFile(secilenGorsel!!).addOnSuccessListener { uploadTask ->
                gorselReferans.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val productMap = hashMapOf<String,Any>()
                    productMap.put("urunSahibiEmail",email)
                    productMap.put("urunBaslik",urunBaslikk)
                    productMap.put("urunName",urunName)
                    productMap.put("katagoriName",katagoriName)
                    productMap.put("urunMarka",urunMarka)
                    productMap.put("UrunDurumu",urunDurumu)
                    productMap.put("urunFiyat",urunFiyat)
                    productMap.put("urunKonumu",urunKonumu)
                    productMap.put("urunKayitTarihi", com.google.firebase.Timestamp.now())
                    productMap.put("urunGorselUri",downloadUrl)

                    db.collection("Urunler").add(productMap).addOnSuccessListener {
                        Toast.makeText(requireContext(),"Urununuz yüklendi...",Toast.LENGTH_SHORT).show()
                        val action = NewProductFragmentDirections.actionNewProductFragmentToMainPageFragment()
                        Navigation.findNavController(view).navigate(action)
                    }.addOnFailureListener { exception ->
                        Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
                    }
                }

            }.addOnFailureListener{ exception ->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()

            }
        }



    }
    private  fun UrunVerileriniCek(email:String,urunName:String){
        db.collection("Urunler").whereEqualTo("urunSahibiEmail",email).addSnapshotListener{value , error ->
            if (error != null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                if (value != null ){
                    val documents = value.documents
                    for (document in documents){
                        val urunIsmi = document.getString("urunName")
                        if (urunIsmi == urunName){
                            binding.editUrunName.setText(document.getString("urunName"))
                            binding.editUrunBaslik.setText(document.getString("urunBaslik"))
                            binding.editUrunFiyat.setText(document.getString("urunFiyat"))
                            binding.editMarkaName.setText(document.getString("urunMarka"))
                            Picasso.get().load(document.getString("urunGorselUri")).into(binding.imageView)
                            val konum = document.getString("urunKonumu")
                            IlSpinnerKonumVerisiAlma(konum!!)
                            val urunDurumu = document.getString("UrunDurumu")
                            UrunDurumSpinnerVerisiAlma(urunDurumu!!)
                            val urunKatagori = document.getString("katagoriName")
                            KatagoriSpinnerVerisiAlma(urunKatagori!!)
                        }
                    }
                }
            }
        }
    }
    private fun KatagoriSpinnerVerisiAlma(urunKatagorisi:String){
        val urunKatagoriList = mutableListOf<String>()
        urunKatagoriList.add(urunKatagorisi)
        val adapter = ArrayAdapter(
            requireContext(), // Context
            android.R.layout.simple_spinner_item, // Spinner görünümü
            urunKatagoriList // Veriler
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Açılır görünüm
        binding.katagoriSpinner.adapter = adapter
    }
    private fun UrunDurumSpinnerVerisiAlma(urunDurumu:String){
        val urunDurumList = mutableListOf<String>()
        urunDurumList.add(urunDurumu)
        val adapter = ArrayAdapter(
            requireContext(), // Context
            android.R.layout.simple_spinner_item, // Spinner görünümü
            urunDurumList // Veriler
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Açılır görünüm
        binding.DurumSpinner.adapter = adapter
    }
    private fun IlSpinnerKonumVerisiAlma(konum:String){
        val locationList = mutableListOf<String>()
        locationList.add(konum)
        val adapter = ArrayAdapter(
            requireContext(), // Context
            android.R.layout.simple_spinner_item, // Spinner görünümü
            locationList // Veriler
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Açılır görünüm
        binding.IlSpinner.adapter = adapter
    }
    private fun GorselSec(view: View){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
        }

    }

    private fun SpinnerDurum(){
        val spinner = binding.DurumSpinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Durum,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    private fun SpinnerKatagoriIcerigi(){
        val spinner = binding.katagoriSpinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.katagoriler,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    private fun SpinnerIlIcerigi(){
        val spinner = binding.IlSpinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.iller,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    secilenGorsel = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)

                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }

        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                val intentToGalery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }else{
                Toast.makeText(requireContext(),"İzini reddettiniz izine ihtiyacımız var.",Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}