package quiz.controller

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import quiz.dto.*
import quiz.security.CurrentUser
import quiz.security.CustomUserDetails
import quiz.service.QuizService

@RestController
@RequestMapping("/api/quiz")
class QuizController(private val quizService: QuizService) {

    @PostMapping("/create")
    @ApiOperation("Метод для создания квиза. Принимает список всех вопросов с вариантами ответов")
    fun createQuiz(
        @RequestBody quizCreationRequest: QuizCreationRequest,
        @CurrentUser currentUser: CustomUserDetails?,
    ): BlankQuizDto {
        return quizService.createQuiz(quizCreationRequest, currentUser)
    }

    @PostMapping("/create-blank")
    @ApiOperation("Метод для создания незаполненного квиза. Не принимает список вопросов")
    fun createBlankQuiz(
        @RequestBody blankQuizRequest: BlankQuizRequest,
        @CurrentUser currentUser: CustomUserDetails?,
    ): BlankQuizDto {
        return quizService.createBlankQuiz(blankQuizRequest, currentUser)
    }

    @GetMapping("/all")
    @ApiOperation("Метод, возвращающий все квизы, включая все вопросы с вариантами ответов")
    fun getQuizzes(): ListWrapper<QuizDto> {
        return quizService.getQuizzes()
    }

    @GetMapping("/all-blank")
    @ApiOperation("Метод, возвращающий все квизы, но без вопросов")
    fun getBlankQuizzes(): ListWrapper<BlankQuizDto> {
        return quizService.getBlankQuizzes()
    }

    @GetMapping("/created-by-me")
    @ApiOperation("Метод, возвращающий квизы, созданные авторизованным пользователем")
    fun getQuizzesCreatedByMe(@CurrentUser currentUser: CustomUserDetails?): ListWrapper<BlankQuizDto> {
        return quizService.getQuizzesCreatedByMe(currentUser)
    }

    @GetMapping
    @ApiOperation("Метод, возвращающий список квизов, созданных автором с определенным id")
    fun getBlankQuizzesByAuthor(@RequestParam("authorId") authorId: Long): ListWrapper<BlankQuizDto> {
        return quizService.getBlankQuizzesByAuthor(authorId)
    }

    @GetMapping("/{quizId}/questions")
    @ApiOperation("Метод для возвращения списка вопросов по id квиза")
    fun getQuizQuestions(
        @PathVariable("quizId") quizId: Long,
    ): ListWrapper<QuestionDto> {
        return quizService.getQuizQuestions(quizId)
    }

    @PostMapping("/{quizId}/question")
    @ApiOperation("Метод для добавления вопроса к квизу")
    fun addQuestionToQuiz(
        @PathVariable("quizId") quizId: Long,
        @RequestBody questionCreationRequest: QuestionCreationRequest,
        @CurrentUser currentUser: CustomUserDetails?,
    ) {
        quizService.addQuestion(quizId, questionCreationRequest, currentUser)
    }

    @GetMapping("/exists-by-code")
    @ApiOperation("Метод для проверки существования квиза по коду")
    fun existsByCode(@RequestParam("code") code: String): Boolean {
        return quizService.existsByCode(code)
    }

    @GetMapping("/{code}")
    @ApiOperation("Метод для получения квиза по коду")
    fun getByCode(@PathVariable("code") code: String): BlankQuizDto {
        return quizService.getQuizByCode(code)
    }

    @PostMapping("/access")
    @ApiOperation("Метод для подключения к квизу по коду")
    fun accessWithCode(@RequestBody quizAccessRequest: QuizAccessRequest): JwtAuthenticationResponse {
        return quizService.accessWithCode(quizAccessRequest)
    }

    @PostMapping("/{quizId}/send-answers")
    @ApiOperation("Метод для отправки всех выбранных пользователем ответов на квиз")
    fun sendAnswers(
        @PathVariable("quizId") quizId: Long,
        @RequestBody selectedAnswers: SelectedAnswers,
        @CurrentUser currentUser: CustomUserDetails?,
    ): ResultDto {
        return quizService.sendAnswers(quizId, selectedAnswers, currentUser)
    }
}