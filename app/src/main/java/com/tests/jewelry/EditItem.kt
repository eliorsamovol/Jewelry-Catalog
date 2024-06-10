package com.tests.jewelry

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tests.jewelry.databinding.EditItemBinding
import com.tests.jewelry.ui.viewmodel.JewelryViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditItem : Fragment() {
    private var _binding: EditItemBinding? = null
    private val binding get() = _binding!!
    private val jewelryViewModel: JewelryViewModel by viewModels()
    private val args: EditItemArgs by navArgs()
    private var capturedImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

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

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val imageBitmap = BitmapFactory.decodeStream(inputStream)
                val resizedBitmap = resizeBitmap(imageBitmap, 800, 800)
                binding.imageSelected.setImageBitmap(resizedBitmap)
                capturedImage = resizedBitmap
            }
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

    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if(isGranted) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "Storage permission is required", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jewelryItem = args.jewelryItem

        binding.nameEditText.setText(jewelryItem.name)
        binding.descriptionEditText.setText(jewelryItem.description)
        binding.priceSeekBar.progress = jewelryItem.price.toInt()

        when (jewelryItem.type) {
            getString(R.string.necklace_radio_button) -> binding.necklaceButton.isChecked = true
            getString(R.string.ring_radio_button) -> binding.ringButton.isChecked = true
            getString(R.string.earring_radio_button) -> binding.earringButton.isChecked = true
            getString(R.string.bracelet_radio_button) -> binding.braceletButton.isChecked = true
        }

        binding.btnTakePhoto.setOnClickListener {
            binding.btnTakePhoto.setOnClickListener {
                showImageSourceDialog()
            }
        }

        binding.saveButton.setOnClickListener {
            val editName = binding.nameEditText.text.toString()
            val editDescription = binding.descriptionEditText.text.toString()
            val editPrice = binding.priceSeekBar.progress.toString().toDoubleOrNull()
            val selectedRadioButtonId = binding.typeRdioGroup.checkedRadioButtonId
            val selectedRadioButton = binding.typeRdioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            val editType = selectedRadioButton?.text.toString()

            if (editName.isNotEmpty() && editDescription.isNotEmpty() && editPrice!=null && editType.isNotEmpty()){
                val imagePath = capturedImage?.let { bitmap ->
                    saveBitmapToFile(requireContext(), bitmap)
                }
                if(imagePath != null) {
                    val editedItem=JewelryEntities(
                        name = editName,
                        type = editType,
                        description = editDescription,
                        price = editPrice,
                        imageResId = imagePath
                    )
                    jewelryViewModel.addJewelry(editedItem)
                    Toast.makeText(requireContext(), "Item updated", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_editItem_to_Items)
                } else {
                    Toast.makeText(context, "Please take a photo", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "please fill all", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take a picture", "Choose from library")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> checkStoragePermission()
                }
            }
            .show()
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

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission()
        } else {
            openGallery()
        }
    }

    private fun requestStoragePermission() {
        requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_", ".jpg", storageDir)
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}