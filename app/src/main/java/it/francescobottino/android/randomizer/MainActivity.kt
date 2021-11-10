package it.francescobottino.android.randomizer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity: NavDrawerActivity() {
    override val items: ArrayList<NavigationItemModel>
        get() {
            return arrayListOf(
                NavigationItemModel(R.drawable.ic_dices, getString(R.string.navigation_name_main_fragment)),
                NavigationItemModel(R.drawable.ic_baseline_info_24, getString(R.string.navigation_name_info)),
            )
        }
    override val initialFragment: Fragment
        get() = RandomizerFragment()
    override val navigationItemClickListener: (position: Int) -> Boolean
        get() {
            return { clicked ->
                false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        versionNameClickListener = {
            startActivity(Intent(this, CheatActivity::class.java))
        }
    }
}