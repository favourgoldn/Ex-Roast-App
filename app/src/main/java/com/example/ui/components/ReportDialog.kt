package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExRoastTheme

val REPORT_REASONS = listOf(
    "Real person identified",
    "Harassment or bullying",
    "Hate speech",
    "Sexual content",
    "Threats or violence",
    "Self-harm encouragement",
    "Spam or promotion",
    "Misinformation",
    "Underage content",
    "Other"
)

@Composable
fun ReportDialog(
    targetType: String,
    onDismiss: () -> Unit,
    onSubmit: (reason: String, note: String) -> Unit
) {
    var selectedReason by remember { mutableStateOf(REPORT_REASONS.first()) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ExRoastTheme.colors.surface,
        title = {
            Text(
                text = "Report $targetType",
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
                    text = "Help keep EX ROAST safe. Reports are reviewed by human moderators according to Community Rules.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ExRoastTheme.colors.textMuted
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(REPORT_REASONS) { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = ExFlame)
                            )
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ExRoastTheme.colors.text,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Additional details (optional)") },
                    placeholder = { Text("Provide context for the mod team...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedReason, note) },
                colors = ButtonDefaults.buttonColors(containerColor = ExFlame),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ExRoastTheme.colors.textMuted)
            }
        }
    )
}
