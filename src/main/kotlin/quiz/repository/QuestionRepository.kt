package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import quiz.model.Question

interface QuestionRepository: JpaRepository<Question, Long> {
}