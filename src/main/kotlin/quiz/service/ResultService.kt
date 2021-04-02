package quiz.service

import org.springframework.stereotype.Service
import quiz.dto.ResultDto
import quiz.exception.ResultNotFoundException
import quiz.mapper.DtoMapper
import quiz.repository.ResultRepository

@Service
class ResultService(
    private val resultRepository: ResultRepository,
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
}