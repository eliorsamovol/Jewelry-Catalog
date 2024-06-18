package com.tests.jewelry

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tests.jewelry.databinding.NewItemBinding
import com.tests.jewelry.ui.viewmodel.JewelryViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Observer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged


class NewItem : Fragment() {

    private var _binding: NewItemBinding? = null
    private val binding get() = _binding!!
    private val jewelryViewModel: JewelryViewModel by viewModels()

    private lateinit var uploadPhotoButton: Button
    private var capturedImage: Bitmap? = null

    private lateinit var priceSeekBar: SeekBar
    private lateinit var priceValueTextView: TextView

    // Demonstrate how to use the device's camera and handle the result
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            binding.imageSelected.setImageBitmap(imageBitmap)
            capturedImage = imageBitmap
        }
    }

    // Demonstrate how to upload image from the device's gallery
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val imageBitmap = BitmapFactory.decodeStream(inputStream)
                val rotatedBitmap = handleImageOrientation(imageBitmap, uri)
                val resizedBitmap = resizeBitmap(rotatedBitmap, 800, 800)
                binding.imageSelected.setImageBitmap(resizedBitmap)
                capturedImage = resizedBitmap
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), R.string.failed_load_image_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Detect the orientation of the uploaded image
    private fun handleImageOrientation(bitmap: Bitmap, uri: Uri): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val exif = inputStream?.let { ExifInterface(it) }
        val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) ?: ExifInterface.ORIENTATION_NORMAL

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    // Rotate the image in case it's not in the right orientation
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap { // Rotating the image as a function of the orientation needed
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // Resizing image for increasing performance
    private fun resizeBitmap(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = image.width
        val height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        val newWidth = if (bitmapRatio > 1) maxWidth else (maxHeight * bitmapRatio).toInt()
        val newHeight = if (bitmapRatio > 1) (maxWidth / bitmapRatio).toInt() else maxHeight

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
    }

    // Handle camera permission request
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if(isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), R.string.photo_permission, Toast.LENGTH_SHORT).show()
        }
    }

    // Handle storage permission request
    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if(isGranted) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), R.string.photo_permission, Toast.LENGTH_SHORT).show()
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

        val button = binding.saveButton
        val str = getString(R.string.save_change_btn)
        val span = SpannableString(str)
        span.setSpan(UnderlineSpan(), 0, str.length, 0)
        button.text = span

        priceSeekBar = binding.priceSeekBar
        priceValueTextView = binding.jewelryPrice
        priceValueTextView.text = getString(R.string.jewelry_price_box, 0)
        priceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                priceValueTextView.text = getString(R.string.jewelry_price_box, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Check weight validity
        binding.weightEditText.doAfterTextChanged {
            val input = it.toString()
            val isValidInput = input.isNotEmpty() && input.toDoubleOrNull() != null
            if (!isValidInput){
                binding.weightTextView.error = "Invalid input"
            } else {
                binding.weightEditText.error = null
            }
        }

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.clicked_button)
        binding.btnTakePhoto.setOnClickListener {
            binding.btnTakePhoto.startAnimation(animation)
            showImageSourceDialog()
        }

        // Save Button and add the item to the database
        binding.saveButton.setOnClickListener {
            binding.saveButton.startAnimation(animation)

            val name = binding.nameEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()
            val price = binding.priceSeekBar.progress.toString().toDoubleOrNull()
            val weight = binding.weightEditText.text.toString().toDoubleOrNull()
            val selectedRadioButtonId = binding.typeRdioGroup.checkedRadioButtonId
            val selectedRadioButton = binding.typeRdioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            val type = selectedRadioButton?.text.toString()

            // Check if all fields were filled by the user and check their validity
            if (name.isNotEmpty() && description.isNotEmpty() && price!=null && weight != null && type.isNotEmpty()){
                val imagePath = capturedImage?.let { bitmap ->
                    saveBitmapToFile(requireContext(), bitmap)
                }
                if(imagePath != null) { // Check if photo uploaded
                    val newItem=JewelryEntities(
                        name = name,
                        type = type,
                        description = description,
                        price = price,
                        weight = weight,
                        imageResId = imagePath
                    )
                    jewelryViewModel.addJewelry(newItem)
                    Toast.makeText(context, R.string.jewelry_added_message, Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_newItem_to_catalog)
                } else {
                    Toast.makeText(context, R.string.upload_an_image_message, Toast.LENGTH_SHORT).show()
                }
            } else { // not all of the details has been filled
                Toast.makeText(context, R.string.fill_all_fields_message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.backBtn.setOnClickListener {
            binding.backBtn.startAnimation(animation)
            findNavController().navigate(R.id.action_newItem_to_items)
        }

        setupDoneAction(binding.nameEditText)
        setupDoneAction(binding.descriptionEditText)
    }

    // Options for capturing and for upload from gallery
    private fun showImageSourceDialog() {
        val options = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.choose_photo_from_gallery)
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.select_image_source))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> checkStoragePermission()
                }
            }
            .show()
    }

    private fun checkCameraPermission(){ // Handle camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestCameraPermission()
        } else {
            openCamera()
        }
    }

    private fun requestCameraPermission(){ // Request camera permission
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun openCamera(){ // Camera launcher
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(requireActivity().packageManager) != null){
            takePictureLauncher.launch(intent)
        }
    }

    private fun checkStoragePermission() { // Handle storage permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission()
        } else {
            openGallery()
        }
    }

    private fun requestStoragePermission() { // Request storage permission
        requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun openGallery() { // Gallery launcher
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch("image/*")
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? { // Storing photo in local storage
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

    private fun setupDoneAction(editText: EditText){ // Handle the case when keyboard is active and the user press the done button
        editText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide keyboard
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
            _binding = null
    }
}