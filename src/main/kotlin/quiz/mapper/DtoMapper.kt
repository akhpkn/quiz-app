package quiz.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import quiz.dto.*
import quiz.model.*

@Mapper(componentModel = "spring")
interface DtoMapper {

    @Mappings(Mapping(target = "userId", source = "id"))
    fun userToDto(user: User): UserDto

    @Mappings(
        Mapping(target = "quizId", source = "id"),
    )
    fun quizToBlankDto(quiz: Quiz): BlankQuizDto

    @Mappings(Mapping(target = "answerId", source = "id"))
    fun answerToDto(answer: Answer): AnswerDto

    @Mappings(Mapping(target = "resultId", source = "id"))
    fun resultToDto(result: Result): ResultDto

    fun answersToDtos(results: List<Answer>): List<AnswerDto>

    @Mappings(Mapping(target = "questionId", source = "question.id"))
    fun questionToDto(question: Question, answers: List<Answer>): QuestionDto {
        return QuestionDto(
            question.id,
            question.text,
            question.multiple,
            answersToDtos(answers)
        )
    }
}