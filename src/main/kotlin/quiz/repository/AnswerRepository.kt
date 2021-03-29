package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import quiz.model.Answer

interface AnswerRepository : JpaRepository<Answer, Long> {

    @Query("select a from Answer a join fetch a.question q join fetch q.quiz qu join fetch qu.author")
    fun findAllAnswers(): List<Answer>

    @Query("select a from Answer a join fetch a.question q join fetch q.quiz qu join fetch qu.author " +
            "where qu.id = ?1")
    fun findAnswersByQuizId(quizId: Long): List<Answer>

    @Query("select a from Answer a join fetch a.question q where q.id = ?1")
    fun findAnswersByQuestionId(questionId: Long): List<Answer>

    fun findByIdIn(ids: Set<Long>): List<Answer>
}