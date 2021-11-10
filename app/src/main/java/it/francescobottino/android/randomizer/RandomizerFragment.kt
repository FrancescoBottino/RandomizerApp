package it.francescobottino.android.randomizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import it.francescobottino.android.randomizer.databinding.RandomizerMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.*
import kotlin.random.Random

class RandomizerFragment: Fragment() {
    lateinit var binding: RandomizerMainBinding
    private var _state: RandomizerStatePreferences? = null
    private val state: RandomizerStatePreferences
        get() {
            return _state ?: viewModels<RandomizerViewModel>().value.getState(requireContext()).also {
                _state = it
            }
        }

    val random = Random(Calendar.getInstance().time.time)
    lateinit var possibleNumbers: Set<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = RandomizerMainBinding.inflate(inflater, container, false)

        updatePossibleNumbers()

        binding.numberPicker.value = state.selectedNumber
        binding.numberPicker.setOnValueChangedListener { _, _, newVal ->
            state.selectedNumber = newVal
            updatePossibleNumbers()
        }

        binding.randomResult.text = state.resultNumber.toString()

        binding.randomizerButton.setOnClickListener {
            val resultNumber = try {
                extractRandom()
            } catch (_: Exception) {
                return@setOnClickListener
            }

            state.resultNumber = resultNumber

            runAnimation(resultNumber)
        }

        return binding.root
    }

    private fun updatePossibleNumbers() {
        val excludedNumbers = state.excludedNumbers
        val lastNumber = state.selectedNumber

        val allNumbers = (1..lastNumber).toSet()
        val onlyAllowed = allNumbers.minus(excludedNumbers)

        possibleNumbers = onlyAllowed
    }

    private fun extractRandom(): Int {
        return if(state.exclusionIsEnabled) {
            extractRandomMinusExcluded()
        } else {
            extractRandomInRange()
        }
    }

    private fun extractRandomInRange(): Int {
        val min = 1
        val max = state.selectedNumber + 1

        return random.nextInt(min, max)
    }

    private fun extractRandomMinusExcluded(): Int {
        val min = 0
        val max = possibleNumbers.size

        if(possibleNumbers.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_possible_numbers),
                Toast.LENGTH_LONG
            ).show()

            throw IllegalStateException()
        }

        val allowed = possibleNumbers.toList()
        val chosenIndex = random.nextInt(min, max)
        val chosenNumber = allowed[chosenIndex]

        return chosenNumber
    }

    private fun runAnimation(resultNumber: Int) {
        binding.randomizerButton.isClickable = false

        val min = 1
        val max = state.selectedNumber + 1

        MainScope().launch(Dispatchers.Main) {
            binding.randomResult.setAnimation(
                800,
                20,
                { step ->
                    random.nextInt(min, max)
                },
                {
                    binding.randomizerButton.isClickable = true
                    resultNumber
                }
            )
        }.invokeOnCompletion {
            binding.randomResult.text = resultNumber.toString()
        }
    }
}