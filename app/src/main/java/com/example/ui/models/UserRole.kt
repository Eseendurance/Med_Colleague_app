package com.example.ui.models

enum class UserRole(
    val title: String,
    val subtitle: String,
    val systemFocus: String
) {
    STUDENT(
        title = "Medical Student / Trainee",
        subtitle = "Pathophysiology, MoA & High-Yield Board Concepts",
        systemFocus = "Focus heavily on underlying pathophysiology, mechanisms of action, intuitive real-world analogies, high-yield USMLE/board exam concepts, and memorable breakdowns. Include an optional interactive follow-up question or vignette."
    ),
    CLINICIAN(
        title = "Clinician / Medical Staff",
        subtitle = "Concise Differential, 1st-Line Tx & Red Flags",
        systemFocus = "Focus on concise differential diagnoses, first-line management steps, acute red flags, evidence-based practice guidelines, and rapid clinical decision support. Omit basic board questions."
    )
}
