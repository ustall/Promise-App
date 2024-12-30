package com.example.pledge.ui

import android.annotation.SuppressLint
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load profile data
        loadProfileData()
    }

    @SuppressLint("StringFormatMatches")
    private fun loadProfileData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentStreak = db.statsDao().getCurrentStreak()
            val longestStreak = db.statsDao().getLongestStreak()
            val mostChallenging = db.statsDao().getMostChallengingPromise()
            val totalViolations = db.statsDao().getTotalViolations()

            withContext(Dispatchers.Main) {
                binding.currentStreakValue.text = "$currentStreak days"
                binding.longestStreakValue.text = "$longestStreak days"
                mostChallenging?.let {
                    binding.mostChallengingValue.text = getString(R.string.promise, it.text)
                    binding.mostChallengingViolations.text =
                        getString(R.string.violations_2, it.failureCount)
                } ?: run {
                    binding.mostChallengingValue.text = "Promise: None"
                    binding.mostChallengingViolations.text = "Violations: 0"
                }
                binding.totalViolationsValue.text = "$totalViolations"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        // Перехват системной кнопки "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_profileFragment_to_main_promises_fragment)
        }
    }
}
