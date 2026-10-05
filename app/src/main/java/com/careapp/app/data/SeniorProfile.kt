package com.careapp.app.data

data class SeniorProfile(
    val id: Int = 1,
    val fullName: String = "Robert Lee",
    val preferredName: String = "Robert",
    val birthday: String = "04/15/1945",
    val address: String = "123 Example Street",
    val primaryLanguage: String = "English",
    val memoryNote: String = "You may have memory loss. Please speak slowly and tell me where I am.",
    val calmingInstructions: String = "Show family photos and use breathing techniques.",
    val primaryCaregiverName: String = "Maya Lee",
    val primaryCaregiverPhone: String = "555-123-4567",
    val doctorName: String = "Dr. Patel",
    val doctorPhone: String = "555-234-5678",
    val preferredHospital: String = "San Diego Medical Center",
    val allergies: String = "Penicillin",
    val medicalConditions: String = "Alzheimer's disease, Type 2 Diabetes"
)
