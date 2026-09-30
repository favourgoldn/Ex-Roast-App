package com.example.data.safety

import com.example.data.local.entity.BannedTermEntity

data class SafetyCheckResult(
    val isSafe: Boolean,
    val isSelfHarmRisk: Boolean = false,
    val failureReason: String? = null,
    val highlightedWords: List<String> = emptyList()
)

object SafetyChecker {
    private val phoneRegex = Regex("""(\+?\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}|\b\d{10,11}\b""")
    private val emailRegex = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
    private val urlRegex = Regex("""(?i)\b(https?://|www\.)\S+|\b[a-zA-Z0-9.-]+\.(com|org|net|io|co|app)\b""")
    private val socialHandleRegex = Regex("""(?i)(?:ig|insta|snap|snapchat|twitter|tiktok|fb|facebook):?\s*@?([a-zA-Z0-9_.]+)""")
    private val addressRegex = Regex("""(?i)\b\d{1,5}\s+[a-zA-Z0-9\s.,]+(street|st|avenue|ave|road|rd|blvd|boulevard|lane|ln|drive|dr|way|court|ct)\b""")
    
    // Self-harm keywords triggering compassionate crisis resources
    private val selfHarmKeywords = listOf(
        "kill myself", "suicide", "end my life", "want to die", 
        "harm myself", "slit my", "hang myself", "don't want to live"
    )

    // Base prohibited terms (doxxing, severe slurs, threats)
    private val defaultProhibited = listOf(
        "doxx", "leak their address", "swat them", "kill him", "kill her", "die in a fire"
    )

    fun checkContent(
        text: String,
        customBannedTerms: List<BannedTermEntity> = emptyList()
    ): SafetyCheckResult {
        val lower = text.lowercase()

        // 1. Check self-harm first for compassionate safety intervention
        for (keyword in selfHarmKeywords) {
            if (lower.contains(keyword)) {
                return SafetyCheckResult(
                    isSafe = false,
                    isSelfHarmRisk = true,
                    failureReason = "It sounds like you may be going through a tough time. If you need support, free and confidential crisis counselors are here 24/7."
                )
            }
        }

        // 2. Check phone numbers
        if (phoneRegex.containsMatchIn(text)) {
            return SafetyCheckResult(
                isSafe = false,
                failureReason = "Contains phone numbers. To protect privacy and prevent doxxing, phone numbers are not allowed."
            )
        }

        // 3. Check emails
        if (emailRegex.containsMatchIn(text)) {
            return SafetyCheckResult(
                isSafe = false,
                failureReason = "Contains email addresses. Please remove identifiable contact details."
            )
        }

        // 4. Check links
        if (urlRegex.containsMatchIn(text)) {
            return SafetyCheckResult(
                isSafe = false,
                failureReason = "Links and URLs are not permitted in stories or roasts to prevent spam and tracking."
            )
        }

        // 5. Check social media handles pointing to real ex accounts
        if (socialHandleRegex.containsMatchIn(text)) {
            return SafetyCheckResult(
                isSafe = false,
                failureReason = "Contains social media handles. Don't post their handle or account name—use a nickname instead!"
            )
        }

        // 6. Check physical street addresses
        if (addressRegex.containsMatchIn(text)) {
            return SafetyCheckResult(
                isSafe = false,
                failureReason = "Contains street addresses. Physical addresses are strictly prohibited."
            )
        }

        // 7. Check default and dynamic banned terms
        for (banned in defaultProhibited) {
            if (lower.contains(banned)) {
                return SafetyCheckResult(
                    isSafe = false,
                    failureReason = "Contains prohibited harassment, threat, or doxxing language."
                )
            }
        }

        for (banned in customBannedTerms) {
            if (banned.severity == "block") {
                if (banned.isRegex) {
                    try {
                        if (Regex(banned.pattern, RegexOption.IGNORE_CASE).containsMatchIn(text)) {
                            return SafetyCheckResult(
                                isSafe = false,
                                failureReason = "Contains prohibited language flagged by community safety filters."
                            )
                        }
                    } catch (_: Exception) {}
                } else if (lower.contains(banned.pattern.lowercase())) {
                    return SafetyCheckResult(
                        isSafe = false,
                        failureReason = "Contains prohibited language flagged by community safety filters."
                    )
                }
            }
        }

        return SafetyCheckResult(isSafe = true)
    }

    /**
     * Anonymization helper: finds capitalized words that might be real names,
     * excluding sentence-initial words or common title words.
     */
    fun findPotentialNames(text: String): List<String> {
        val commonWords = setOf(
            "I", "The", "He", "She", "They", "We", "My", "His", "Her", "Their",
            "Then", "When", "After", "Before", "So", "And", "But", "If", "Because",
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday",
            "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"
        )
        val wordRegex = Regex("""\b[A-Z][a-z]{2,15}\b""")
        val matches = wordRegex.findAll(text)
            .map { it.value }
            .filter { !commonWords.contains(it) }
            .distinct()
            .toList()
        return matches
    }
}
