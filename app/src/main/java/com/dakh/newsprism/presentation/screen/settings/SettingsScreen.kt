package com.dakh.newsprism.presentation.screen.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dakh.newsprism.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {

    val state = viewModel.state.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            viewModel.processCommand(SettingsCommand.SwitchNotificationStatus(it))
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 8.dp)
                            .clickable {
                                onBackClick()
                            },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            )
        }
    ) { innerPadding ->
        when (val currentState = state.value) {
            is SettingsState.DisplaySettings -> {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        SettingCard(
                            modifier = Modifier,
                            title = stringResource(R.string.search_language),
                            description = stringResource(R.string.select_language_for_news_search)
                        ) {
                            MenuDropdown(
                                modifier = Modifier,
                                options = currentState.languages,
                                selected = currentState.settings.language,
                                onSelectedChange = {
                                    viewModel.processCommand(SettingsCommand.SwitchLanguage(it))
                                },
                                optionAsString = {
                                    it.asString()
                                }
                            )
                        }
                    }
                    item {
                        SettingCard(
                            modifier = Modifier,
                            title = stringResource(R.string.update_interval),
                            description = stringResource(R.string.how_often_to_update_news)
                        ) {
                            MenuDropdown(
                                modifier = Modifier,
                                options = currentState.intervals,
                                selected = currentState.settings.interval,
                                onSelectedChange = {
                                    viewModel.processCommand(SettingsCommand.SwitchUpdateInterval(it))
                                },
                                optionAsString = {
                                    it.asString()
                                }
                            )
                        }
                    }
                    item {
                        SettingCard(
                            modifier = Modifier,
                            title = stringResource(R.string.notifications),
                            description = stringResource(R.string.show_notifications_about_new_articles)
                        ) {
                            Switch(
                                modifier = Modifier,
                                checked = currentState.settings.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.processCommand(
                                            SettingsCommand.SwitchNotificationStatus(
                                                enabled
                                            )
                                        )
                                    }
                                },
                                enabled = true
                            )
                        }
                    }
                    item {
                        SettingCard(
                            modifier = Modifier,
                            title = stringResource(R.string.update_only_via_wi_fi),
                            description = stringResource(R.string.save_mobile_data)
                        ) {
                            Switch(
                                modifier = Modifier,
                                checked = currentState.settings.wifiOnly,
                                onCheckedChange = {
                                    viewModel.processCommand(
                                        SettingsCommand.SwitchWifiOnlyStatus(
                                            it
                                        )
                                    )
                                },
                                enabled = true
                            )
                        }
                    }
                }
            }

            SettingsState.Initial -> {}
        }
    }
}

@Composable
fun SettingCard(
    modifier: Modifier,
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MenuDropdown(
    modifier: Modifier = Modifier,
    options: List<T>,
    selected: T,
    onSelectedChange: (T) -> Unit,
    optionAsString: @Composable (T) -> String,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                )
                .fillMaxWidth(),
            value = optionAsString(selected),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = optionAsString(option)) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

