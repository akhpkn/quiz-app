package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import quiz.model.Answer

interface AnswerRepository : JpaRepository<Answer, Long> {

    @Query("select a from Answer a join fetch a.question q join fetch q.quiz qu join fetch qu.author")
    fun findAllAnswers(): List<Answer>

    @Query("select a from Answer a join fetch a.question q join fetch q.quiz qu join fetch qu.author " +
            "where qu.id=:quizId")
    fun findAnswersByQuizId(@Param("quizId") quizId: Long): List<Answer>
}