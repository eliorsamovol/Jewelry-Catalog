package com.tests.jewelry

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tests.jewelry.data.db.entities.SupplierEntities
import com.tests.jewelry.databinding.NewSupplierBinding
import com.tests.jewelry.ui.viewmodel.JewelryViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import android.app.DatePickerDialog
import android.location.Geocoder
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class NewSupplier : Fragment(), OnMapReadyCallback {

    private var _binding: NewSupplierBinding? = null
    private val binding get() = _binding!!
    private val supplierViewModel: JewelryViewModel by viewModels()

    private lateinit var uploadPhotoButton: Button
    private var capturedImage: Bitmap? = null

    private lateinit var priceSeekBar: SeekBar
    private lateinit var priceValueTextView: TextView

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

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
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
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

    // Handle location permission request
    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        if(permission[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            enableMyLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NewSupplierBinding.inflate(inflater, container, false)

        if(!Places.isInitialized()){
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY, Locale.getDefault())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.clicked_button)

        binding.backBtn.setOnClickListener {
            binding.backBtn.startAnimation(animation)
            findNavController().navigate(R.id.action_newSupplier_to_supplier)
        }

        val button = binding.saveButton
        val str = getString(R.string.save_item)
        val span = SpannableString(str)
        span.setSpan(UnderlineSpan(), 0, str.length, 0)
        button.text = span

        priceSeekBar = binding.priceSeekBar
        priceValueTextView = binding.supplierPrice
        mapView = binding.mapView
        priceValueTextView.text = getString(R.string.jewelry_price_box, 0)
        priceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                priceValueTextView.text = getString(R.string.supplier_purchase_price_details_page, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Open calender
        binding.dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.dateEditText.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isClickable = false
        }

        binding.btnTakePhoto.setOnClickListener {
            binding.btnTakePhoto.startAnimation(animation)
            showImageSourceDialog()
        }

        // Save Button and add the supplier to the database
        binding.saveButton.setOnClickListener {
            binding.saveButton.startAnimation(animation)

            val name = binding.nameEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val date = binding.dateEditText.text.toString()
            val price = binding.priceSeekBar.progress.toString().toDoubleOrNull()
            val selectedRadioButtonId = binding.typeRadioGroup.checkedRadioButtonId
            val selectedRadioButton = binding.typeRadioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            val type = selectedRadioButton?.text.toString()

            // Check if all fields were filled by the user and check their validity
            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty() && date.isNotEmpty() && price!=null && type.isNotEmpty()){
                val parsedDate = parseDate(date)
                val imagePath = capturedImage?.let { bitmap ->
                    saveBitmapToFile(requireContext(), bitmap)
                }
                if(imagePath != null) { // Check if photo uploaded
                    val newItem=SupplierEntities(
                        name = name,
                        address = address,
                        phone = phone,
                        type = type,
                        date = parsedDate,
                        purchasePrice = price,
                        reception = imagePath
                    )
                    supplierViewModel.addSupplier(newItem)
                    Toast.makeText(context, R.string.supplier_added_message, Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_newSupplier_to_supplier)
                } else {
                    Toast.makeText(context, R.string.upload_an_image_message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, R.string.fill_all_fields_message, Toast.LENGTH_SHORT).show()
            }
        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        mapView.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                v?.performClick()
                binding.newSupplierFragment.requestDisallowInterceptTouchEvent(true)
                return false
            }
        })

        checkLocationPermission()

        // Google places search addresses
        binding.addressEditText.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(requireContext())
            startAutocomplete.launch(intent)
        }

        // Done and not next button in keyboard
        setupDoneAction(binding.nameEditText)
        setupDoneAction(binding.addressEditText)
    }

    // Calender date handler
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDay = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                binding.dateEditText.setText(selectedDay)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
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

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? { // Storing photo in local storage
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

    // Converting Date format
    private fun parseDate(dateStr: String): Date {
        val date = SimpleDateFormat("dd-MM-yyyy")
        return date.parse(dateStr) ?: Date()
    }

    private fun checkLocationPermission() { // Handle location permission
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            enableMyLocation()
        }
    }

    private fun enableMyLocation() { // Enabling location
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set initial location to Reichman University
        val initialLocation = LatLng(32.17605083308438, 34.83765488676843)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15f))

        // Set up maps gestures (zoom, scroll)
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isZoomGesturesEnabled = true
        googleMap?.uiSettings?.isScrollGesturesEnabled = true

        // Handle googleMap click, converting map to address string
        googleMap?.setOnMapClickListener { latLng ->
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if(addresses!!.isNotEmpty()){
                val address = addresses[0].getAddressLine(0)
                binding.addressEditText.setText(address)
            }
        }
    }

    // Handling addresses search box
    private val startAutocomplete = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            binding.addressEditText.setText(place.address)
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(place.latLng!!).title("Selected location"))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 15f))

            hideKeyboard()
        } else if(result.resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status: Status = Autocomplete.getStatusFromIntent(result.data!!)
            Toast.makeText(requireContext(), "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Keyboard hide for enable google search box
    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.addressEditText.windowToken, 0)
    }

    private fun setupDoneAction(editText: EditText){ // Handle the case when keyboard is active and the user press the done button
        // For cases that press done
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

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


}
