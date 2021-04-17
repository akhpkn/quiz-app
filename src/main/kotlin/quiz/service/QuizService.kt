package quiz.service

import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator
import org.springframework.stereotype.Service
import quiz.dto.*
import quiz.exception.QuizNotFoundException
import quiz.exception.QuizWriteAccessNotAllowedException
import quiz.exception.ResultAlreadyExistsException
import quiz.exception.WrongSelectedAnswersDefinitionException
import quiz.mapper.DtoMapper
import quiz.model.*
import quiz.repository.*
import quiz.security.AuthManager
import quiz.security.CustomUserDetails

@Service
class QuizService(
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val authManager: AuthManager,
    private val resultRepository: ResultRepository,
    private val dtoMapper: DtoMapper,
) {

    private val usedCodes = quizRepository.getCodes()

    fun createQuiz(quizCreationRequest: QuizCreationRequest, currentUser: CustomUserDetails?): BlankQuizDto {
        val user: User = authManager.getAuthorizedUser(currentUser)
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

        return dtoMapper.quizToBlankDto(savedQuiz)
    }

    fun createBlankQuiz(blankQuizRequest: BlankQuizRequest, currentUser: CustomUserDetails?): BlankQuizDto {
        val user: User = authManager.getAuthorizedUser(currentUser)
        val code = generateCode().also { usedCodes.add(it) }

        val quiz = Quiz(blankQuizRequest.title, user, code)
        val savedQuiz = quizRepository.save(quiz)

        return dtoMapper.quizToBlankDto(savedQuiz)
    }

    fun addQuestion(quizId: Long, questionCreationRequest: QuestionCreationRequest, currentUser: CustomUserDetails?) {
        val user: User = authManager.getAuthorizedUser(currentUser)

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

    fun getQuizzes(): ListWrapper<QuizDto> {
        val answers: List<Answer> = answerRepository.findAllAnswers()
        val answersByQuestionMap: Map<Question, List<Answer>> = answers.groupBy { it.question }

        return ListWrapper(
            answersByQuestionMap
                .keys
                .groupBy { it.quiz }
                .map { (quiz, questions) ->
                    val questionDtoList = questions.map { question ->
                        val answerDtoList: List<AnswerDto> =
                            answersByQuestionMap[question]?.map { AnswerDto(it.id, it.text, it.correct) } ?: ArrayList()
                        QuestionDto(question.id, question.text, question.multiple, answerDtoList)
                    }
                    val author = dtoMapper.userToDto(quiz.author)
                    QuizDto(quiz.id, quiz.title, quiz.title, author, questionDtoList)
                }
                .toList()
        )
    }

    fun getBlankQuizzes() = ListWrapper(quizRepository.findAllQuizzes().map { dtoMapper.quizToBlankDto(it) })


    fun getBlankQuizzesByAuthor(authorId: Long) =
        ListWrapper(quizRepository.findQuizzesByAuthorId(authorId).map { dtoMapper.quizToBlankDto(it) })


    fun getQuizzesCreatedByMe(currentUser: CustomUserDetails?): ListWrapper<BlankQuizDto> {
        val user = authManager.getAuthorizedUser(currentUser)
        return ListWrapper(quizRepository.findQuizzesByAuthorId(user.id).map { dtoMapper.quizToBlankDto(it) })
    }

    fun getQuizQuestions(quizId: Long): ListWrapper<QuestionDto> {
        val answers: List<Answer> = answerRepository.findAnswersByQuizId(quizId)
        return ListWrapper(
            answers
                .groupBy { it.question }
                .map { (question, answerList) -> dtoMapper.questionToDto(question, answerList) }
                .toList()
        )
    }

    fun existsByCode(code: String) = quizRepository.existsByCode(code)

    fun getQuizByCode(code: String): BlankQuizDto {
        val quiz = quizRepository.findByCode(code) ?: throw QuizNotFoundException(code)
        return dtoMapper.quizToBlankDto(quiz)
    }

    fun accessWithCode(quizAccessRequest: QuizAccessRequest): JwtAuthenticationResponse {
        val code = quizAccessRequest.code
        val quiz = quizRepository.findByCode(code) ?: throw QuizNotFoundException(code)
        val username = generateUsername()
        val password = generatePassword()
        val user = authManager.userWithEncodedPassword(
            username,
            password,
            quizAccessRequest.name,
            emptySet()
        ).apply {
            registered = false
            contextQuiz = quiz
        }
        userRepository.save(user)
        return authManager.authenticate(username, password)
    }

    fun sendAnswers(quizId: Long, selectedAnswers: SelectedAnswers, currentUser: CustomUserDetails?): ResultDto {
        val user = authManager.getAuthorizedUser(currentUser)
        val answers = answerRepository.findByIdIn(selectedAnswers.answersIds)
        val quiz = validateSelectedAnswersAndGetQuiz(quizId, answers)
        var totalCorrectAnswers = 0

        answers.groupBy { it.question }.forEach { (_, answerList) ->
            val diff = answerList.count { it.correct } - answerList.count { !it.correct }
            val correctAnswers = if (diff > 0) diff else 0
            totalCorrectAnswers += correctAnswers
        }

        if (resultRepository.findResultByQuizIdAndUserId(quizId, user.id) != null) {
            throw ResultAlreadyExistsException(user.id, quizId)
        }
        val result = Result(user, quiz)

        val quizAnswers = answerRepository.findAnswersByQuizId(quizId)
        result.correctAnswers = totalCorrectAnswers
        result.score = 1.0 * result.correctAnswers / quizAnswers.count { it.correct }

        val savedResult = resultRepository.save(result)
        return dtoMapper.resultToDto(savedResult)
    }

    fun generateCodesIfNull() {
        val quizzes = quizRepository.findQuizzesWithoutCode()
        quizzes.forEach { it.code = generateCode() }
        quizRepository.saveAll(quizzes)
    }

    private fun validateSelectedAnswersAndGetQuiz(quizId: Long, answers: List<Answer>): Quiz {
        val quiz = quizRepository.findQuizById(quizId) ?: throw QuizNotFoundException(quizId)
        val quizIds = answers.map { it.question.quiz.id }.toSet()

        if (quizIds.size > 1) {
            throw WrongSelectedAnswersDefinitionException(quizId)
        }
        if (quizIds.size == 1 && quizIds.first() != quizId) {
            throw WrongSelectedAnswersDefinitionException(quizId)
        }

        return quiz
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
            .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.LETTERS)
            .build()
        return stringGenerator.generate(length)
    }
}