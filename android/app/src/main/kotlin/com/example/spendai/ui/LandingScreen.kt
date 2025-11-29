package com.example.spendai.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendai.models.Transaction
import com.example.spendai.services.OCRService
import com.example.spendai.viewmodels.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: TransactionViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ocrService = remember { OCRService(context) }
    var isProcessing by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                isProcessing = true
                scope.launch {
                    try {
                        val text = ocrService.extractText(uri)
                        val data = ocrService.parseReceiptData(text)
                        
                        val merchant = data["merchant"] as String
                        val amount = data["amount"] as Double
                        val date = data["date"] as String

                        val newTransaction = Transaction(
                            name = merchant,
                            category = "Shopping",
                            date = date,
                            amount = -amount,
                            type = "Expense"
                        )

                        if (viewModel.isDuplicate(newTransaction)) {
                            Toast.makeText(context, "Duplicate transaction detected!", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.addTransaction(newTransaction)
                            Toast.makeText(context, "Transaction added successfully!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isProcessing = false
                    }
                }
            }
        }
    )

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
        floatingActionButton = {
            AssistantFab(onPressed = { showAssistant = true })
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Normal) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "spendAI",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Connected Cards",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No cards yet. Add one to get started.",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            DashedButton(
                onClick = { Toast.makeText(context, "Card integration coming soon!", Toast.LENGTH_SHORT).show() },
                icon = Icons.Default.Add,
                label = "Add New Card"
            )
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Spending by Date",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No receipts yet. Add one to see your spending.",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            DashedButton(
                onClick = {
                    if (!isProcessing) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                },
                icon = Icons.Default.CameraAlt,
                label = if (isProcessing) "Processing..." else "Add Receipt from Photos"
            )
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onNavigateToDashboard,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text("View Full Dashboard")
            }
        }
    }
}

@Composable
fun DashedButton(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    // Note: Jetpack Compose doesn't have a built-in dashed border modifier easily accessible without custom drawing.
    // For simplicity, using a solid border with a specific color to match the design intent.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color(0xFF2196F3),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = Color(0xFF2196F3),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
