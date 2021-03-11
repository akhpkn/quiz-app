package quiz.model

import javax.persistence.*

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    var username: String = ""

    var password: String = ""

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")])
    var roles: Set<Role> = HashSet()

    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }

    constructor()
}