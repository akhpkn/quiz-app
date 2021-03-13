package quiz.controller

import org.springframework.web.bind.annotation.*
import quiz.dto.QuizCreationRequest
import quiz.dto.QuizDto
import quiz.security.CurrentUser
import quiz.security.CustomUserDetails
import quiz.service.QuizService

@RestController
@RequestMapping("/api/quiz")
class QuizController(private val quizService: QuizService) {

    @PostMapping("/create")
    fun createQuiz(
        @RequestBody quizCreationRequest: QuizCreationRequest,
        @CurrentUser currentUser: CustomUserDetails
    ) {
        quizService.createQuiz(quizCreationRequest, currentUser)
    }

    @GetMapping("/all")
    fun getAllQuizzes(): List<QuizDto> {
        return quizService.getAllQuizzes()
    }
}