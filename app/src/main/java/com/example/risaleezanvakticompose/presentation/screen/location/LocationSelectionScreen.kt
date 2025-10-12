package com.example.risaleezanvakticompose.presentation.screen.location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.risaleezanvakticompose.data.local.entities.SavedLocation
import com.example.risaleezanvakticompose.data.model.CountriesItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionScreen(
    navController: NavController,
    viewModel: LocationSelectionViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val locationAdded by viewModel.locationAdded.collectAsState()
    val savedLocations by viewModel.savedLocations.collectAsState(initial = emptyList())
    val currentLocation by viewModel.currentLocation.collectAsState(initial = null)
    val needsLocationPermission by viewModel.needsLocationPermission.collectAsState()
    val isLoadingGps by viewModel.isLoadingGps.collectAsState()
    val locationSelected by viewModel.locationSelected.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(1) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    LaunchedEffect(locationAdded) {
        if (locationAdded) {
            navController.popBackStack()
            viewModel.resetLocationAdded()
        }
    }

    LaunchedEffect(locationSelected) {
        if (locationSelected) {
            navController.popBackStack()
            viewModel.resetLocationSelected()
        }
    }

    LaunchedEffect(needsLocationPermission) {
        if (needsLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Konum Seçimi") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedTabIndex == 1 && currentStep !is SelectionStep.CountrySelection) {
                            viewModel.goBackStep()
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                actions = {
                    if (isLoadingGps) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.useGpsLocation() }) {
                            Icon(Icons.Default.MyLocation, "GPS Konumumu Al")
                        }
                    }
                }
            )
        },
        snackbarHost = {
            errorMessage?.let { message ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Tamam")
                        }
                    }
                ) {
                    Text(message)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                    },
                    text = { Text("Kayıtlı Konumlar") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                    },
                    text = { Text("Yeni Ekle") }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    SavedLocationsTab(
                        savedLocations = savedLocations,
                        currentLocation = currentLocation,
                        onLocationSelected = { location ->
                            viewModel.selectLocation(location)
                        },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) },
                        onDeleteLocation = { }
                    )
                }
                1 -> {
                    NewLocationTab(
                        currentStep = currentStep,
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        searchResults = searchResults,
                        isSearching = isSearching,
                        keyboardController = keyboardController,
                        onCountrySelected = { viewModel.onCountrySelected(it) },
                        onRegionSelected = { country, region -> viewModel.onRegionSelected(country, region) },
                        onShowCities = { country, region -> viewModel.onShowCities(country, region) },
                        onAddRegionAsLocation = { country, region -> viewModel.onAddRegionAsLocation(country, region) },
                        onCitySelected = { country, region, city -> viewModel.onCitySelected(country, region, city) }
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
    onQueryChange: (String) -> Unit,
    searchResults: SearchResult?,
    isSearching: Boolean,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    onCountrySelected: (CountriesItem) -> Unit,
    onRegionSelected: (CountriesItem, String) -> Unit,
    onShowCities: (CountriesItem, String) -> Unit,
    onAddRegionAsLocation: (CountriesItem, String) -> Unit,
    onCitySelected: (CountriesItem, String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BreadcrumbBar(currentStep = currentStep)

        SearchBar(
            query = searchQuery,
            onQueryChange = { onQueryChange(it) },
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
                    onCountrySelected = { onCountrySelected(it) }
                )
            }
            is SelectionStep.RegionSelection -> {
                RegionList(
                    searchResults = searchResults,
                    isSearching = isSearching,
                    country = currentStep.country,
                    onRegionSelected = { country, region ->
                        onRegionSelected(country, region)
                    }
                )
            }
            is SelectionStep.RegionAction -> {
                RegionActionCard(
                    country = currentStep.country,
                    region = currentStep.region,
                    onAddAsLocation = {
                        onAddRegionAsLocation(currentStep.country, currentStep.region)
                    },
                    onShowCities = {
                        onShowCities(currentStep.country, currentStep.region)
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
                        onCitySelected(country, region, city)
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
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
                    onLocationSelected = { onLocationSelected(location) },
                    onFavoriteToggle = { onFavoriteToggle(location.placeId) },
                    onDelete = { onDeleteLocation(location) }
                )
            }
        }
    }
}

@Composable
fun BreadcrumbBar(currentStep: SelectionStep) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (currentStep) {
                is SelectionStep.CountrySelection -> {
                    Text(
                        text = "Ülke Seçin",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                is SelectionStep.RegionSelection -> {
                    Text(
                        text = currentStep.country.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Bölge Seçin",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                is SelectionStep.RegionAction -> {
                    Text(
                        text = currentStep.country.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = currentStep.region,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                is SelectionStep.CitySelection -> {
                    Text(
                        text = currentStep.country.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = currentStep.region,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Şehir Seçin",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
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
            Icon(Icons.Default.Search, "Ara")
        },
        trailingIcon = {
            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "Temizle")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
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
                    modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth(),
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
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SavedLocationCard(
    location: SavedLocation,
    isSelected: Boolean,
    onLocationSelected: (SavedLocation) -> Unit,
    onFavoriteToggle: (Int) -> Unit,
    onDelete: (SavedLocation) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLocationSelected(location) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    )  {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Seçili",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = location.placeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                        text = "${location.placeName}, ${location.region}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { onFavoriteToggle(location.placeId) }
            ) {
                Icon(
                    imageVector = if (location.isFavorite)
                        Icons.Default.Star
                    else
                        Icons.Default.StarBorder,
                    contentDescription = "Favori",
                    tint = if (location.isFavorite)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}