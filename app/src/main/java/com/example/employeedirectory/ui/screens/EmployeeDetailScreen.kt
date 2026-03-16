package com.example.employeedirectory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.employeedirectory.model.Employee

@Composable
fun EmployeeDetailScreen(
    employee: Employee,
    onCloseClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA0EBE9)) // Match detail page cyan color
            .verticalScroll(rememberScrollState())
    ) {
        // Top section (Image)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.LightGray)
        ) {
            // Placeholder for the large image
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp)
            )

            // Close button
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }

        // Details section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Bookmark",
                tint = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "${employee.name}, ${employee.designation}",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF2E3D49),
                textDecoration = TextDecoration.Underline
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = employee.cpfNo,
                fontSize = 18.sp,
                color = Color(0xFF2E3D49)
            )
            
            Text(
                text = employee.department,
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Call Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                IconButton(
                    onClick = onCallClick,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color(0xFF4DB6AC)
                    )
                }
                Text(
                    text = "CALL",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            DetailItem(label = "NAME", value = "${employee.name},")
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem(label = "DESIGNATION", value = employee.designation)
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem(label = "CPF NO", value = employee.cpfNo)
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem(label = "SECTION", value = employee.section)
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem(label = "DEPARTMENT", value = employee.department)
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem(label = "EXT NO", value = employee.extNo)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF7A9D96), // Light muted teal color for the label
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color(0xFF2E3D49)
        )
    }
}
