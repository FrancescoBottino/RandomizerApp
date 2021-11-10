package it.francescobottino.android.randomizer

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import it.francescobottino.android.randomizer.databinding.RowExcludedNumberBinding

class ExcludedNumber(
    val value: Int,
    val deleteListener: ((number: ExcludedNumber)->Unit)? = null
): AbstractItem<ExcludedNumber.ViewHolder>() {

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<ExcludedNumber>(itemView) {
        val binding = RowExcludedNumberBinding.bind(itemView)

        override fun bindView(item: ExcludedNumber, payloads: List<Any>) {
            binding.numberLabel.text = item.value.toString()
            binding.deleteButton.setOnClickListener {
                item.deleteListener?.invoke(item)
            }
        }

        override fun unbindView(item: ExcludedNumber) {
            binding.numberLabel.text = null
            binding.deleteButton.setOnClickListener(null)
        }
    }

    override val layoutRes: Int
        get() = R.layout.row_excluded_number
    override val type: Int
        get() = R.id.excluded_number

    override fun getViewHolder(v: View) = ViewHolder(v)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExcludedNumber) return false
        if (!super.equals(other)) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value
        return result
    }
}