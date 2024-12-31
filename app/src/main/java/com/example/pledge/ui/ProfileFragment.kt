package com.example.pledge.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pledge.R
import com.example.pledge.databinding.ProfileBinding
import com.example.pledge.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import updateProfileData

class ProfileFragment : Fragment(R.layout.profile) {

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            updateProfileData(db.promiseDao(), db.profileDataDao())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Load profile data
        loadProfileData()
    }


    private fun loadProfileData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val profileData = db.profileDataDao().getProfileData()
            withContext(Dispatchers.Main) {
                profileData?.let {
                    binding.currentStreakValue.text = "${it.currentStreak} days"
                    binding.longestStreakValue.text = "${it.longestStreak} days"
                    binding.mostChallengingValue.text = it.mostChallengingPromise.ifEmpty { "Promise: None" }
                    binding.mostChallengingViolations.text = "Violations: ${it.mostChallengingViolations}"
                    binding.totalViolationsValue.text = "${it.lifetimeViolations}"
                    binding.currentViolationsValue.text = "${it.totalViolations}"
                } ?: run {
                    binding.currentStreakValue.text = "0 days"
                    binding.longestStreakValue.text = "0 days"
                    binding.mostChallengingValue.text = "Promise: None"
                    binding.mostChallengingViolations.text = "Violations: 0"
                    binding.totalViolationsValue.text = "0"
                    binding.currentViolationsValue.text = "0"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Handle back button press
        lifecycleScope.launch {
            updateProfileData(db.promiseDao(), db.profileDataDao())
        }
        binding.root.invalidate()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_profileFragment_to_main_promises_fragment)
        }
    }
}
