package com.serranoie.app.feature.welcome

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelListScreen(
    viewModel: TravelViewModel,
    onCreateTravelClick: () -> Unit,
    onTravelClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val travels by viewModel.travels.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(key1 = true) {
        viewModel.getAllTravels()
        
        viewModel.uiState.collectLatest { state ->
            if (state is TravelUiState.Error) {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Trips") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTravelClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState is TravelUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                travels.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No trips found",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button to create your first trip",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = padding
                    ) {
                        items(travels) { travel ->
                            TravelItem(
                                travel = travel,
                                onClick = { onTravelClick(travel.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TravelItem(
    travel: Travel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = travel.destination,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row {
                Text(
                    text = "From:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = travel.startDate,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "To:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = travel.endDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = travel.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row {
                Text(
                    text = "Accommodation:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = travel.accommodation,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (travel.isOwner) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You are the owner",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
