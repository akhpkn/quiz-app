package quiz.model

import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    var username: String,

    var password: String,

    var name: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Set<Role> = HashSet(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    @ManyToOne
    @JoinColumn("quiz_id")
    var contextQuiz: Quiz? = null

    var registered: Boolean = true
}