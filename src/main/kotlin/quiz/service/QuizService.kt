package quiz.service

import org.springframework.stereotype.Service
import quiz.dto.*
import quiz.exception.NoAuthException
import quiz.exception.QuizNotFoundException
import quiz.exception.QuizWriteAccessNotAllowedException
import quiz.exception.UserNotFoundException
import quiz.model.Answer
import quiz.model.Question
import quiz.model.Quiz
import quiz.model.User
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
        val user: User = getCurrentUser(currentUser)

        val quiz = Quiz(quizCreationRequest.title, user)
        val savedQuiz = quizRepository.save(quiz)

        quizCreationRequest.questions.forEach { questionRequest ->
            val question = Question(questionRequest.text, questionRequest.multiple, savedQuiz)
            val savedQuestion = questionRepository.save(question)

            questionRequest.answers.forEach { answerRequest ->
                val answer = Answer(answerRequest.text, answerRequest.correct, savedQuestion)
                answerRepository.save(answer)
            }
        }
    }

    fun createBlankQuiz(blankQuizRequest: BlankQuizRequest, currentUser: CustomUserDetails?): BlankQuizDto {
        val user: User = getCurrentUser(currentUser)

        val quiz = Quiz(blankQuizRequest.title, user)
        val savedQuiz = quizRepository.save(quiz)

        return BlankQuizDto(savedQuiz.id, savedQuiz.title, UserDto(user.id, user.username))
    }

    fun addQuestion(quizId: Long, questionCreationRequest: QuestionCreationRequest, currentUser: CustomUserDetails?) {
        val user: User = getCurrentUser(currentUser)

        val quiz = quizRepository.findQuizById(quizId) ?: throw QuizNotFoundException(quizId)

        if (quiz.author != user) {
            throw QuizWriteAccessNotAllowedException(user.username, quizId)
        }

        val question = Question(questionCreationRequest.text, questionCreationRequest.multiple, quiz)
        val savedQuestion = questionRepository.save(question)

        questionCreationRequest.answers.forEach { answerRequest ->
            val answer = Answer(answerRequest.text, answerRequest.correct, savedQuestion)
            answerRepository.save(answer)
        }
    }

    fun getAllQuizzes(): List<QuizDto> {
        val answers: List<Answer> = answerRepository.findAllAnswers()
        val answerByQuestionMap: Map<Question, List<Answer>> = answers.groupBy { it.question }

        val quizDtoList: MutableList<QuizDto> = ArrayList()
        val questionsByQuizMap: MutableMap<Quiz, MutableList<Question>> = LinkedHashMap()
        answerByQuestionMap.forEach { (key, _) ->
            if (questionsByQuizMap[key.quiz] == null) {
                questionsByQuizMap[key.quiz] = ArrayList()
            }
            questionsByQuizMap[key.quiz]?.add(key)
        }

        questionsByQuizMap.forEach { (key, value) ->
            val questionDtoList: MutableList<QuestionDto> = ArrayList()
            value.forEach { question ->
                val answerDtoList: List<AnswerDto> =
                    answerByQuestionMap[question]?.map { AnswerDto(it.id, it.text, it.correct) } ?: ArrayList()

                val questionDto = QuestionDto(question.id, question.text, question.multiple, answerDtoList)
                questionDtoList.add(questionDto)
            }
            val author = UserDto(key.author.id, key.author.username)
            val quizDto = QuizDto(key.id, key.title, author, questionDtoList)
            quizDtoList.add(quizDto)
        }

        return quizDtoList
    }

    fun getBlankQuizzes(): List<BlankQuizDto> {
        return quizRepository.findAllQuizzes().map { quiz ->
            BlankQuizDto(quiz.id, quiz.title, UserDto(quiz.author.id, quiz.author.username))
        }
    }

    private fun getCurrentUser(currentUser: CustomUserDetails?): User {
        if (currentUser == null) {
            throw NoAuthException()
        }
        return userRepository.findUserById(currentUser.userId) ?: throw UserNotFoundException(currentUser.userId)
    }
}