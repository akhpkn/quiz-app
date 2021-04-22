package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import quiz.model.Result
import quiz.model.User

interface ResultRepository : JpaRepository<Result, Long> {

    @Query("select r from Result r join fetch r.quiz q join fetch r.user u where q.id=:quizId and u.id=:userId")
    fun findResultByQuizIdAndUserId(@Param("quizId") quizId: Long, @Param("userId") userId: Long): Result?

    @Query("select r from Result r join fetch r.quiz q join fetch r.user u where q.author = ?1")
    fun findResultsForQuizzesCreatedByUser(user: User): List<Result>
}