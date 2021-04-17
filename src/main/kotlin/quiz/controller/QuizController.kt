package quiz.controller

import org.springframework.web.bind.annotation.*
import quiz.dto.*
import quiz.security.CurrentUser
import quiz.security.CustomUserDetails
import quiz.service.QuizService

@RestController
@RequestMapping("/api/quiz")
class QuizController(private val quizService: QuizService) {

    @PostMapping("/create")
    fun createQuiz(
        @RequestBody quizCreationRequest: QuizCreationRequest,
        @CurrentUser currentUser: CustomUserDetails?,
    ): BlankQuizDto {
        return quizService.createQuiz(quizCreationRequest, currentUser)
    }

    @PostMapping("/create-blank")
    fun createBlankQuiz(
        @RequestBody blankQuizRequest: BlankQuizRequest,
        @CurrentUser currentUser: CustomUserDetails?,
    ): BlankQuizDto {
        return quizService.createBlankQuiz(blankQuizRequest, currentUser)
    }

    @GetMapping("/all")
    fun getQuizzes(): ListWrapper<QuizDto> {
        return quizService.getQuizzes()
    }

    @GetMapping("/all-blank")
    fun getBlankQuizzes(): ListWrapper<BlankQuizDto> {
        return quizService.getBlankQuizzes()
    }

    @GetMapping("/created-by-me")
    fun getQuizzesCreatedByMe(@CurrentUser currentUser: CustomUserDetails?): ListWrapper<BlankQuizDto> {
        return quizService.getQuizzesCreatedByMe(currentUser)
    }

    @GetMapping
    fun getBlankQuizzesByAuthor(@RequestParam("authorId") authorId: Long): ListWrapper<BlankQuizDto> {
        return quizService.getBlankQuizzesByAuthor(authorId)
    }

    @GetMapping("/{quizId}/questions")
    fun getQuizQuestions(
        @PathVariable("quizId") quizId: Long,
    ): ListWrapper<QuestionDto> {
        return quizService.getQuizQuestions(quizId)
    }

    @PostMapping("/{quizId}/question")
    fun addQuestionToQuiz(
        @PathVariable("quizId") quizId: Long,
        @RequestBody questionCreationRequest: QuestionCreationRequest,
        @CurrentUser currentUser: CustomUserDetails?,
    ) {
        quizService.addQuestion(quizId, questionCreationRequest, currentUser)
    }

    @GetMapping("/exists-by-code")
    fun existsByCode(@RequestParam("code") code: String): Boolean {
        return quizService.existsByCode(code)
    }

    @GetMapping("/{code}")
    fun getByCode(@PathVariable("code") code: String): BlankQuizDto {
        return quizService.getQuizByCode(code)
    }

    @PostMapping("/access")
    fun accessWithCode(@RequestBody quizAccessRequest: QuizAccessRequest): JwtAuthenticationResponse {
        return quizService.accessWithCode(quizAccessRequest)
    }

    @PostMapping("/{quizId}/send-answers")
    fun sendAnswers(
        @PathVariable("quizId") quizId: Long,
        @RequestBody selectedAnswers: SelectedAnswers,
        @CurrentUser currentUser: CustomUserDetails?,
    ): ResultDto {
        return quizService.sendAnswers(quizId, selectedAnswers, currentUser)
    }

    @PutMapping("/generate-codes")
    fun generateCodes() {
        quizService.generateCodesIfNull()
    }

    @PutMapping("/init-question-numbers")
    fun initQuestionNumbers() {
        quizService.initQuestionsNumberForQuizzes()
    }
}