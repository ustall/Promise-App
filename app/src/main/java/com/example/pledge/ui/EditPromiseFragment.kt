package com.example.pledge.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
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
import com.example.pledge.databinding.EditPromiseFragmentBinding
import com.example.pledge.db.Promise
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditPromiseFragment : Fragment() {

    private var promiseId: Long = 0L
    private var promise: Promise? = null
    private lateinit var binding: EditPromiseFragmentBinding
    private lateinit var textDateDate: TextView
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize the binding
        binding = EditPromiseFragmentBinding.inflate(inflater, container, false)

        // Return the root view from the binding
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            promiseId = it.getLong("promiseId", 0L) // Извлекаем id
        }

        // Инициализация UI элементов
        val promiseEditText: EditText = binding.taskTextEdit
        textDateDate = binding.textDateDate
        val cancelButton = binding.cancelButton
        val confirmButton = binding.confirmButton

        // Загружаем данные для редактирования
        loadPromiseData()

        // Обработка выбора даты
        textDateDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Обработка кнопки "Отмена"
        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_editPromiseFragment_to_main_promises_fragment)
        }

        // Обработка кнопки "Подтвердить"
        confirmButton.setOnClickListener {
            val promiseText = promiseEditText.text.toString()
            if (promiseText.isNotBlank()) {
                // Обновляем данные существующего Promise
                promise?.let {
                    it.text = promiseText
                    it.lastFailureDate = selectedDate.timeInMillis
                    it.creationDate = selectedDate.timeInMillis
                    // Сохраняем изменения
                    updatePromiseInDatabase(it)
                }
                findNavController().navigate(R.id.action_editPromiseFragment_to_main_promises_fragment)
            } else {
                val snackbar = Snackbar.make(binding.root,
                    getString(R.string.fill_your_promise), Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
    }

    // Загружаем данные Promise для редактирования
    private fun loadPromiseData() {
        val db = AppDatabase.getDatabase(requireContext())
        val promiseDao = db.promiseDao()

        lifecycleScope.launch(Dispatchers.IO) {
            // Получаем Promise по id
            promise = promiseDao.getPromiseById(promiseId)

            withContext(Dispatchers.Main) {
                promise?.let {
                    // Заполняем UI данными из Promise
                    binding.taskTextEdit.setText(it.text)
                    binding.textStreakDays.text=
                        context?.let { it1 -> PromiseUtils.formatTimeHeld(it1, promise!!.lastFailureDate) }
                    binding.textViolations.text= context?.let {
                        it1 -> PromiseUtils.formatViolationsCount(it1, promise!!.failureCount) }
                    selectedDate.timeInMillis = it.lastFailureDate

                    updateDateDisplay()
                }
            }
        }
    }

    // Функция для обновления данных Promise в базе данных
    private fun updatePromiseInDatabase(promise: Promise) {
        val db = AppDatabase.getDatabase(requireContext())
        val promiseDao = db.promiseDao()

        lifecycleScope.launch(Dispatchers.IO) {
            // Обновляем Promise в базе данных
            promiseDao.updatePromise(promise)

            withContext(Dispatchers.Main) {
                // После обновления, обновляем список в родительском фрагменте
                val fragment = parentFragmentManager.findFragmentById(R.id.fragment_container) as? PromisesRecycleViewFragment
                fragment?.let {
                    val updatedPromises = promiseDao.getAll() // Получаем обновленные данные
                    it.loadPromises() // Загружаем обновленный список
                }
            }
        }
    }

    // Функция для выбора даты
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

    // Обновление отображаемой даты
    private fun updateDateDisplay() {
        val formattedDate = SimpleDateFormat("yyyy MM dd", Locale.getDefault()).format(selectedDate.time)
        textDateDate.text = formattedDate
    }

    override fun onResume() {
        super.onResume()
        // Перехват системной кнопки "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_editPromiseFragment_to_main_promises_fragment)
        }
    }
}