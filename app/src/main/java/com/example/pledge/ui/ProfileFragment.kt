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
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_main_promises_fragment)
        }
    }


    private fun loadProfileData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val profileData = db.profileDataDao().getProfileData()
            withContext(Dispatchers.Main) {
                profileData?.let {
                    binding.currentStreakValue.text = getString(R.string.days_, it.currentStreak)
                    binding.longestStreakValue.text = getString(R.string.days_, it.longestStreak)
                    binding.mostChallengingValue.text = it.mostChallengingPromise.ifEmpty {
                        getString(R.string.promise_none)
                    }
                    binding.mostChallengingViolations.text =
                        getString(R.string.violations, it.mostChallengingViolations)
                    binding.totalViolationsValue.text = getString(R.string.violations_count_, it.lifetimeViolations)
                    binding.currentViolationsValue.text = getString(R.string.violations_count_, it.totalViolations)
                } ?: run {
                    binding.currentStreakValue.text = getString(R.string.default_days)
                    binding.longestStreakValue.text = getString(R.string.default_days)
                    binding.mostChallengingValue.text = getString(R.string.promise_none)
                    binding.mostChallengingViolations.text = getString(R.string.default_violations)
                    binding.totalViolationsValue.text = getString(R.string.default_violations_count)
                    binding.currentViolationsValue.text = getString(R.string.default_violations_count)
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
