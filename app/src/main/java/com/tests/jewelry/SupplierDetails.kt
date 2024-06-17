package com.tests.jewelry

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        binding.closeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_supplierDetailsFragment_to_suppliersFragment)
        }

        val supplier = arguments?.getParcelable<SupplierEntities>("selectedItem")
        supplier?.let { displaySupplierDetails(it) }
    }

    private fun displaySupplierDetails(item: SupplierEntities) {
        binding.supplierName.text = "Name: ${item.name}"
        binding.supplierDescription.text = "Address: ${item.address}"
        binding.supplierPhone.text = "Phone: ${item.phone}"
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.supplierPurchaseDate.text = "Date Of Purchase: ${format.format(item.date)}"
        binding.supplierPrice.text = "Price: ${item.purchasePrice}"
        binding.supplierType.text = "Type: ${item.type}"

        val bitmap = BitmapFactory.decodeFile(item.reception)
        binding.supplierImage.setImageBitmap(bitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}