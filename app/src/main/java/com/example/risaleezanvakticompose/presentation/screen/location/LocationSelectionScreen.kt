package com.example.risaleezanvakticompose.presentation.screen.location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

//    Box(modifier = Modifier.fillMaxSize()) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFF1E3A8A),
//                            Color(0xFF7C3AED),
//                            Color(0xFFF97316)
//                        )
//                    )
//                )
//        )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        Column(modifier = Modifier.fillMaxSize()) {
            GlassLocationTopBar(
                isLoadingGps = isLoadingGps,
                onBackClick = {
                    if (selectedTabIndex == 1 && currentStep !is SelectionStep.CountrySelection) {
                        viewModel.goBackStep()
                    } else {
                        navController.popBackStack()
                    }
                },
                onGpsClick = { viewModel.useGpsLocation() }
            )

            GlassTabRow(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

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
                        onRegionSelected = { country, region ->
                            viewModel.onRegionSelected(
                                country,
                                region
                            )
                        },
                        onShowCities = { country, region ->
                            viewModel.onShowCities(
                                country,
                                region
                            )
                        },
                        onAddRegionAsLocation = { country, region ->
                            viewModel.onAddRegionAsLocation(
                                country,
                                region
                            )
                        },
                        onCitySelected = { country, region, city ->
                            viewModel.onCitySelected(
                                country,
                                region,
                                city
                            )
                        }
                    )
                }
            }
        }

        errorMessage?.let { message ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Tamam", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassLocationTopBar(
    isLoadingGps: Boolean,
    onBackClick: () -> Unit,
    onGpsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()

            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        "Geri",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Konum Seçimi",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isLoadingGps) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                IconButton(
                    onClick = onGpsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        "GPS",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun GlassTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                GlassTab(
                    text = "Kayıtlı Konumlar",
                    selected = selectedTabIndex == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f)
                )
                GlassTab(
                    text = "Yeni Ekle",
                    selected = selectedTabIndex == 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun GlassTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) Color.White.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
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

        GlassSearchBar(
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (savedLocations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(20.dp)
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
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Henüz kayıtlı konum yok",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Yeni Ekle sekmesinden konum ekleyebilirsiniz",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
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
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${savedLocations.size} konum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp)
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
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    is SelectionStep.RegionSelection -> {
                        Text(
                            text = currentStep.country.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Bölge Seçin",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    is SelectionStep.RegionAction -> {
                        Text(
                            text = currentStep.country.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = currentStep.region,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    is SelectionStep.CitySelection -> {
                        Text(
                            text = currentStep.country.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = currentStep.region,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Şehir Seçin",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
fun GlassSearchBar(
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
        placeholder = {
            Text(placeholder, color = Color.White.copy(alpha = 0.6f))
        },
        leadingIcon = {
            Icon(Icons.Default.Search, "Ara", tint = Color.White.copy(alpha = 0.8f))
        },
        trailingIcon = {
            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "Temizle", tint = Color.White.copy(alpha = 0.8f))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
            cursorColor = Color.White
        )
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        } else if (searchResults is SearchResult.Countries) {
            items(searchResults.items) { country ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCountrySelected(country) },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
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
                                tint = Color.White
                            )
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f)
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        } else if (searchResults is SearchResult.Regions) {
            items(searchResults.items) { region ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRegionSelected(country, region) },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
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
                                tint = Color.White
                            )
                            Text(
                                text = region,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f)
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
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddAsLocation() },
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(20.dp)
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
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$region'u ekle",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bu bölgeyi konum olarak kaydet",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowCities() },
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(20.dp)
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
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Şehirleri göster",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "$region içindeki şehirleri listele",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f)
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        } else if (searchResults is SearchResult.Cities) {
            items(searchResults.items) { city ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCitySelected(country, region, city) },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
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
                                tint = Color.White
                            )
                            Text(
                                text = city,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
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
                Color.White.copy(alpha = 0.25f)
            else
                Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
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
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = location.placeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        text = "${location.placeName}, ${location.region}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

        }
    }
}