package quiz.controller

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import quiz.dto.ListWrapper
import quiz.dto.QuizResultsDto
import quiz.dto.ResultDto
import quiz.security.CurrentUser
import quiz.security.CustomUserDetails
import quiz.service.ResultService

@RestController
@RequestMapping("/api/result")
class ResultController(private val resultService: ResultService) {

    @GetMapping
    @ApiOperation("Метод для получения результата пользователя по квизу")
    fun getResultForQuiz(
        @RequestParam("userId") userId: Long,
        @RequestParam("quizId") quizId: Long,
    ): ResultDto {
        return resultService.getResultByUserAndQuizIds(userId, quizId)
    }

    @GetMapping("/my-quizzes")
    @ApiOperation("Метод для получения всех результатов по всем квизам, созданных авторизованным пользователем")
    fun getAllResultsForCreatedQuizzes(@CurrentUser currentUser: CustomUserDetails?): ListWrapper<QuizResultsDto> {
        return resultService.getResultsForQuizzesCreatedByMe(currentUser)
    }
}