package com.tests.jewelry

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tests.jewelry.databinding.ItemDetailsBinding

class ItemDetails : Fragment() {

    private var _binding : ItemDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_itemDetailsFragment_to_itemsFragment)
        }

        val item = arguments?.getParcelable<JewelryEntities>("selectedItem")
        item?.let {displayItemDetails(it)}
    }

    private fun displayItemDetails(item: JewelryEntities) {
        binding.jewelryName.text = getString(R.string.jewelry_name_details, item.name)
        binding.jewelryDescription.text = getString(R.string.jewelry_description_details, item.description)
        binding.jewelryPrice.text = getString(R.string.jewelry_price_details, item.price.toInt())
        binding.jewelryType.text = getString(R.string.jewelry_type_details, item.type)
        binding.weightTextView.text = getString(R.string.jewelry_weight_details, item.weight.toString())
        binding.jewelrySales.text = getString(R.string.jewelry_sales_amount, item.soldItems)

        Glide.with(this)
            .load(item.imageResId)
            .centerCrop()
            .into(binding.jewelryImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}