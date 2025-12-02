package com.shadowcheck.mobile.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun APIKeysScreen(
    onBack: () -> Unit,
    onSaveWigleKey: (String, String) -> Unit
) {
    var wigleApiName by remember { mutableStateOf("") }
    var wigleApiToken by remember { mutableStateOf("") }
    var showToken by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            color = ShadowCheckColors.Surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Text(
                    text = "API Keys",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // WiGLE API
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ShadowCheckColors.Surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "WiGLE API",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Enter your WiGLE API credentials to upload and search networks",
                        style = MaterialTheme.typography.bodySmall,
                        color = ShadowCheckColors.TextSecondary
                    )
                    
                    OutlinedTextField(
                        value = wigleApiName,
                        onValueChange = { wigleApiName = it },
                        label = { Text("API Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, null) }
                    )
                    
                    OutlinedTextField(
                        value = wigleApiToken,
                        onValueChange = { wigleApiToken = it },
                        label = { Text("API Token") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showToken) VisualTransformation.None 
                            else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Key, null) },
                        trailingIcon = {
                            IconButton(onClick = { showToken = !showToken }) {
                                Icon(
                                    if (showToken) Icons.Default.VisibilityOff 
                                    else Icons.Default.Visibility,
                                    "Toggle visibility"
                                )
                            }
                        }
                    )
                    
                    Button(
                        onClick = { onSaveWigleKey(wigleApiName, wigleApiToken) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = wigleApiName.isNotEmpty() && wigleApiToken.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save WiGLE Credentials")
                    }
                    
                    TextButton(
                        onClick = { /* Open WiGLE website */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Get WiGLE API Key")
                        Icon(Icons.Default.OpenInNew, null)
                    }
                }
            }
            
            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ShadowCheckColors.Primary.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = ShadowCheckColors.Primary
                    )
                    Column {
                        Text(
                            text = "Secure Storage",
                            fontWeight = FontWeight.Bold,
                            color = ShadowCheckColors.Primary
                        )
                        Text(
                            text = "API keys are encrypted and stored securely on your device using AndroidX Security library.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
