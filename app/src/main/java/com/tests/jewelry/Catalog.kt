package com.tests.jewelry

import android.graphics.Bitmap
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tests.jewelry.databinding.CatalogBinding
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import android.text.SpannableString
import android.text.style.UnderlineSpan

class Catalog : Fragment() {

    private var _binding : CatalogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CatalogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("title")?.let{
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

        loadImages()

        binding.jewelries.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_items)
        }

        binding.supplier.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_supplier)
        }

        binding.showAllButton.setOnClickListener {
            val bundle = Bundle().apply { putString("itemType", "all") }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        binding.necklaces.setOnClickListener {
            val bundle = Bundle().apply { putString("itemType", "necklace") }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        binding.rings.setOnClickListener {
            val bundle = Bundle().apply { putString("itemType", "ring") }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        binding.earrings.setOnClickListener {
            val bundle = Bundle().apply { putString("itemType", "earring") }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        binding.bracelets.setOnClickListener {
            val bundle = Bundle().apply { putString("itemType", "bracelet") }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        val text = getString(R.string.view_all)
        val spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, text.length,0)
        binding.showAllButton.text = spannableString
    }

    private fun loadImages(){
        Glide.with(this)
            .load(R.drawable.sand)
            .centerCrop()
            .into(binding.toolbarImage)
        Glide.with(this)
            .load(R.drawable.top_frame_photo)
            .centerCrop()
            .into(binding.mainImage)
        Glide.with(this)
            .load(R.drawable.necklace_main)
            .centerCrop()
            .into(binding.necklaces)
        Glide.with(this)
            .load(R.drawable.ring_main)
            .centerCrop()
            .into(binding.rings)
        Glide.with(this)
            .load(R.drawable.earings_main)
            .centerCrop()
            .into(binding.earrings)
        Glide.with(this)
            .load(R.drawable.bracelet_main)
            .centerCrop()
            .into(binding.bracelets)
        Glide.with(this)
            .load(R.drawable.bottom_photo_main)
            .centerCrop()
            .into(binding.secondImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}