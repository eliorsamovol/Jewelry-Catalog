package com.tests.jewelry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tests.jewelry.databinding.NewItemBinding
import com.tests.jewelry.ui.viewmodel.JewelryViewModel
import com.tests.jewelry.ui.viewmodel.NewItemViewModel

class NewItem : Fragment() {

    private var _binding: NewItemBinding? = null
    private val binding get() = _binding!!
    private val jewelryViewModel: JewelryViewModel by viewModels()

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

        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()
            val price = binding.priceSeekBar.progress.toString().toDoubleOrNull()
            val selectedRadioButtonId = binding.typeRdioGroup.checkedRadioButtonId
            val selectedRadioButton = binding.typeRdioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            val type = selectedRadioButton?.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && price!=null && type.isNotEmpty()){
                val newItem=JewelryEntities(
                        name = name,
                        type = type,
                        description = description,
                        price = price,
                        imageResId = R.drawable.test_photo
                        )
                jewelryViewModel.addJewelry(newItem)
                Toast.makeText(context, "jewelry item added", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_newItem_to_catalog)
            } else {
                Toast.makeText(context, "please fill all", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
            _binding = null
    }
}