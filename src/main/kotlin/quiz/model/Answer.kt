package quiz.model

import javax.persistence.*

@Entity
@Table(name = "answers")
class Answer {
    @Id
    @GeneratedValue
    var id: Long = -1

    var text: String = ""

    var correct: Boolean = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    var question: Question? = null
}