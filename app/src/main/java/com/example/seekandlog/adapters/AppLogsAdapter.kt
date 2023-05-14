package com.example.seekandlog.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seekandlog.databinding.AppLogViewHolderBinding
import com.example.seekandlog.objs.AppLog

class AppLogsAdapter : RecyclerView.Adapter<AppLogsAdapter.AppLogsViewHolder>() {

    var logs: MutableList<AppLog> = mutableListOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppLogsViewHolder {
        return AppLogsViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: AppLogsViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addLogs(appLogs: List<AppLog>) {
        logs.addAll(appLogs)
        notifyDataSetChanged()
    }

    class AppLogsViewHolder(private val binding: AppLogViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(log: AppLog) {
            binding.appTitle.text = log.title
            binding.date.text = log.date
            binding.time.text = log.time
        }

        companion object {
            fun inflate(parent: ViewGroup): AppLogsViewHolder {
                return AppLogsViewHolder(
                    AppLogViewHolderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}