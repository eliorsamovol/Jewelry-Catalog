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
import android.text.Spanned
import android.text.style.UnderlineSpan
import androidx.viewpager2.widget.ViewPager2
import android.os.Handler
import android.os.Looper

class Catalog : Fragment() {

    private var _binding : CatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private val imagesList = listOf(R.drawable.bottom_photo_main, R.drawable.bottom2, R.drawable.bottom3, R.drawable.bottom4, R.drawable.bottom5)
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

        viewPager = binding.viewPager
        viewPager.adapter = ViewPagerAdapter(imagesList, requireContext())

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

        binding.businessAnalytics.setOnClickListener {
            findNavController().navigate(R.id.action_catalog_to_business_analytics)
        }

        binding.necklaces.setOnClickListener {
            val itemType = getString(R.string.necklaces)
            val bundle = Bundle().apply { putString("itemType", itemType) }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        binding.rings.setOnClickListener {
            val itemType = getString(R.string.rings)
            val bundle = Bundle().apply { putString("itemType", itemType) }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        binding.earrings.setOnClickListener {
            val itemType = getString(R.string.earrings)
            val bundle = Bundle().apply { putString("itemType", itemType) }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        binding.bracelets.setOnClickListener {
            val itemType = getString(R.string.bracelets)
            val bundle = Bundle().apply { putString("itemType", itemType) }
            findNavController().navigate(R.id.action_catalog_to_items, bundle)
        }

        val button = binding.jewelries
        val str = getString(R.string.items_headline)
        val spanstr = SpannableString(str)
        spanstr.setSpan(UnderlineSpan(), 0, str.length, 0)
        button.text = spanstr

        val suppliersBtn = binding.supplier
        val supplierStr = getString(R.string.suppliers)
        val spanStrSuppliers = SpannableString(supplierStr)
        spanStrSuppliers.setSpan(UnderlineSpan(), 0, supplierStr.length, 0)
        suppliersBtn.text = spanStrSuppliers

        val analyticsBtn = binding.businessAnalytics
        val analyticsStr = getString(R.string.bi_info)
        val spanStrAnalytics = SpannableString(analyticsStr)
        spanStrAnalytics.setSpan(UnderlineSpan(), 0, analyticsStr.length, 0)
        analyticsBtn.text = spanStrAnalytics

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}