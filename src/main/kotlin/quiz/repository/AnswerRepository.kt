package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import quiz.model.Answer

interface AnswerRepository: JpaRepository<Answer, Long> {
}