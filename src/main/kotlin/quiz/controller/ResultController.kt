package quiz.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import quiz.dto.ResultDto
import quiz.service.ResultService

@RestController
@RequestMapping("/api/result")
class ResultController(private val resultService: ResultService) {

    @GetMapping
    fun getResultForQuiz(
        @RequestParam("userId") userId: Long,
        @RequestParam("quizId") quizId: Long,
    ): ResultDto {
        return resultService.getResultByUserAndQuizIds(userId, quizId)
    }
}