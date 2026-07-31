package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CaseVignetteEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.PearlEntity
import com.example.data.repository.MedColleagueRepository
import com.example.ui.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedColleagueViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedColleagueRepository
    val chatMessages: StateFlow<List<ChatMessageEntity>>
    val pearls: StateFlow<List<PearlEntity>>
    val duePearls: StateFlow<List<PearlEntity>>
    val caseVignettes: StateFlow<List<CaseVignetteEntity>>

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isGeneratingPaper = MutableStateFlow(false)
    val isGeneratingPaper: StateFlow<Boolean> = _isGeneratingPaper.asStateFlow()

    private val _academicPaperResult = MutableStateFlow<String?>(null)
    val academicPaperResult: StateFlow<String?> = _academicPaperResult.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.STUDENT)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isGeneratingQuestions = MutableStateFlow(false)
    val isGeneratingQuestions: StateFlow<Boolean> = _isGeneratingQuestions.asStateFlow()

    private val _showEmergencyAlert = MutableStateFlow(false)
    val showEmergencyAlert: StateFlow<Boolean> = _showEmergencyAlert.asStateFlow()

    private val _selectedSpecialtyFilter = MutableStateFlow("All")
    val selectedSpecialtyFilter: StateFlow<String> = _selectedSpecialtyFilter.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).dao()
        repository = MedColleagueRepository(dao)

        chatMessages = repository.allMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        pearls = repository.allPearls.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        duePearls = repository.duePearls.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        caseVignettes = repository.allCaseVignettes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun updateInputText(text: String) {
        _inputText.value = text
        _showEmergencyAlert.value = repository.isEmergencyQuery(text)
    }

    fun setSpecialtyFilter(specialty: String) {
        _selectedSpecialtyFilter.value = specialty
    }

    fun dismissEmergencyAlert() {
        _showEmergencyAlert.value = false
    }

    fun sendQuery(customQuery: String? = null) {
        val queryToSend = customQuery ?: _inputText.value
        if (queryToSend.isBlank()) return

        val userMessage = ChatMessageEntity(
            sender = "user",
            content = queryToSend,
            isEmergencyAlert = repository.isEmergencyQuery(queryToSend),
            userRole = _userRole.value.name
        )

        viewModelScope.launch {
            repository.saveMessage(userMessage)
            if (customQuery == null) _inputText.value = ""

            // Check if input is a Slash Command (e.g., /convert glucose 100 or /unit)
            val slashResult = com.example.ui.utils.LabUnitConverter.processSlashCommand(queryToSend)
            if (slashResult != null) {
                val botMessage = ChatMessageEntity(
                    sender = "ai",
                    content = slashResult,
                    isEmergencyAlert = false,
                    userRole = _userRole.value.name
                )
                repository.saveMessage(botMessage)
                return@launch
            }

            _isLoading.value = true
            repository.queryMedColleague(queryToSend, _userRole.value)
            _isLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun addCustomPearl(title: String, specialty: String, concept: String, pearl: String, moa: String) {
        viewModelScope.launch {
            val entity = PearlEntity(
                title = title,
                specialty = specialty,
                concept = concept,
                highYieldPearl = pearl,
                moaOrGuideline = moa
            )
            repository.savePearl(entity)
        }
    }

    fun deletePearl(id: Long) {
        viewModelScope.launch {
            repository.deletePearl(id)
        }
    }

    fun selectVignetteOption(vignette: CaseVignetteEntity, optionIndex: Int, confidenceLevel: String = "High") {
        viewModelScope.launch {
            val updated = vignette.copy(
                userSelectedIndex = optionIndex,
                confidenceLevel = confidenceLevel,
                isCompleted = true
            )
            repository.updateCaseVignette(updated)
        }
    }

    fun generateCustomExamQuestions(examStyle: String, topic: String) {
        viewModelScope.launch {
            _isGeneratingQuestions.value = true
            repository.generateCustomExamQuestions(examStyle, topic)
            _isGeneratingQuestions.value = false
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun reviewPearl(pearl: PearlEntity, rating: Int) {
        viewModelScope.launch {
            repository.reviewPearl(pearl, rating)
        }
    }

    fun generateAcademicPaper(topic: String, paperType: String) {
        viewModelScope.launch {
            _isGeneratingPaper.value = true
            _academicPaperResult.value = null
            val result = repository.generateAcademicPaper(topic, paperType)
            _academicPaperResult.value = result.getOrNull()
            _isGeneratingPaper.value = false
        }
    }

    fun clearAcademicPaper() {
        _academicPaperResult.value = null
    }

    suspend fun getAITutorResponse(documentText: String, studentQuestion: String? = null): String {
        return repository.generateAITutorResponse(documentText, studentQuestion)
    }

    private val _isAnalyzingVision = MutableStateFlow(false)
    val isAnalyzingVision: StateFlow<Boolean> = _isAnalyzingVision.asStateFlow()

    private val _visionResult = MutableStateFlow<String?>(null)
    val visionResult: StateFlow<String?> = _visionResult.asStateFlow()

    private val _isSearchingLiterature = MutableStateFlow(false)
    val isSearchingLiterature: StateFlow<Boolean> = _isSearchingLiterature.asStateFlow()

    private val _literatureSearchResult = MutableStateFlow<String?>(null)
    val literatureSearchResult: StateFlow<String?> = _literatureSearchResult.asStateFlow()

    fun searchMedicalDatabases(query: String, journalFilter: String = "All Databases") {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isSearchingLiterature.value = true
            _literatureSearchResult.value = null
            val res = repository.searchMedicalDatabases(query, journalFilter)
            _literatureSearchResult.value = res.getOrNull()
            _isSearchingLiterature.value = false
        }
    }

    fun clearLiteratureSearch() {
        _literatureSearchResult.value = null
    }

    // Study Group Real-Time Room State
    data class StudyRoomMessage(
        val sender: String,
        val role: String,
        val text: String,
        val isAiModerator: Boolean = false,
        val timestamp: String = "Just now"
    )

    private val _studyRoomMessages = MutableStateFlow<List<StudyRoomMessage>>(
        listOf(
            StudyRoomMessage(
                sender = "MedColleague Senior AI Moderator",
                role = "Attending AI Physician",
                text = "Welcome to Cardiology & Board Prep Virtual Study Room! Ask a case question or post clinical findings. I will moderate for medical accuracy and provide real-time board citations.",
                isAiModerator = true,
                timestamp = "08:30 AM"
            ),
            StudyRoomMessage(
                sender = "Dr. Sarah Lin",
                role = "PGY-1 Internal Medicine Resident",
                text = "In a patient presenting with suspected Acute Heart Failure, what's the primary utility of checking NT-proBNP vs BNP, and what cutoff rules out acute heart failure in the ER?",
                isAiModerator = false,
                timestamp = "08:32 AM"
            ),
            StudyRoomMessage(
                sender = "MedColleague Senior AI Moderator",
                role = "Attending AI Physician",
                text = "Great clinical question! According to the 2022 AHA/ACC/HFSA Heart Failure Guidelines: NT-proBNP < 300 pg/mL or BNP < 100 pg/mL has a high negative predictive value (> 98%) to RULE OUT acute HF. Age-adjusted cutoffs apply for NT-proBNP (< 50 yrs: > 450; 50-75 yrs: > 900; > 75 yrs: > 1800 pg/mL).",
                isAiModerator = true,
                timestamp = "08:33 AM"
            )
        )
    )
    val studyRoomMessages: StateFlow<List<StudyRoomMessage>> = _studyRoomMessages.asStateFlow()

    fun analyzeVisionImage(base64Image: String?, prompt: String, imageType: String) {
        viewModelScope.launch {
            _isAnalyzingVision.value = true
            _visionResult.value = null
            val result = repository.analyzeClinicalImage(base64Image, prompt, imageType)
            _visionResult.value = result.getOrNull()
            _isAnalyzingVision.value = false
        }
    }

    fun clearVisionResult() {
        _visionResult.value = null
    }

    fun sendStudyRoomMessage(userContent: String) {
        if (userContent.isBlank()) return
        val userMsg = StudyRoomMessage(
            sender = "You (${_userRole.value.name.lowercase().capitalize()})",
            role = _userRole.value.name,
            text = userContent,
            isAiModerator = false,
            timestamp = "Just now"
        )
        val updated = _studyRoomMessages.value + userMsg
        _studyRoomMessages.value = updated

        // Generate AI Moderator live feedback
        viewModelScope.launch {
            _isLoading.value = true
            val prompt = "Moderator review for medical student room. User posted: $userContent. Provide concise, board-certified feedback with high-yield teaching points or guidelines."
            val res = repository.queryMedColleague(prompt, _userRole.value)
            val aiText = res.getOrNull()?.content ?: "The case discussion highlights crucial clinical reasoning. Always correlate with baseline vitals and serial ECG/biomarkers."
            
            val aiMsg = StudyRoomMessage(
                sender = "MedColleague AI Moderator",
                role = "Attending AI Physician",
                text = aiText,
                isAiModerator = true,
                timestamp = "Just now"
            )
            _studyRoomMessages.value = _studyRoomMessages.value + aiMsg
            _isLoading.value = false
        }
    }

    fun clearVignettes() {
        viewModelScope.launch {
            repository.clearVignettes()
        }
    }
}
