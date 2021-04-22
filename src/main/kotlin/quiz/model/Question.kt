package quiz.model

import javax.persistence.*

@Entity
@Table(name = "questions")
class Question(
    var text: String,

    var multiple: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    var quiz: Quiz,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1
}