package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import quiz.model.Question
import quiz.model.Quiz

interface QuestionRepository : JpaRepository<Question, Long> {

    @Query("select q from Question q join fetch q.quiz where q.id = ?1")
    fun findQuestionById(id: Long): Question?

    @Query("select count(q) from Question q where q.quiz = ?1")
    fun getQuestionsNumberForQuiz(quiz: Quiz): Int
}