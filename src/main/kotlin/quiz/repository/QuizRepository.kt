package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import quiz.model.Quiz

interface QuizRepository : JpaRepository<Quiz, Long> {

    @Query("select q from Quiz q join fetch q.author where q.id = ?1")
    fun findQuizById(id: Long): Quiz?

    @Query("select q from Quiz q join fetch q.author")
    fun findAllQuizzes(): List<Quiz>
}