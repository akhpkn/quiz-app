package quiz.model

import javax.persistence.*

@Entity
@Table(name = "quizzes")
class Quiz(
    var title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    var author: User,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1
}