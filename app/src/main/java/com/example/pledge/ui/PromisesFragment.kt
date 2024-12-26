package com.example.pledge.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pledge.R
import com.example.pledge.databinding.MainPromisesFragmentBinding

class PromisesFragment : Fragment(R.layout.main_promises_fragment) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем биндинг для основного фрагмента
        val binding = MainPromisesFragmentBinding.inflate(inflater, container, false)

        // Вставляем фрагмент RecyclerView в главный фрагмент
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction().apply {
                // Проверка, что фрагменты еще не добавлены
                if (childFragmentManager.findFragmentById(R.id.promises_list_view) == null) {
                    replace(R.id.promises_list_view, PromisesRecycleViewFragment())
                }
                if (childFragmentManager.findFragmentById(R.id.promises_streak) == null) {
                    replace(R.id.promises_streak, PromisesStreakFragment())
                }
                commit()
            }
        }
        return binding.root
    }
}