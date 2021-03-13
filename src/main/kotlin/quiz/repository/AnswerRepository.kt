package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import quiz.model.Answer
import quiz.model.Question

interface AnswerRepository : JpaRepository<Answer, Long> {

    @Query("select a from Answer a join fetch a.question q join fetch q.quiz qu join fetch qu.author")
    fun findAllAnswers(): List<Answer>
}