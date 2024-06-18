package com.tests.jewelry

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tests.jewelry.data.db.entities.SupplierEntities
import com.tests.jewelry.databinding.SupplierDetailsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SupplierDetails : Fragment() {
    private var  _binding : SupplierDetailsBinding? = null
    private val  binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SupplierDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close button
        binding.closeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_supplierDetailsFragment_to_suppliersFragment)
        }

        // Getting argument from Parcel
        val supplier = arguments?.getParcelable<SupplierEntities>("selectedItem")
        supplier?.let { displaySupplierDetails(it) }
    }

    // Display supplier details
    private fun displaySupplierDetails(item: SupplierEntities) {
        binding.supplierName.text = getString(R.string.supplier_name_details_page, item.name)
        binding.supplierDescription.text = getString(R.string.supplier_address_details_page, item.address)
        binding.supplierPhone.text = getString(R.string.supplier_phone_details_page, item.phone)
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding.supplierPurchaseDate.text = getString(R.string.supplier_date_of_purchase_details_page, format.format(item.date))
        binding.supplierPrice.text = getString(R.string.supplier_purchase_price_details_page, item.purchasePrice.toInt())
        binding.supplierType.text = getString(R.string.supplier_type_details_page, item.type)

        Glide.with(this)
            .load(item.reception)
            .centerCrop()
            .into(binding.supplierImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}