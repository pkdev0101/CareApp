package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.MemoryCard
import com.careapp.app.data.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MemoryCardViewModel(private val repository: CareRepository) : ViewModel() {

    // Memory cards list
    val memoryCards: StateFlow<List<MemoryCard>> = repository.getAllMemoryCards()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Quiz state
    private val _showQuiz = MutableStateFlow(false)
    val showQuiz: StateFlow<Boolean> = _showQuiz.asStateFlow()

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizMessage = MutableStateFlow("")
    val quizMessage: StateFlow<String> = _quizMessage.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow(-1)
    val selectedAnswerIndex: StateFlow<Int> = _selectedAnswerIndex.asStateFlow()

    // Dialog states
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogMessage = MutableStateFlow("")
    val dialogMessage: StateFlow<String> = _dialogMessage.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    val dialogTitle: StateFlow<String> = _dialogTitle.asStateFlow()

    // Quiz questions
    private val quizQuestions = listOf(
        QuizQuestion(
            question = "What is your daughter's name?",
            options = listOf("Maya", "Sarah", "Emma", "Lisa"),
            correctIndex = 0
        ),
        QuizQuestion(
            question = "What is your favorite flower?",
            options = listOf("Tulip", "Rose", "Sunflower", "Daisy"),
            correctIndex = 1
        ),
        QuizQuestion(
            question = "What did you love doing in your career?",
            options = listOf("Teaching", "Gardening", "Nursing", "Engineering"),
            correctIndex = 0
        )
    )

    // ========== START GENTLE PRACTICE QUIZ ==========
    fun startGentleQuiz() {
        _showQuiz.value = true
        _currentQuizIndex.value = 0
        _quizScore.value = 0
        _selectedAnswerIndex.value = -1
        showNextQuestion()
    }

    // ========== GET CURRENT QUESTION ==========
    fun getCurrentQuestion(): QuizQuestion? {
        return if (_currentQuizIndex.value < quizQuestions.size) {
            quizQuestions[_currentQuizIndex.value]
        } else {
            null
        }
    }

    // ========== SUBMIT ANSWER ==========
    fun submitAnswer(answerIndex: Int) {
        _selectedAnswerIndex.value = answerIndex
        val currentQuestion = getCurrentQuestion() ?: return

        val isCorrect = answerIndex == currentQuestion.correctIndex

        if (isCorrect) {
            _quizScore.value++
            _quizMessage.value = "✓ Great! That's correct."
        } else {
            _quizMessage.value = "That's okay. The answer is: ${currentQuestion.options[currentQuestion.correctIndex]}"
        }
    }

    // ========== SHOW NEXT QUESTION ==========
    fun showNextQuestion() {
        val nextIndex = _currentQuizIndex.value + 1

        if (nextIndex < quizQuestions.size) {
            _currentQuizIndex.value = nextIndex
            _selectedAnswerIndex.value = -1
            _quizMessage.value = ""
        } else {
            finishQuiz()
        }
    }

    // ========== FINISH QUIZ ==========
    private fun finishQuiz() {
        _showQuiz.value = false

        val percentage = (_quizScore.value * 100) / quizQuestions.size
        val message = buildString {
            append("Quiz Complete!\n\n")
            append("Score: ${_quizScore.value}/${quizQuestions.size}\n")
            append("Percentage: $percentage%\n\n")
            append("Great practice session!\n")
            append("You're doing wonderful.")
        }

        _dialogTitle.value = "✓ Quiz Complete"
        _dialogMessage.value = message
        _showDialog.value = true
    }

    // ========== SKIP QUIZ ==========
    fun skipQuiz() {
        _showQuiz.value = false
        _selectedAnswerIndex.value = -1
        _quizMessage.value = ""
    }

    // ========== ADD NEW MEMORY CARD ==========
    fun addMemoryCard(name: String, relationship: String, memoryCue: String, phone: String, helpsWith: String = "") {
        viewModelScope.launch {
            val newCard = MemoryCard(
                name = name,
                relationship = relationship,
                memoryCue = memoryCue,
                helpsWith = helpsWith,
                phone = phone,
                photoUri = null
            )
            repository.insertMemoryCard(newCard)

            _dialogTitle.value = "✓ Card Added"
            _dialogMessage.value = "New memory card for $name added successfully."
            _showDialog.value = true
        }
    }

    // ========== DISMISS DIALOG ==========
    fun dismissDialog() {
        _showDialog.value = false
        _dialogTitle.value = ""
        _dialogMessage.value = ""
    }
}

// Quiz question data class
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)
