package com.example.pledge.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.pledge.AppDatabase
import com.example.pledge.R
import com.example.pledge.databinding.PromisesListViewBinding
import com.example.pledge.db.Promise
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PromisesRecycleViewFragment : Fragment() {

    private lateinit var binding: PromisesListViewBinding
    private lateinit var adapter: PledgeRecycleViewAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация биндинга
        binding = PromisesListViewBinding.inflate(inflater, container, false)

        // Настройка кнопки добавления
        binding.addButton.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_main_promises_fragment_to_addPromiseFragment)
            } catch (e: Exception) {
                Log.e("NavigationError", "Failed to navigate", e)
            }
        }

        // Инициализация базы данных
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "promise_database"
        ).build()

        // Инициализация адаптера
        adapter = PledgeRecycleViewAdapter(
            emptyList(),
            onClick = { promise ->
                val bundle = Bundle().apply {
                    putLong("promiseId", promise.id) // Передаем только id Promise
                }

                // Переход к EditPromiseFragment
                findNavController().navigate(R.id.action_main_promises_fragment_to_editPromiseFragment, bundle)
            },
            onLongClick = { promise ->
                showResetTimerDialog(promise)
            }
        )
        binding.promisesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.promisesRecyclerView.adapter = adapter

        // Добавляем ItemTouchHelper для свайпа
        val itemTouchHelper = ItemTouchHelper(createSwipeToDeleteCallback())
        itemTouchHelper.attachToRecyclerView(binding.promisesRecyclerView)

        // Загрузка данных
        loadPromises()

        return binding.root
    }

    fun loadPromises() {
        lifecycleScope.launch {
            try {
                val promises = db.promiseDao().getAll()
                withContext(Dispatchers.Main) {
                    adapter.updateData(promises)
                    binding.textViewIfDbEmpty.visibility =
                        if (promises.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Log.e("PromisesRecycleViewFragment", "Error loading promises", e)
            }
        }
    }

    private fun showResetTimerDialog(promise: Promise) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.reset_timer))
            .setMessage(getString(R.string.do_you_want_to_reset_the_timer_for_this_promise))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                resetTimerForPromise(promise)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun resetTimerForPromise(promise: Promise) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val updatedPromise = promise.copy(
                    failureCount = promise.failureCount + 1,
                    lastFailureDate = System.currentTimeMillis()
                )
                db.promiseDao().updatePromise(updatedPromise)

                withContext(Dispatchers.Main) {
                    loadPromises()
                }
            } catch (e: Exception) {
                Log.e("PromisesRecycleViewFragment", "Error resetting timer", e)
            }
        }
    }
    private fun createSwipeToDeleteCallback(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Перетаскивание не нужно, возвращаем false
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val promise = adapter.getItemAt(position)

                // Показ диалога подтверждения удаления
                showDeleteConfirmationDialog(promise, position)
            }
        }
    }

    private fun showDeleteConfirmationDialog(promise: Promise, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_promise))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_promise))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deletePromise(promise, position)
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                // Восстанавливаем элемент, если пользователь отменяет действие
                adapter.notifyItemChanged(position)
            }
            .show()
    }

    private fun deletePromise(promise: Promise, position: Int) {
        // Удаляем элемент из адаптера
        adapter.removeItemAt(position)

        // Показываем Snackbar с возможностью отмены
        val snackbar = Snackbar.make(binding.root,
            getString(R.string.promise_deleted), Snackbar.LENGTH_LONG)
        snackbar.setAction(getString(R.string.undo)) {
            undoDeletePromise(promise, position)
        }
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                // Удаляем Promise из базы данных только если Snackbar не был отменен
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            db.promiseDao().deletePromise(promise)
                            withContext(Dispatchers.Main) {
                                if (adapter.itemCount == 0) {
                                    binding.textViewIfDbEmpty.visibility = View.VISIBLE
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PromisesRecycleViewFragment", "Error deleting promise", e)
                        }
                    }
                }
            }
        })
        snackbar.show()
    }

    private fun undoDeletePromise(promise: Promise, position: Int) {
        adapter.addItemAt(position, promise)
        if (binding.textViewIfDbEmpty.visibility == View.VISIBLE) {
            binding.textViewIfDbEmpty.visibility = View.GONE
        }
    }

    fun deleteAllPromises() {
        lifecycleScope.launch(Dispatchers.IO) {
            db.promiseDao().deleteAll()
            withContext(Dispatchers.Main) {
                loadPromises()
            }
        }
    }
}


