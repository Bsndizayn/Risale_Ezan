package com.example.risaleezanvakticompose.presentation.screen.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.risaleezanvakticompose.data.local.entities.SavedLocation
import com.example.risaleezanvakticompose.data.model.CountriesItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionScreen(
    onLocationSelected: (SavedLocation) -> Unit,
    onBack: () -> Unit,
    viewModel: LocationSelectionViewModel = hiltViewModel()
) {
    val savedLocations by viewModel.savedLocations.collectAsState(initial = emptyList())
    val currentLocation by viewModel.currentLocation.collectAsState(initial = null)
    val currentStep by viewModel.currentStep.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val locationAdded by viewModel.locationAdded.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(locationAdded) {
        if (locationAdded) {
            snackbarHostState.showSnackbar("✅ Konum eklendi")
            viewModel.resetLocationAdded()
            onBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Konum Yönetimi") },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (selectedTabIndex == 0 && currentStep is SelectionStep.CountrySelection) {
                                onBack()
                            } else if (selectedTabIndex == 0) {
                                viewModel.goBackStep()
                            } else {
                                onBack()
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, "Geri")
                        }
                    }
                )

                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Yeni Ekle") },
                        icon = { Icon(Icons.Default.Add, null) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Kayıtlı (${savedLocations.size})") },
                        icon = { Icon(Icons.Default.List, null) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTabIndex == 0 && currentStep is SelectionStep.CountrySelection) {
                FloatingActionButton(onClick = { viewModel.useGpsLocation() }) {
                    Icon(Icons.Default.MyLocation, "GPS Konumu")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    NewLocationTab(
                        currentStep = currentStep,
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        isSearching = isSearching,
                        viewModel = viewModel,
                        keyboardController = keyboardController
                    )
                }
                1 -> {
                    SavedLocationsTab(
                        savedLocations = savedLocations,
                        currentLocation = currentLocation,
                        onLocationSelected = { location ->
                            viewModel.selectLocation(location)
                            onLocationSelected(location)
                            onBack()
                        },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) },
                        onDeleteLocation = { }
                    )
                }
            }
        }
    }
}

@Composable
fun NewLocationTab(
    currentStep: SelectionStep,
    searchQuery: String,
    searchResults: SearchResult?,
    isSearching: Boolean,
    viewModel: LocationSelectionViewModel,
    keyboardController: SoftwareKeyboardController?
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BreadcrumbBar(currentStep = currentStep)

        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onSearch = { keyboardController?.hide() },
            isSearching = isSearching,
            placeholder = getPlaceholder(currentStep),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        when (currentStep) {
            is SelectionStep.CountrySelection -> {
                CountryList(
                    searchResults = searchResults,
                    isSearching = isSearching,
                    onCountrySelected = { viewModel.onCountrySelected(it) }
                )
            }
            is SelectionStep.RegionSelection -> {
                RegionList(
                    searchResults = searchResults,
                    isSearching = isSearching,
                    country = currentStep.country,
                    onRegionSelected = { country, region ->
                        viewModel.onRegionSelected(country, region)
                    }
                )
            }
            is SelectionStep.RegionAction -> {
                RegionActionCard(
                    country = currentStep.country,
                    region = currentStep.region,
                    onAddAsLocation = {
                        viewModel.onAddRegionAsLocation(currentStep.country, currentStep.region)
                    },
                    onShowCities = {
                        viewModel.onShowCities(currentStep.country, currentStep.region)
                    }
                )
            }
            is SelectionStep.CitySelection -> {
                CityList(
                    searchResults = searchResults,
                    isSearching = isSearching,
                    country = currentStep.country,
                    region = currentStep.region,
                    onCitySelected = { country, region, city ->
                        viewModel.onCitySelected(country, region, city)
                    }
                )
            }
        }
    }
}

@Composable
fun SavedLocationsTab(
    savedLocations: List<SavedLocation>,
    currentLocation: SavedLocation?,
    onLocationSelected: (SavedLocation) -> Unit,
    onFavoriteToggle: (Int) -> Unit,
    onDeleteLocation: (SavedLocation) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (savedLocations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Henüz kayıtlı konum yok",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Yeni Ekle sekmesinden konum ekleyebilirsiniz",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tüm Konumlar",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${savedLocations.size} konum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(savedLocations) { location ->
                SavedLocationCard(
                    location = location,
                    isSelected = location.placeId == currentLocation?.placeId,
                    onClick = { onLocationSelected(location) },
                    onFavoriteToggle = { onFavoriteToggle(location.placeId) }
                )
            }
        }
    }
}

@Composable
fun SavedLocationCard(
    location: SavedLocation,
    isSelected: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (location.isCurrentLocation)
                        Icons.Default.LocationOn
                    else
                        Icons.Default.Place,
                    contentDescription = null,
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )

                Column {
                    Text(
                        text = location.placeName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${location.region}, ${location.country}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (location.isFavorite)
                            Icons.Default.Star
                        else
                            Icons.Default.StarBorder,
                        contentDescription = "Favori",
                        tint = if (location.isFavorite)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Aktif",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun BreadcrumbBar(currentStep: SelectionStep) {
    if (currentStep !is SelectionStep.CountrySelection) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            when (currentStep) {
                is SelectionStep.RegionSelection -> {
                    Text(
                        text = currentStep.country.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is SelectionStep.RegionAction -> {
                    Text(
                        text = currentStep.country.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Icon(Icons.Default.ChevronRight, null, Modifier.size(16.dp))
                    Text(
                        text = currentStep.region,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is SelectionStep.CitySelection -> {
                    Text(
                        text = currentStep.country.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Icon(Icons.Default.ChevronRight, null, Modifier.size(16.dp))
                    Text(
                        text = currentStep.region,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                else -> {}
            }
        }
        HorizontalDivider()
    }
}

fun getPlaceholder(step: SelectionStep): String {
    return when (step) {
        is SelectionStep.CountrySelection -> "Ülke ara..."
        is SelectionStep.RegionSelection -> "Bölge ara..."
        is SelectionStep.CitySelection -> "Şehir ara..."
        else -> "Ara..."
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isSearching: Boolean,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Ara")
        },
        trailingIcon = {
            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Temizle")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        shape = MaterialTheme.shapes.large
    )
}

@Composable
fun CountryList(
    searchResults: SearchResult?,
    isSearching: Boolean,
    onCountrySelected: (CountriesItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (searchResults is SearchResult.Countries) {
            items(searchResults.items) { country ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCountrySelected(country) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegionList(
    searchResults: SearchResult?,
    isSearching: Boolean,
    country: CountriesItem,
    onRegionSelected: (CountriesItem, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (searchResults is SearchResult.Regions) {
            items(searchResults.items) { region ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRegionSelected(country, region) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = region,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegionActionCard(
    country: CountriesItem,
    region: String,
    onAddAsLocation: () -> Unit,
    onShowCities: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "$region seçildi",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddAsLocation() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AddLocation,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$region'u ekle",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Bu bölgeyi konum olarak kaydet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowCities() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Şehirleri göster",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "$region içindeki şehirleri listele",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun CityList(
    searchResults: SearchResult?,
    isSearching: Boolean,
    country: CountriesItem,
    region: String,
    onCitySelected: (CountriesItem, String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (searchResults is SearchResult.Cities) {
            items(searchResults.items) { city ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCitySelected(country, region, city) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = city,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}