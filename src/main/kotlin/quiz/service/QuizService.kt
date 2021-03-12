package quiz.service

import org.springframework.stereotype.Service
import quiz.dto.QuizCreationRequest
import quiz.dto.QuizDto
import quiz.exception.NoAuthException
import quiz.model.Answer
import quiz.model.Question
import quiz.model.Quiz
import quiz.repository.AnswerRepository
import quiz.repository.QuestionRepository
import quiz.repository.QuizRepository
import quiz.repository.UserRepository
import quiz.security.CustomUserDetails

@Service
class QuizService(
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
) {

    fun createQuiz(quizCreationRequest: QuizCreationRequest, currentUser: CustomUserDetails?) {
        if (currentUser == null) {
            throw NoAuthException()
        }
        val user = userRepository.findUserById(currentUser.userId)

        val quiz = Quiz()
        quiz.author = user
        quiz.title = quizCreationRequest.title

        val savedQuiz = quizRepository.save(quiz)

        val questions: MutableList<Question> = ArrayList()
        val answers: MutableList<Answer> = ArrayList()

        quizCreationRequest.questions.forEach { questionRequest ->
            val question = Question()
            question.quiz = savedQuiz
            question.text = questionRequest.text
            question.multiple = questionRequest.multiple

            val savedQuestion = questionRepository.save(question)
            questions.add(savedQuestion)

            questionRequest.answers.forEach { answerRequest ->
                val answer = Answer()
                answer.correct = answerRequest.correct
                answer.text = answerRequest.text
                answer.question = savedQuestion

                val savedAnswer = answerRepository.save(answer)
                answers.add(savedAnswer)
            }
        }
    }
}