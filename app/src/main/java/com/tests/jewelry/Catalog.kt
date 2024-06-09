package com.tests.jewelry

import android.graphics.Bitmap
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.widget.ImageView
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

        arguments?.getString("title")?.let{
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}