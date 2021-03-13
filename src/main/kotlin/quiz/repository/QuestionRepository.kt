package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import quiz.model.Question
import quiz.model.Quiz

interface QuestionRepository : JpaRepository<Question, Long>