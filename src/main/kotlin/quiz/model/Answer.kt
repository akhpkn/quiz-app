package quiz.model

import javax.persistence.*

@Entity
@Table(name = "answers")
class Answer(
    var text: String,

    var correct: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    var question: Question,
) {
    @Id
    @GeneratedValue
    var id: Long = -1
}