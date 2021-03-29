package quiz.model

import javax.persistence.*

@Entity
@Table(name = "results")
class Result(
    @OneToOne
    @JoinColumn(name = "user_id")
    var user: User,

    @OneToOne
    @JoinColumn(name = "quiz_id")
    var quiz: Quiz,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    var correctAnswers: Int = 0

    var score: Double = 0.0
}