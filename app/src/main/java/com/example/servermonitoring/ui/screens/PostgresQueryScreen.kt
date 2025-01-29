package com.example.servermonitoring.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.servermonitoring.ui.theme.ServerMonitoringTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.servermonitoring.model.Container
import com.example.servermonitoring.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Database(val name: String)
data class Table(val name: String)
data class Column(val name: String, val type: String)

@Composable
fun PostgresQueryScreen(
    container: Container,
    onBack: () -> Unit,
    onNavigateToResult: (List<Map<String, Any>>) -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    var databases by remember { mutableStateOf<List<Database>>(emptyList()) }
    var tables by remember { mutableStateOf<List<Table>>(emptyList()) }
    var columns by remember { mutableStateOf<List<Column>>(emptyList()) }

    var selectedDatabase by remember { mutableStateOf<String?>(null) }
    var selectedTables by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedColumns by remember { mutableStateOf<Set<String>>(emptySet()) }
    var whereClause by remember { mutableStateOf("") }
    var orderByClause by remember { mutableStateOf("") }

    var showDatabaseMenu by remember { mutableStateOf(false) }
    var showTableMenu by remember { mutableStateOf(false) }
    var showColumnMenu by remember { mutableStateOf(false) }

    LaunchedEffect(container.Id) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dbNames = RetrofitInstance.api.getDatabases(container.Id)
                databases = dbNames.map { Database(it) }
            } catch (e: Exception) {
            }
        }
    }

    LaunchedEffect(selectedDatabase) {
        if (selectedDatabase != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tableNames = RetrofitInstance.api.getTables(container.Id, selectedDatabase!!)
                    tables = tableNames.map { Table(it) }
                } catch (e: Exception) {
                }
            }
        }
    }

    LaunchedEffect(selectedTables) {
        if (selectedTables.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val columnNames = RetrofitInstance.api.getTableColumns(container.Id, selectedDatabase!!, selectedTables.first())
                    columns = columnNames.map { Column(it, "VARCHAR") }
                } catch (e: Exception) {
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Query Builder for ${shortenContainerId(container.Id)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Select Database",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { showDatabaseMenu = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(selectedDatabase ?: "Select Database")
            }
            DropdownMenu(
                expanded = showDatabaseMenu,
                onDismissRequest = { showDatabaseMenu = false }
            ) {
                databases.forEach { database ->
                    DropdownMenuItem(
                        text = { Text(database.name) },
                        onClick = {
                            selectedDatabase = database.name
                            showDatabaseMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDatabase != null) {
            Text(
                text = "Select Tables",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showTableMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (selectedTables.isEmpty()) "Select Tables" else selectedTables.joinToString(", "))
                }
                DropdownMenu(
                    expanded = showTableMenu,
                    onDismissRequest = { showTableMenu = false }
                ) {
                    tables.forEach { table ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedTables.contains(table.name),
                                        onCheckedChange = { checked ->
                                            selectedTables = if (checked) {
                                                selectedTables + table.name
                                            } else {
                                                selectedTables - table.name
                                            }
                                        }
                                    )
                                    Text(text = table.name)
                                }
                            },
                            onClick = { }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTables.isNotEmpty()) {
            Text(
                text = "Select Columns",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showColumnMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (selectedColumns.isEmpty()) "Select Columns" else selectedColumns.joinToString(", "))
                }
                DropdownMenu(
                    expanded = showColumnMenu,
                    onDismissRequest = { showColumnMenu = false }
                ) {
                    columns.forEach { column ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedColumns.contains(column.name),
                                        onCheckedChange = { checked ->
                                            selectedColumns = if (checked) {
                                                selectedColumns + column.name
                                            } else {
                                                selectedColumns - column.name
                                            }
                                        }
                                    )
                                    Text(text = "${column.name} (${column.type})")
                                }
                            },
                            onClick = { }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedColumns.isNotEmpty()) {
            Text(
                text = "WHERE Clause",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = whereClause,
                onValueChange = { whereClause = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("exmpl: id > 10") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedColumns.isNotEmpty()) {
            Text(
                text = "ORDER BY Clause",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = orderByClause,
                onValueChange = { orderByClause = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("exmpl: id ASC") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDatabase != null && selectedTables.isNotEmpty() && selectedColumns.isNotEmpty()) {
            Button(
                onClick = {
                    val query = buildQuery(selectedTables, selectedColumns, whereClause, orderByClause)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val result = RetrofitInstance.api.executeComplexQuery(container.Id, selectedDatabase!!, query)
                            onNavigateToResult(result)
                        } catch (e: Exception) {
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Execute Query")
            }
        }
    }
}

fun buildQuery(
    tables: Set<String>,
    columns: Set<String>,
    whereClause: String,
    orderByClause: String
): String {
    val selectedTables = tables.joinToString(", ")
    val selectedColumns = if (columns.isEmpty()) "*" else columns.joinToString(", ")
    return """
        SELECT $selectedColumns 
        FROM $selectedTables 
        ${if (whereClause.isNotEmpty()) "WHERE $whereClause" else ""}
        ${if (orderByClause.isNotEmpty()) "ORDER BY $orderByClause" else ""}
    """.trimIndent()
}

@Preview(showBackground = true)
@Composable
fun PostgresQueryScreenPreview() {
    ServerMonitoringTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PostgresQueryScreen(
                container = Container("1", "postgres:latest", "Running", "state"),
                onBack = {},
                onNavigateToResult = {},
                onStartClick = {},
                onStopClick = {},
                onRestartClick = {}
            )
        }
    }
}