package com.serranoie.app.feature.travel.navigation

object TravelDestinations {
    const val TRAVEL_ROUTE = "travel"
    const val TRAVEL_LIST_ROUTE = "$TRAVEL_ROUTE/list"
    const val CREATE_TRAVEL_ROUTE = "$TRAVEL_ROUTE/create"
    const val TRAVEL_DETAIL_ROUTE = "$TRAVEL_ROUTE/detail/{travelId}"
    
    fun travelDetailRoute(travelId: String): String = "$TRAVEL_ROUTE/detail/$travelId"
}

@Composable
fun TravelNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = TravelDestinations.TRAVEL_LIST_ROUTE,
    travelViewModel: TravelViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        travelGraph(
            navController = navController,
            travelViewModel = travelViewModel
        )
    }
}

fun NavGraphBuilder.travelGraph(
    navController: NavHostController,
    travelViewModel: TravelViewModel
) {
    composable(TravelDestinations.TRAVEL_LIST_ROUTE) {
        TravelListScreen(
            viewModel = travelViewModel,
            onCreateTravelClick = {
                navController.navigate(TravelDestinations.CREATE_TRAVEL_ROUTE)
            },
            onTravelClick = { travelId ->
                navController.navigate(TravelDestinations.travelDetailRoute(travelId))
            }
        )
    }
    
    composable(TravelDestinations.CREATE_TRAVEL_ROUTE) {
        CreateTravelScreen(
            viewModel = travelViewModel,
            onTravelCreated = {
                navController.popBackStack()
            }
        )
    }
    
    composable(
        route = TravelDestinations.TRAVEL_DETAIL_ROUTE,
        arguments = listOf(
            navArgument("travelId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("travelId") ?: ""
        // TODO: Implement Travel Detail Screen
        // For now we'll just navigate back to the list
        navController.popBackStack()
    }
}
