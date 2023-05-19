package com.example.seekandlog.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.seekandlog.objs.SelectableApp

class SelectableAppDiffUtilCallback(
    private val old: List<SelectableApp>,
    private val new: List<SelectableApp>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size
    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition].info.uid == new[newItemPosition].info.uid

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition] == new[newItemPosition]

}