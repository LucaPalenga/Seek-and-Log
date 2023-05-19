package com.example.seekandlog.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seekandlog.databinding.MonitoredAppsViewHolderBinding
import com.example.seekandlog.objs.SelectableAppInfo

class MonitoredAppsAdapter : RecyclerView.Adapter<MonitoredAppsAdapter.MonitoredAppsViewHolder>() {

    var monitoredApps: List<SelectableAppInfo> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitoredAppsViewHolder {
        return MonitoredAppsViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: MonitoredAppsViewHolder, position: Int) {
        holder.bind(monitoredApps[position])
    }

    override fun getItemCount(): Int {
        return monitoredApps.size
    }


    class MonitoredAppsViewHolder(private val binding: MonitoredAppsViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appDescription: SelectableAppInfo) {
            binding.title.text = appDescription.title
        }

        companion object {
            fun inflate(parent: ViewGroup): MonitoredAppsViewHolder {
                return MonitoredAppsViewHolder(
                    MonitoredAppsViewHolderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}