package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import quiz.model.Quiz

interface QuizRepository : JpaRepository<Quiz, Long>