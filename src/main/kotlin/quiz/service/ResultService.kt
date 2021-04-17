package quiz.service

import org.springframework.stereotype.Service
import quiz.dto.ListWrapper
import quiz.dto.QuizResultsDto
import quiz.dto.ResultDto
import quiz.exception.ResultNotFoundException
import quiz.mapper.DtoMapper
import quiz.repository.QuestionRepository
import quiz.repository.ResultRepository
import quiz.repository.UserRepository
import quiz.security.AuthManager
import quiz.security.CustomUserDetails

@Service
class ResultService(
    private val resultRepository: ResultRepository,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val authManager: AuthManager,
    private val dtoMapper: DtoMapper,
) {

    fun getResultByUserAndQuizIds(userId: Long, quizId: Long): ResultDto {
        val result = resultRepository.findResultByQuizIdAndUserId(quizId, userId)
            ?: throw ResultNotFoundException(
                userId,
                quizId
            )
        return dtoMapper.resultToDto(result)
    }

    fun getResultsForQuizzesCreatedByMe(currentUser: CustomUserDetails?): ListWrapper<QuizResultsDto> {
        val user = authManager.getAuthorizedUser(currentUser)
        val results = resultRepository.findResultsForQuizzesCreatedByUser(user)

        return ListWrapper(
            results
                .groupBy { it.quiz }
                .map { (quiz, results) ->
                    val questionsNumber = questionRepository.getQuestionsNumberForQuiz(quiz)
                    dtoMapper.quizToQuizResultsDto(quiz, questionsNumber, results)
                }
        )
    }
}