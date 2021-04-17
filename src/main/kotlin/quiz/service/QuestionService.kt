package quiz.service

import org.springframework.stereotype.Service
import quiz.dto.SelectedAnswers
import quiz.exception.QuestionNotFoundException
import quiz.model.Result
import quiz.repository.AnswerRepository
import quiz.repository.QuestionRepository
import quiz.repository.ResultRepository
import quiz.security.AuthManager
import quiz.security.CustomUserDetails

@Service
class QuestionService(
    private val resultRepository: ResultRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val authManager: AuthManager,
) {

    fun chooseAnswers(questionId: Long, selectedAnswers: SelectedAnswers, currentUser: CustomUserDetails?) {
        val user = authManager.getAuthorizedUser(currentUser)
        val question = questionRepository.findQuestionById(questionId) ?: throw QuestionNotFoundException(questionId)
        val quiz = question.quiz
        val answers = answerRepository.findByIdIn(selectedAnswers.answersIds)

        val diff = answers.count { it.correct } - answers.count { !it.correct }
        val correctAnswers = if (diff > 0) diff else 0

        val quizAnswers = answerRepository.findAnswersByQuizId(quiz.id)

        val result = resultRepository.findResultByQuizIdAndUserId(quiz.id, user.id) ?: Result(user, quiz)

        result.correctAnswers += correctAnswers
        result.score = 1.0 * result.correctAnswers / quizAnswers.count { it.correct }
        resultRepository.save(result)
    }
}