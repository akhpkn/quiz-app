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

    @Mappings(Mapping(target = "name", source = "user.name"))
    fun resultToSmallDto(result: Result): ResultSmallDto

    fun resultsToSmallDtos(results: List<Result>): List<ResultSmallDto>

    fun answersToDtos(answers: List<Answer>): List<AnswerDto>

    @Mappings(Mapping(target = "questionId", source = "question.id"))
    fun questionToDto(question: Question, answers: List<Answer>): QuestionDto {
        return QuestionDto(
            question.id,
            question.text,
            question.multiple,
            answersToDtos(answers)
        )
    }

    @Mappings(Mapping(target = "quizId", source = "quiz.id"))
    fun quizToQuizResultsDto(quiz: Quiz, results: List<Result>): QuizResultsDto {
        return QuizResultsDto(
            quiz.id,
            quiz.title,
            quiz.code,
            quiz.questionsNumber,
            resultsToSmallDtos(results)
        )
    }
}