package com.example.joiefull.presentation.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.joiefull.presentation.detail.DetailScreen
import com.example.joiefull.presentation.home.HomeScreen
import com.example.joiefull.presentation.home.HomeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{clothesId}") {
        fun createRoute(clothesId: Int) = "detail/$clothesId"
    }
}

// Seuil tablette : largeur >= 600dp
private const val TABLET_BREAKPOINT = 600

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val isTablet = screenWidth >= TABLET_BREAKPOINT
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as Application
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val repository = com.example.joiefull.data.repository.ClothesRepositoryImpl(
                    com.example.joiefull.data.remote.RetrofitInstance.api
                )
                val useCase = com.example.joiefull.domain.usecase.GetClothesUseCase(repository)
                return HomeViewModel(application, useCase) as T
            }
        }
    )

    if (isTablet) {
        TabletLayout(viewModel = homeViewModel, modifier = modifier)
    } else {
        PhoneLayout(viewModel = homeViewModel, modifier = modifier)
    }
}

// ── TÉLÉPHONE : navigation classique ────────────────────────────────────────

@Composable
private fun PhoneLayout(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onClothesClick = { clothesId ->
                    navController.navigate(Screen.Detail.createRoute(clothesId))
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("clothesId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clothesId = backStackEntry.arguments?.getInt("clothesId") ?: return@composable
            DetailScreen(
                clothesId = clothesId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// ── TABLETTE : layout deux colonnes ─────────────────────────────────────────

@Composable
private fun TabletLayout(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Colonne gauche : liste (2/3)
        HomeScreen(
            viewModel = viewModel,
            onClothesClick = { clothesId -> viewModel.selectClothes(clothesId) },
            isTablet = true,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.667f)
        )

        // Colonne droite : détail (1/3) — blanc si rien de sélectionné
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White)
        ) {
            uiState.selectedClothesId?.let { clothesId ->
                DetailScreen(
                    clothesId = clothesId,
                    viewModel = viewModel,
                    onBack = { /* pas de retour sur tablette */ },
                    isTablet = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}