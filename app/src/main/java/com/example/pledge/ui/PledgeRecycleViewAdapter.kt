package com.example.pledge.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pledge.R
import com.example.pledge.databinding.PromiseCardviewBinding
import com.example.pledge.db.Promise
import com.example.pledge.utils.PromiseUtils
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant

class PledgeRecycleViewAdapter(
    private var promises: List<Promise>,
    private val onLongClick: (Promise) -> Unit // Обработчик долгого нажатия
) : RecyclerView.Adapter<PledgeRecycleViewAdapter.PledgeViewHolder>() {

    // Вложенный класс ViewHolder для каждого элемента списка
    class PledgeViewHolder(val binding: PromiseCardviewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(promise: Promise, onLongClick: (Promise) -> Unit) {
            val context = binding.root.context
            binding.promiseText.text = promise.text
            binding.timeHeldText.text = PromiseUtils.formatTimeHeld(context, promise.lastFailureDate)
            binding.violationsCountText.text = PromiseUtils.formatViolationsCount(context, promise.failureCount)

            // Установка обработчика долгого нажатия
            binding.root.setOnLongClickListener {
                onLongClick(promise)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PledgeViewHolder {
        val binding = PromiseCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PledgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PledgeViewHolder, position: Int) {
        val promise = promises[position]
        holder.bind(promise, onLongClick)
    }

    override fun getItemCount(): Int = promises.size

    // Обновление списка данных
    fun updateData(newPromises: List<Promise>) {
        promises = newPromises
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): Promise {
        return promises[position]
    }

    fun removeItemAt(position: Int): Promise {
        val removedItem = promises[position]
        promises = promises.toMutableList().apply { removeAt(position) }
        notifyItemRemoved(position)
        return removedItem
    }

    fun addItemAt(position: Int, promise: Promise) {
        promises = promises.toMutableList().apply { add(position, promise) }
        notifyItemInserted(position)
    }

}