package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.SetoranRepository
import com.example.data.SettingsManager
import com.example.ui.BottomNavigationBar
import com.example.ui.MainNavGraph
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "setoran_database"
        ).fallbackToDestructiveMigration().build()
        val settingsManager = SettingsManager(applicationContext)
        val repository = SetoranRepository(db.setoranDao(), settingsManager)
        val viewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                        MainNavGraph(navController = navController, viewModel = viewModel)
                    }
                }
            }
        }
    }
}
