package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import quiz.model.Quiz

interface QuizRepository : JpaRepository<Quiz, Long> {

    @Query("select q from Quiz q join fetch q.author where q.id = ?1")
    fun findQuizById(id: Long): Quiz?

    @Query("select q from Quiz q join fetch q.author")
    fun findAllQuizzes(): List<Quiz>

    @Query("select q from Quiz q join fetch q.author a where a.id = ?1")
    fun findQuizzesByAuthorId(authorId: Long): List<Quiz>

    @Query("select q from Quiz q join fetch q.author where q.code = ?1")
    fun findByCode(code: String): Quiz?

    fun existsByCode(code: String): Boolean

    @Query("select q from Quiz q where q.code is null")
    fun findQuizzesWithoutCode(): MutableList<Quiz>

    @Query("select q.code from Quiz q")
    fun getCodes(): MutableList<String>
}