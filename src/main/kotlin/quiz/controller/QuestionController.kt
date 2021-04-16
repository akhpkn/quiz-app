package quiz.controller

import org.springframework.web.bind.annotation.*
import quiz.dto.SelectedAnswers
import quiz.security.CurrentUser
import quiz.security.CustomUserDetails
import quiz.service.QuestionService

@RestController
@RequestMapping("/api/question")
class QuestionController(private val questionService: QuestionService) {

    @PostMapping("/{questionId}")
    fun chooseAnswers(
        @PathVariable("questionId") questionId: Long,
        @RequestBody selectedAnswers: SelectedAnswers,
        @CurrentUser currentUser: CustomUserDetails,
    ) {
        questionService.chooseAnswers(questionId, selectedAnswers, currentUser)
    }
}