package com.example.employeedirectory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.employeedirectory.model.EmployeeRepository
import com.example.employeedirectory.ui.screens.EmployeeDetailScreen
import com.example.employeedirectory.ui.screens.EmployeeListScreen
import com.example.employeedirectory.ui.theme.EmployeeDirectoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmployeeDirectoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EmployeeApp()
                }
            }
        }
    }
}

@Composable
fun EmployeeApp() {
    val navController = rememberNavController()
    val employees by EmployeeRepository.employees.collectAsState()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            EmployeeListScreen(
                employees = employees,
                onEmployeeClick = { employee ->
                    navController.navigate("detail/${employee.id}")
                }
            )
        }
        
        composable(
            route = "detail/{employeeId}",
            arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId")
            val employee = employeeId?.let { EmployeeRepository.getEmployeeById(it) }
            val context = LocalContext.current
            
            if (employee != null) {
                EmployeeDetailScreen(
                    employee = employee,
                    onCloseClick = {
                        navController.popBackStack()
                    },
                    onCallClick = {
                        val sanitizedNumber = employee.phoneNumber.filter { it.isDigit() }
                        val uri = android.net.Uri.fromParts("tel", sanitizedNumber, null)
                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, uri)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}
