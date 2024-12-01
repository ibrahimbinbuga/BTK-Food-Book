package com.ibrahimbinbuga.yemekkitabi.view

import android.Manifest
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ibrahimbinbuga.yemekkitabi.databinding.FragmentRecipeBinding
import com.ibrahimbinbuga.yemekkitabi.model.Recipe
import java.io.ByteArrayOutputStream


class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedPicture: Uri? = null
    private var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener{selectImage(it)}
        binding.saveButton.setOnClickListener{save(it)}
        binding.deleteButton.setOnClickListener{delete(it)}

        arguments?.let{
            val info = RecipeFragmentArgs.fromBundle(it).info

            if(info == "yeni"){
                //yeni tarif ekle
                binding.deleteButton.isEnabled = false
                binding.saveButton.isEnabled = true

                binding.nameText.setText("")
                binding.ingredientsText.setText("")
            }else {
                //eski tarifi göster
                binding.deleteButton.isEnabled = true
                binding.saveButton.isEnabled = false
            }
        }
    }

    fun save(view: View){
        val name = binding.nameText.text.toString()
        val ingredients = binding.ingredientsText.text.toString()

        if (selectedBitmap != null){
            val smallBitmap = smallBitmapCreate(selectedBitmap!!,300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            val recipe = Recipe(name,ingredients,byteArray)

        }
    }
    fun delete(view: View){}
    fun selectImage(view: View){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //izin verilmedi izin iste
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                    //Snackbar göster
                    Snackbar.make(view, "Need permission to access gallery", Snackbar.LENGTH_INDEFINITE).setAction(
                        "Allow", View.OnClickListener {
                            //izin iste
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    ).show()
                }else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else {
                //izin verildi galeriye git
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }else {
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin verilmedi izin iste
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //Snackbar göster
                    Snackbar.make(view, "Need permission to access gallery", Snackbar.LENGTH_INDEFINITE).setAction(
                        "Allow", View.OnClickListener {
                            //izin iste
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    ).show()
                }else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else {
                //izin verildi galeriye git
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun smallBitmapCreate(selectedBitmapFromUser : Bitmap, maxSize : Int): Bitmap{
        var width = selectedBitmapFromUser.width
        var height = selectedBitmapFromUser.height

        val bitmapRate : Double = width.toDouble() / height.toDouble()

        if(bitmapRate >= 1){
            //görsel yatay
            width = maxSize
            val scaledHeight = width / bitmapRate
            height = scaledHeight.toInt()
        }else {
            //görsel dikey
            height = maxSize
            val scaledWidth = height * bitmapRate
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(selectedBitmapFromUser,width,height,true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    selectedPicture = intentFromResult.data

                    try {
                        if (Build.VERSION.SDK_INT >=28){
                            val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedPicture!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }else{
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,selectedPicture)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    }catch (e: Exception){
                        println(e.localizedMessage)
                    }
                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result ->
            if (result){
                //izin verildi galeriye gidilebilir
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                //izin verilmedi
                Toast.makeText(requireContext(),"Permission denied!", Toast.LENGTH_LONG).show()
            }
        }
    }
}