package quiz.model

import javax.persistence.*

@Entity
@Table(name = "quizzes")
class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    var title: String = ""
}