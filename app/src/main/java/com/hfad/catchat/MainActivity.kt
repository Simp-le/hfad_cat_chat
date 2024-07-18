package com.hfad.catchat

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.keyIterator
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DynamicColors.applyIfAvailable(this)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the navigation controller from the navigation host
        // Using the findNavController() function from onCreate() may cause a crash. So here's the way to get the NavController from onCreate()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val originalNavigationBarColor = MaterialColors.getColor(bottomNavView, com.google.android.material.R.attr.colorSurfaceContainer)
        val drawerBackgroundColor = MaterialColors.getColor(drawer, com.google.android.material.R.attr.colorSurfaceContainerLow)


        // Drawer
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                window.navigationBarColor = blendColors(originalNavigationBarColor, drawerBackgroundColor, slideOffset)
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setupWithNavController(navController)

        //region Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Creating a configuration connecting the toolbar to the navigation graph
        // val builder = AppBarConfiguration.Builder(navController.graph) // Then there will be Up button if the destination is not home screen
        // Getting navGraph for the main pages that will be in the bottom navigation
        val mainNav = navController.graph.nodes.get(R.id.main_nav) as NavGraph
        val builder = AppBarConfiguration.Builder(mainNav.nodes.keyIterator().asSequence().toSet())
        builder.setOpenableLayout(drawer)
        val appBarConfiguration = builder.build()

        // Apply created configuration to the toolbar. Now we have the "Up" button and the label of the current fragment on the toolbar
        toolbar.setupWithNavController(navController, appBarConfiguration)
        //endregion

        // Bottom Navigation
        bottomNavView.setupWithNavController(navController)
        window.navigationBarColor = originalNavigationBarColor
    }

    //region Toolbar
    // Add all the items from the menu to the toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Navigate to a destination when an item's clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
    //endregion

    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
        return ArgbEvaluator().evaluate(ratio, from, to) as Int
    }
}
