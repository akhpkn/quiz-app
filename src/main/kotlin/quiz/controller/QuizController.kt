package quiz.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import quiz.dto.QuizCreationRequest
import quiz.security.CurrentUser
import quiz.security.CustomUserDetails
import quiz.service.QuizService

@RestController
@RequestMapping("/api/quiz")
class QuizController(private val quizService: QuizService) {

    @PostMapping("/create")
    fun createQuiz(@RequestBody quizCreationRequest: QuizCreationRequest,
                   @CurrentUser currentUser: CustomUserDetails) {
        quizService.createQuiz(quizCreationRequest, currentUser)
    }
}