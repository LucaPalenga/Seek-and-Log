package com.example.seekandlog.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.seekandlog.databinding.SelectableAppsViewHolderBinding
import com.example.seekandlog.diffutils.SelectableAppDiffUtilCallback
import com.example.seekandlog.objs.SelectableApp

class SelectableAppsAdapter :
    RecyclerView.Adapter<SelectableAppsAdapter.SelectableAppsViewHolder>() {

    var apps: List<SelectableApp> = listOf()
        set(value) {
            field = value
            filteredApps = value
        }

    private var filteredApps: List<SelectableApp> = listOf()
        set(value) {
            val diffApps = DiffUtil.calculateDiff(SelectableAppDiffUtilCallback(field, value))
            field = value
            diffApps.dispatchUpdatesTo(this)
        }

    var onAppSelectionListener: OnAppSelectionChangedCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableAppsViewHolder {
        return SelectableAppsViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: SelectableAppsViewHolder, position: Int) {
        holder.bind(filteredApps[position]) { onAppSelectionListener?.onAppSelectionChanged() }
    }

    override fun getItemCount(): Int {
        return filteredApps.size
    }

    fun filter(constraints: String?) {
        constraints?.let {
            filteredApps =
                apps.filter { it.info.title.lowercase().contains(constraints.lowercase()) }
        } ?: run { filteredApps = apps }
    }


    class SelectableAppsViewHolder(private val binding: SelectableAppsViewHolderBinding) :
        ViewHolder(binding.root) {

        fun bind(app: SelectableApp, onSelectionChanged: () -> Unit) {
            binding.logo.setImageDrawable(app.logo)
            binding.title.text = app.info.title
            binding.checkBox.isChecked = app.selected

            binding.checkBox.setOnClickListener {
                app.selected = !app.selected
                onSelectionChanged()
            }
        }

        companion object {
            fun inflate(parent: ViewGroup): SelectableAppsViewHolder {
                return SelectableAppsViewHolder(
                    SelectableAppsViewHolderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    interface OnAppSelectionChangedCallback {
        fun onAppSelectionChanged()
    }
}