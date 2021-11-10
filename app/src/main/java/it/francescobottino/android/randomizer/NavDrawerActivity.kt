package it.francescobottino.android.randomizer

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import it.francescobottino.android.randomizer.databinding.DrawerActivityLayoutBinding
import kotlinx.coroutines.*

abstract class NavDrawerActivity: AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter
    lateinit var binding: DrawerActivityLayoutBinding

    internal abstract val items: ArrayList<NavigationItemModel>
    internal abstract val initialFragment: Fragment
    internal abstract val navigationItemClickListener: (position:Int)->Boolean

    var versionNameClickListener: (()->Unit)? = null
    var logoClickListener: (()->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrawerActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout

        // Set the toolbar
        setSupportActionBar(binding.toolbar)

        // Setup Recyclerview's Layout
        binding.navigationRv.layoutManager = LinearLayoutManager(this)
        binding.navigationRv.setHasFixedSize(true)

        // Add Item Touch Listener
        binding.navigationRv.addOnItemTouchListener(RecyclerViewTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                val shouldHighlight = navigationItemClickListener.invoke(position)

                if (shouldHighlight) {
                    updateAdapter(position)
                }
                MainScope().launch(Dispatchers.IO) {
                    delay(200)
                    withContext(Dispatchers.Main) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                }
            }
        }))

        updateAdapter(0)

        val homeFragment = initialFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_activity_content_id, homeFragment).commit()

        val toggle: ActionBarDrawerToggle = object: ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        // Set Header Image
        binding.navigationHeaderImg.setImageResource(R.drawable.logo)

        // Set background of Drawer
        binding.navigationLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))

        binding.navigationFooter.text = getString(R.string.version, BuildConfig.VERSION_NAME)

        binding.navigationFooter.setOnClickListener {
            versionNameClickListener?.invoke()
        }

        binding.navigationHeaderImg.setOnClickListener {
            logoClickListener?.invoke()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            } else {
                // Exit the app
                super.onBackPressed()
            }
        }
    }

    private fun updateAdapter(highlightItemPos: Int) {
        adapter = NavigationRVAdapter(items, highlightItemPos)
        binding.navigationRv.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}