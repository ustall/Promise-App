package com.example.pledge.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pledge.R
import com.example.pledge.databinding.ToolBarBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ToolBarFragment : Fragment() {

    private var _binding: ToolBarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ToolBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настраиваем обработчики кнопок
        binding.profileButton.setOnClickListener {
            findNavController().navigate(R.id.action_main_promises_fragment_to_profileFragment)
        }

        binding.actionSettingsButton.setOnClickListener {
            showSettingsMenu(it)
        }
    }

    private fun showSettingsMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_settings, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.change_language -> {
                    changeLanguage()
                    true
                }
                R.id.change_theme -> {
                    changeTheme()
                    true
                }
                R.id.delete_all_promises -> {
                    showDeleteAllDialog()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun changeLanguage() {
        val languageManager = LanguageManager(requireContext())
        val anchorView = requireView().findViewById<View>(R.id.action_settingsButton) // Используем кнопку в качестве якоря
        languageManager.showLanguageMenu(anchorView)
    }

    private fun changeTheme() {
        val isDarkTheme = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        val newTheme = if (isDarkTheme)
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        else
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(newTheme)
    }

    private fun showDeleteAllDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete All Promises")
            .setMessage("Are you sure you want to delete all promises?")
            .setPositiveButton("Yes") { _, _ ->
                // Отправка события в PromisesRecycleViewFragment через родительский Fragment
                (parentFragment as? PromisesRecycleViewFragment)?.deleteAllPromises()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
