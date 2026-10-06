package com.careapp.app.data

// Sample data provider for demo
object SampleData {
    val seniorProfile = SeniorProfile(
        fullName = "Robert Lee",
        preferredName = "Robert",
        birthday = "04/15/1945",
        address = "123 Example Street, San Diego, CA",
        primaryLanguage = "English",
        memoryNote = "You may have memory loss. Please speak slowly and tell me where I am.",
        primaryCaregiverName = "Maya Lee",
        primaryCaregiverPhone = "555-123-4567",
        doctorName = "Dr. Patel",
        doctorPhone = "555-234-5678",
        preferredHospital = "San Diego Medical Center",
        allergies = "Penicillin",
        medicalConditions = "Alzheimer's disease, Type 2 Diabetes"
    )

    val medications = listOf(
        Medication(
            id = 1,
            name = "Donepezil",
            dose = "1 tablet",
            scheduledTime = "8:00 AM",
            instructions = "Take after breakfast",
            pillDescription = "Small white tablet",
            reason = "Alzheimer's disease",
            doctor = "Dr. Patel",
            caregiverConfirmationRequired = false
        ),
        Medication(
            id = 2,
            name = "Metformin",
            dose = "500 mg",
            scheduledTime = "12:00 PM",
            instructions = "Take with lunch",
            pillDescription = "Medium white tablet",
            reason = "Type 2 Diabetes",
            doctor = "Dr. Patel",
            caregiverConfirmationRequired = false
        ),
        Medication(
            id = 3,
            name = "Lisinopril",
            dose = "10 mg",
            scheduledTime = "6:00 PM",
            instructions = "Take in evening",
            pillDescription = "Small yellow tablet",
            reason = "Blood pressure",
            doctor = "Dr. Patel",
            caregiverConfirmationRequired = false
        ),
        Medication(
            id = 4,
            name = "Vitamin D",
            dose = "1 capsule",
            scheduledTime = "9:00 AM",
            instructions = "Take with breakfast",
            pillDescription = "Small softgel",
            reason = "Daily supplement",
            doctor = "Dr. Patel",
            caregiverConfirmationRequired = false
        ),
        Medication(
            id = 5,
            name = "Acetaminophen",
            dose = "As directed",
            scheduledTime = "As needed",
            instructions = "Use only as entered by caregiver or clinician",
            pillDescription = "White tablet",
            reason = "Pain relief",
            doctor = "Dr. Patel",
            caregiverConfirmationRequired = true
        )
    )

    val routineTasks = listOf(
        RoutineTask(
            id = 1,
            title = "Eat breakfast",
            category = "Morning",
            suggestedTime = "7:00 AM",
            completed = true,
            completedTime = "7:15 AM"
        ),
        RoutineTask(
            id = 2,
            title = "Take morning medicine",
            category = "Morning",
            suggestedTime = "8:00 AM",
            completed = true,
            completedTime = "8:12 AM"
        ),
        RoutineTask(
            id = 3,
            title = "Brush teeth",
            category = "Morning",
            suggestedTime = "8:30 AM",
            completed = false
        ),
        RoutineTask(
            id = 4,
            title = "Drink water",
            category = "Afternoon",
            suggestedTime = "2:00 PM",
            completed = false
        ),
        RoutineTask(
            id = 5,
            title = "Take evening medicine",
            category = "Evening",
            suggestedTime = "6:00 PM",
            completed = false
        ),
        RoutineTask(
            id = 6,
            title = "Eat lunch",
            category = "Afternoon",
            suggestedTime = "12:30 PM",
            completed = false
        ),
        RoutineTask(
            id = 7,
            title = "Call Maya",
            category = "Evening",
            suggestedTime = "5:00 PM",
            completed = false
        )
    )

    val memoryCards = listOf(
        MemoryCard(
            id = 1,
            name = "Maya Lee",
            relationship = "Daughter",
            memoryCue = "She visits every Sunday",
            helpsWith = "Groceries and doctor appointments",
            phone = "555-123-4567"
        ),
        MemoryCard(
            id = 2,
            name = "Dr. James Patel",
            relationship = "Primary doctor",
            memoryCue = "Sees you every 3 months",
            helpsWith = "Medical care and medications",
            phone = "555-234-5678"
        ),
        MemoryCard(
            id = 3,
            name = "Sarah",
            relationship = "Neighbor",
            memoryCue = "Lives next door",
            helpsWith = "Garden and daily help",
            phone = "555-345-6789"
        ),
        MemoryCard(
            id = 4,
            name = "Daniel Lee",
            relationship = "Family member",
            memoryCue = "He calls on Wednesday evenings",
            helpsWith = "Family check-ins",
            phone = "555-456-7890"
        ),
        MemoryCard(
            id = 5,
            name = "Care Team",
            relationship = "Support team",
            memoryCue = "Call for help with care questions",
            helpsWith = "Care coordination",
            phone = "555-567-8901"
        )
    )

    val savedPlaces = listOf(
        SavedPlace(
            id = 1,
            name = "Home",
            type = "Home",
            latitude = 32.7157,
            longitude = -117.1611,
            notes = "123 Example Street"
        ),
        SavedPlace(
            id = 2,
            name = "Doctor's Office",
            type = "Hospital",
            latitude = 32.7580,
            longitude = -117.1700,
            notes = "Dr. Patel's office"
        )
    )

    val caregiverNote = CaregiverNote(
        date = "August 21, 2026",
        mealNotes = "Ate breakfast at 7:15 AM, lunch at 12:30 PM",
        medicationNotes = "Morning medication taken at 8:12 AM",
        mood = "Calm and happy",
        sleepQuality = "Good, 8 hours last night",
        confusionNotes = "Mild confusion around 11:30 AM, cleared after viewing photos",
        safetyNotes = "No falls or safety concerns",
        repeatedQuestions = "Asked 'Where is Maya?' twice this morning",
        nextSteps = "Evening medication at 6:00 PM. Maya visiting at 5:00 PM."
    )
}
