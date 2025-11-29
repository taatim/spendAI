package com.example.spendai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendai.viewmodels.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    onNavigateToTransactions: () -> Unit,
    viewModel: TransactionViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val totalBalance = transactions.sumOf { it.amount }
    
    // Calculate assets and liabilities for demo purposes
    val assets = transactions.filter { it.amount > 0 }.sumOf { it.amount }
    val liabilities = transactions.filter { it.amount < 0 }.sumOf { it.amount }

    var showAssistant by remember { mutableStateOf(false) }

    if (showAssistant) {
        ModalBottomSheet(
            onDismissRequest = { showAssistant = false },
            containerColor = Color.White
        ) {
            AssistantPanel(onClose = { showAssistant = false })
        }
    }

    Scaffold(
        containerColor = Color.Black, // Dark theme base
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "sure.",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
                IconButton(onClick = { showAssistant = true }) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Assistant",
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToTransactions, // Or open camera directly
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NET WORTH",
                color = Color.Gray,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.US).format(totalBalance),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-2).sp
            )
            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "ASSETS", amount = assets, color = Color(0xFF4CAF50))
                StatItem(label = "LIABILITIES", amount = liabilities, color = Color.White) // White for neutral/negative in dark mode
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Button(
                onClick = onNavigateToTransactions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ),
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("View All Transactions")
            }
        }
    }
}

@Composable
fun StatItem(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = NumberFormat.getCurrencyInstance(Locale.US).format(amount),
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
