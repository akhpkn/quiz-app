package quiz.controller

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import quiz.dto.SelectedAnswers
import quiz.security.CurrentUser
import quiz.security.CustomUserDetails
import quiz.service.QuestionService

@RestController
@RequestMapping("/api/question")
class QuestionController(private val questionService: QuestionService) {

    @PutMapping("/{questionId}")
    @ApiOperation("Метод для выбора ответов на вопрос квиза")
    fun chooseAnswers(
        @PathVariable("questionId") questionId: Long,
        @RequestBody selectedAnswers: SelectedAnswers,
        @CurrentUser currentUser: CustomUserDetails?,
    ) {
        questionService.chooseAnswers(questionId, selectedAnswers, currentUser)
    }
}