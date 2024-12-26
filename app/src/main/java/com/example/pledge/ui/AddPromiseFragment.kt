package com.example.pledge.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pledge.AppDatabase
import com.example.pledge.R
import com.example.pledge.databinding.AddNewPromiseFragmentBinding
import com.example.pledge.db.Promise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddPromiseFragment : Fragment() {

    private lateinit var binding: AddNewPromiseFragmentBinding
    private lateinit var textDateDate: TextView
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the binding
        binding = AddNewPromiseFragmentBinding.inflate(inflater, container, false)

        // Return the root view from the binding
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val promiseEditText: EditText = binding.taskTextEdit
        textDateDate = binding.textDateDate
        val cancelButton = binding.cancelButton
        val confirmButton = binding.confirmButton

        // Handle date selection
        textDateDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Handle "Cancel" button
        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_addPromiseFragment_to_main_promises_fragment)// Go back to the previous screen
        }


        // Handle "Confirm" button
        confirmButton.setOnClickListener {
            val promiseText = promiseEditText.text.toString()
            if (promiseText.isNotBlank()) {
                val newPromise = Promise(
                    text = promiseText,
                    creationDate = selectedDate.timeInMillis,
                    lastFailureDate = selectedDate.timeInMillis,
                    failureCount = 0
                )

                // Save the promise to the database
                savePromiseToDatabase(newPromise)
                findNavController().navigate(R.id.action_addPromiseFragment_to_main_promises_fragment)
            }
        }
    }

    private fun savePromiseToDatabase(promise: Promise) {
        val db = AppDatabase.getDatabase(requireContext())
        val promiseDao = db.promiseDao()

        lifecycleScope.launch(Dispatchers.IO) {
            // Save the new promise to the database
            promiseDao.insertPromise(promise)

            withContext(Dispatchers.Main) {
                // Get the promises list fragment
                val fragment = parentFragmentManager.findFragmentById(R.id.fragment_container) as? PromisesRecycleViewFragment
                fragment?.let {
                    // Load the updated list of promises from the database
                    val updatedPromises = promiseDao.getAll()
                    it.loadPromises() // Pass the new data to the fragment for UI update
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val currentYear = selectedDate.get(Calendar.YEAR)
        val currentMonth = selectedDate.get(Calendar.MONTH)
        val currentDay = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateDateDisplay()
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.show()
    }

    private fun updateDateDisplay() {
        val formattedDate = SimpleDateFormat("yyyy MM dd", Locale.getDefault()).format(selectedDate.time)
        textDateDate.text = formattedDate
    }
    override fun onResume() {
        super.onResume()

        // Перехват системной кнопки "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_addPromiseFragment_to_main_promises_fragment)  // Возврат на предыдущий экран при нажатии "Назад"
        }
    }
}
