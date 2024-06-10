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

        binding.newItem.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_newItem)
        }

        binding.showAllButton.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_items)
        }

        binding.necklaces.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_items)
        }

        binding.rings.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_items)
        }

        binding.earrings.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_items)
        }

        binding.bracelets.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_items)
        }
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