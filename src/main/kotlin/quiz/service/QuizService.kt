package quiz.service

import org.springframework.stereotype.Service
import quiz.dto.AnswerDto
import quiz.dto.QuestionDto
import quiz.dto.QuizCreationRequest
import quiz.dto.QuizDto
import quiz.exception.NoAuthException
import quiz.exception.UserNotFoundException
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
        val user = userRepository.findUserById(currentUser.userId) ?: throw UserNotFoundException(currentUser.userId)

        val quiz = Quiz(quizCreationRequest.title, user)

        val savedQuiz = quizRepository.save(quiz)

        val questions: MutableList<Question> = ArrayList()
        val answers: MutableList<Answer> = ArrayList()

        quizCreationRequest.questions.forEach { questionRequest ->
            val question = Question(questionRequest.text, questionRequest.multiple, savedQuiz)

            val savedQuestion = questionRepository.save(question)
            questions.add(savedQuestion)

            questionRequest.answers.forEach { answerRequest ->
                val answer = Answer(answerRequest.text, answerRequest.correct, savedQuestion)

                val savedAnswer = answerRepository.save(answer)
                answers.add(savedAnswer)
            }
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
            val quizDto = QuizDto(key.id, key.title, key.author, questionDtoList)
            quizDtoList.add(quizDto)
        }

        return quizDtoList
    }
}