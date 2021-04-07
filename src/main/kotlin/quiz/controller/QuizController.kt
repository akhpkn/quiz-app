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
        @CurrentUser currentUser: CustomUserDetails,
    ) {
        quizService.createQuiz(quizCreationRequest, currentUser)
    }

    @PostMapping("/create-blank")
    fun createBlankQuiz(
        @RequestBody blankQuizRequest: BlankQuizRequest,
        @CurrentUser currentUser: CustomUserDetails,
    ): BlankQuizDto {
        return quizService.createBlankQuiz(blankQuizRequest, currentUser)
    }

    @GetMapping("/all")
    fun getQuizzes(): List<QuizDto> {
        return quizService.getQuizzes()
    }

    @GetMapping("/all-blank")
    fun getBlankQuizzes(): List<BlankQuizDto> {
        return quizService.getBlankQuizzes()
    }

    @GetMapping
    fun getBlankQuizzesByAuthor(@RequestParam("authorId") authorId: Long): List<BlankQuizDto> {
        return quizService.getBlankQuizzesByAuthor(authorId)
    }

    @GetMapping("/{quizId}/questions")
    fun getQuizQuestions(
        @PathVariable("quizId") quizId: Long,
    ): List<QuestionDto> {
        return quizService.getQuizQuestions(quizId)
    }

    @PostMapping("/{quizId}/question")
    fun addQuestionToQuiz(
        @PathVariable("quizId") quizId: Long,
        @RequestBody questionCreationRequest: QuestionCreationRequest,
        @CurrentUser currentUser: CustomUserDetails,
    ) {
        quizService.addQuestion(quizId, questionCreationRequest, currentUser)
    }
}