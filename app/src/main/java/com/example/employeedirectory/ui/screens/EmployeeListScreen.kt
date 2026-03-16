package com.example.employeedirectory.ui.screens

import kotlinx.coroutines.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.employeedirectory.model.Employee
import com.example.employeedirectory.model.EmployeeRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    employees: List<Employee>,
    onEmployeeClick: (Employee) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val filteredEmployees = remember(employees, searchQuery) {
        employees.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.cpfNo.contains(searchQuery, ignoreCase = true) ||
            it.department.contains(searchQuery, ignoreCase = true)
        }.sortedWith(
            compareByDescending<Employee> { it.isBookmarked }
                .thenBy { it.name }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About") },
            text = { Text("Made with Love, for IPEOT, by APS") },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Home",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.White
                    ) 
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("About") },
                                onClick = {
                                    showMenu = false
                                    showAboutDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Update")
                                        if (isUpdating) {
                                            Spacer(Modifier.width(8.dp))
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        }
                                    }
                                },
                                enabled = !isUpdating,
                                onClick = {
                                    showMenu = false
                                    isUpdating = true
                                    scope.launch {
                                        try {
                                            EmployeeRepository.refreshEmployees()
                                            snackbarHostState.showSnackbar("Updated successfully")
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Update failed")
                                        } finally {
                                            isUpdating = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3F51B5) // the deep blue top bar from the image
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE0F7F6)) // Light app background
                .padding(paddingValues)
        ) {
            // Search Bar Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF3F51B5))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                
                IconButton(onClick = { /* TODO Bookmark Filter */ }) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmarks", tint = Color.Gray)
                }
                
                IconButton(onClick = { /* TODO Advanced Filter */ }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.Gray)
                }
            }

            // List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEmployees) { employee ->
                    EmployeeCard(
                        employee = employee, 
                        onClick = { onEmployeeClick(employee) },
                        onBookmarkClick = { EmployeeRepository.toggleBookmark(employee.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeeCard(
    employee: Employee, 
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF80DEEA), // cyan/teal color from picture
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image wrapper
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White), // Use white backdrop like the Aman Sharma image
                contentAlignment = Alignment.Center
            ) {
                // If the user has an explicit image we'd load it here. 
                // Using placeholder if not.
                if (employee.imageUrl != null) {
                    // Implement AsyncImage or similar if real URLs exist
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.LightGray,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${employee.name}, ${employee.designation}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = employee.cpfNo,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = employee.section, // Picture lists the section, not department here
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (employee.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (employee.isBookmarked) Color(0xFF3F51B5) else Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
