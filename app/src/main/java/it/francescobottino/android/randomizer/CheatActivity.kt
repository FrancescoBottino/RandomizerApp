package it.francescobottino.android.randomizer

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import it.francescobottino.android.randomizer.databinding.ActivityCheatBinding
import it.francescobottino.android.randomizer.databinding.DialogChooseExcludedNumberBinding
import java.util.*
import kotlin.random.Random

class CheatActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheatBinding
    private var _state: RandomizerStatePreferences? = null
    private val state: RandomizerStatePreferences
        get() {
            return _state ?: viewModels<RandomizerViewModel>().value.getState(this).also {
                _state = it
            }
        }

    private lateinit var adapter: FastItemAdapter<GenericItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.cheat_activity_name)

        adapter = FastItemAdapter()

        binding.ecludedNumbersRecyclerview.let { rv ->
            rv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            rv.adapter = adapter
        }

        binding.exclusionEnabledSwitch.isChecked = state.exclusionIsEnabled
        binding.exclusionEnabledSwitch.setOnCheckedChangeListener { _, checked ->
            state.exclusionIsEnabled = checked
        }

        binding.addExcludedNumberButton.setOnClickListener {
            chooseNumber {
                onNumberAdd(it)
            }
        }

        loadExcludedNumbersFromState()
    }

    private fun chooseNumber(onNumberChosen: (Int)->Unit) {
        val content = DialogChooseExcludedNumberBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.exclude_number_chooser_dialog)
            .setView(content.root)
            .setPositiveButton(R.string.add) { dialog, _ ->
                onNumberChosen(content.numberPicker.value)
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun loadExcludedNumbersFromState() {
        val excludedNumbers = state.excludedNumbers.toList().sortedBy { it }
        val binders = excludedNumbers.map {
            ExcludedNumber(it, this::onNumberDelete)
        }

        val diffResult = FastAdapterDiffUtil.calculateDiff(adapter.itemAdapter, binders)
        FastAdapterDiffUtil[adapter.itemAdapter] = diffResult
    }

    private fun onNumberDelete(number: ExcludedNumber) {
        state.excludedNumbers -= number.value
        adapter.remove( adapter.getPosition(number) )
    }

    private fun onNumberAdd(value: Int) {
        state.excludedNumbers += value
        adapter.add(ExcludedNumber(value, this::onNumberDelete))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}