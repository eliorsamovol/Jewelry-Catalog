package com.tests.jewelry

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tests.jewelry.databinding.NewItemBinding
import com.tests.jewelry.ui.viewmodel.JewelryViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import java.io.ByteArrayOutputStream


class NewItem : Fragment() {

    private var _binding: NewItemBinding? = null
    private val binding get() = _binding!!
    private val jewelryViewModel: JewelryViewModel by viewModels()

    private lateinit var uploadPhotoButton: Button
    private var capturedImage: Bitmap? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            val resizedBitmap = resizeBitmap(imageBitmap, 800, 800)
            binding.imageSelected.setImageBitmap(imageBitmap)
            capturedImage = imageBitmap
        }
    }

    private fun resizeBitmap(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = image.width
        val height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        val newWidth = if (bitmapRatio > 1) maxWidth else (maxHeight * bitmapRatio).toInt()
        val newHeight = if (bitmapRatio > 1) (maxWidth / bitmapRatio).toInt() else maxHeight

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
    }


    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if(isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NewItemBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadPhotoButton = binding.btnTakePhoto
        uploadPhotoButton.setOnClickListener {
            checkCameraPermission()
        }

        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()
            val price = binding.priceSeekBar.progress.toString().toDoubleOrNull()
            val selectedRadioButtonId = binding.typeRdioGroup.checkedRadioButtonId
            val selectedRadioButton = binding.typeRdioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            val type = selectedRadioButton?.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && price!=null && type.isNotEmpty()){
                val imageByteArray = capturedImage?.let { bitmap ->
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.toByteArray()
                }
                val newItem=JewelryEntities(
                        name = name,
                        type = type,
                        description = description,
                        price = price,
                        imageResId = imageByteArray!!
                        )
                jewelryViewModel.addJewelry(newItem)
                Toast.makeText(context, "jewelry item added", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_newItem_to_catalog)
            } else {
                Toast.makeText(context, "please fill all", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestCameraPermission()
        } else {
            openCamera()
        }
    }

    private fun requestCameraPermission(){
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(requireActivity().packageManager) != null){
            takePictureLauncher.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
            _binding = null
    }
}