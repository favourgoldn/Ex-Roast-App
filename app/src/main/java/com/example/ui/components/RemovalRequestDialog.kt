package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExRoastTheme

@Composable
fun RemovalRequestDialog(
    postId: String,
    onDismiss: () -> Unit,
    onSubmit: (email: String, explanation: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ExRoastTheme.colors.surface,
        title = {
            Text(
                text = "This Story is About Me",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ExRoastTheme.colors.text
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "If you believe this story refers to you or contains identifying information, submit this removal request. Posts flagged here are immediately prioritized and temporarily hidden pending review.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ExRoastTheme.colors.textMuted
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isError = false
                    },
                    label = { Text("Your contact email") },
                    placeholder = { Text("name@example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    isError = isError && email.isBlank()
                )

                OutlinedTextField(
                    value = explanation,
                    onValueChange = {
                        explanation = it
                        isError = false
                    },
                    label = { Text("Why do you believe this is about you?") },
                    placeholder = { Text("Describe identifying details or why this content breaches safety policy...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3,
                    maxLines = 5,
                    isError = isError && explanation.isBlank()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotBlank() && explanation.isNotBlank()) {
                        onSubmit(email.trim(), explanation.trim())
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ExFlame),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ExRoastTheme.colors.textMuted)
            }
        }
    )
}
