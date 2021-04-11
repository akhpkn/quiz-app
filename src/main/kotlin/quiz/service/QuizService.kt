package quiz.service

import org.apache.commons.text.RandomStringGenerator
import org.springframework.stereotype.Service
import quiz.dto.*
import quiz.exception.NoAuthException
import quiz.exception.QuizNotFoundException
import quiz.exception.QuizWriteAccessNotAllowedException
import quiz.exception.UserNotFoundException
import quiz.mapper.DtoMapper
import quiz.model.Answer
import quiz.model.Question
import quiz.model.Quiz
import quiz.model.User
import quiz.repository.AnswerRepository
import quiz.repository.QuestionRepository
import quiz.repository.QuizRepository
import quiz.repository.UserRepository
import quiz.security.AuthenticationProvider
import quiz.security.CustomUserDetails

@Service
class QuizService(
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val authenticationProvider: AuthenticationProvider,
    private val dtoMapper: DtoMapper,
) {

    private val usedCodes = quizRepository.getCodes()

    fun createQuiz(quizCreationRequest: QuizCreationRequest, currentUser: CustomUserDetails?) {
        val user: User = getCurrentUser(currentUser)
        val code = generateCode().also { usedCodes.add(it) }

        val quiz = Quiz(quizCreationRequest.title, user, code)
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
        val code = generateCode().also { usedCodes.add(it) }

        val quiz = Quiz(blankQuizRequest.title, user, code)
        val savedQuiz = quizRepository.save(quiz)

        return dtoMapper.quizToBlankDto(savedQuiz)
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

    fun getQuizzes(): List<QuizDto> {
        val answers: List<Answer> = answerRepository.findAllAnswers()
        val answersByQuestionMap: Map<Question, List<Answer>> = answers.groupBy { it.question }

        return answersByQuestionMap
            .keys
            .groupBy { it.quiz }
            .map { (quiz, questions) ->
                val questionDtoList = questions.map { question ->
                    val answerDtoList: List<AnswerDto> =
                        answersByQuestionMap[question]?.map { AnswerDto(it.id, it.text, it.correct) } ?: ArrayList()
                    QuestionDto(question.id, question.text, question.multiple, answerDtoList)
                }
                val author = UserDto(quiz.author.id, quiz.author.username)
                QuizDto(quiz.id, quiz.title, author, questionDtoList)
            }
            .toList()
    }

    fun getBlankQuizzes(): List<BlankQuizDto> {
        return quizRepository.findAllQuizzes().map { dtoMapper.quizToBlankDto(it) }
    }

    fun getBlankQuizzesByAuthor(authorId: Long): List<BlankQuizDto> {
        return quizRepository.findQuizzesByAuthorId(authorId).map { dtoMapper.quizToBlankDto(it) }
    }

    fun getQuizQuestions(quizId: Long): List<QuestionDto> {
        val answers: List<Answer> = answerRepository.findAnswersByQuizId(quizId)
        return answers
            .groupBy { it.question }
            .map { (question, answerList) -> dtoMapper.questionToDto(question, answerList) }
            .toList()
    }

    fun existsById(id: Long) = quizRepository.existsById(id)

    fun accessWithCode(code: String, name: String): JwtAuthenticationResponse {
        val quiz = quizRepository.findByCode(code) ?: throw QuizNotFoundException(code)
        val username = generateUsername()
        val password = generatePassword()
        val user = authenticationProvider.userWithEncodedPassword(
            username,
            password,
            name,
            emptySet()
        ).apply {
            registered = false
            contextQuiz = quiz
        }
        userRepository.save(user)
        return authenticationProvider.authenticate(username, password)
    }

    private fun getCurrentUser(currentUser: CustomUserDetails?): User {
        if (currentUser == null) {
            throw NoAuthException()
        }
        return userRepository.findUserById(currentUser.userId) ?: throw UserNotFoundException(currentUser.userId)
    }

    private fun generateUsername(): String {
        var username = generateRandomString(30)
        while (userRepository.existsByUsername(username)) {
            username = generateRandomString(30)
        }
        return username
    }

    private fun generatePassword() = generateRandomString(60)

    private fun generateCode(): String {
        var code = generateCodeString()
        while (usedCodes.contains(code)) {
            code = generateCodeString()
        }
        return code
    }

    private fun generateCodeString() = generateRandomString(6).toUpperCase()

    private fun generateRandomString(length: Int): String {
        val stringGenerator = RandomStringGenerator.Builder()
            .withinRange('0'.toInt(), 'z'.toInt())
            .build()
        return stringGenerator.generate(length)
    }
}