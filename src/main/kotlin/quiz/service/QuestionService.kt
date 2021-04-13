package quiz.service

import org.springframework.stereotype.Service
import quiz.exception.NoAuthException
import quiz.exception.QuestionNotFoundException
import quiz.exception.UserNotFoundException
import quiz.model.Result
import quiz.model.User
import quiz.repository.AnswerRepository
import quiz.repository.QuestionRepository
import quiz.repository.ResultRepository
import quiz.repository.UserRepository
import quiz.security.CustomUserDetails

@Service
class QuestionService(
    private val resultRepository: ResultRepository,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
) {

    fun chooseAnswers(questionId: Long, answersIds: Set<Long>, currentUser: CustomUserDetails?) {
        val user = getCurrentUser(currentUser)
        val question = questionRepository.findQuestionById(questionId) ?: throw QuestionNotFoundException(questionId)
        val quiz = question.quiz
        val answers = answerRepository.findByIdIn(answersIds)

        val diff = answers.count { it.correct } - answers.count { !it.correct }
        val correctAnswers = if (diff > 0) diff else 0

        val quizAnswers = answerRepository.findAnswersByQuizId(quiz.id)

        val result = resultRepository.findResultByQuizIdAndUserId(quiz.id, user.id) ?: Result(user, quiz)

        result.correctAnswers += correctAnswers
        result.score = 1.0 * result.correctAnswers / quizAnswers.count { it.correct }
        resultRepository.save(result)
    }

    private fun getCurrentUser(currentUser: CustomUserDetails?): User {
        if (currentUser == null) {
            throw NoAuthException()
        }
        return userRepository.findUserById(currentUser.userId) ?: throw UserNotFoundException(currentUser.userId)
    }
}