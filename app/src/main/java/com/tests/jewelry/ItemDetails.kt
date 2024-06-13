package com.tests.jewelry

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        val item = arguments?.getParcelable<JewelryEntities>("selectedItem")
        item?.let {displayItemDetails(it)}
    }

    private fun displayItemDetails(item: JewelryEntities) {
        binding.jewelryName.text = "Name: ${item.name}"
        binding.jewelryDescription.text = "Description: ${item.description}"
        binding.jewelryPrice.text = "Price: ${item.price}"
        binding.jewelryType.text = "Type: ${item.type}"
        binding.weightTextView.text = "Weight: ${item.weight}"

        val bitmap = BitmapFactory.decodeFile(item.imageResId)
        binding.jewelryImage.setImageBitmap(bitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}