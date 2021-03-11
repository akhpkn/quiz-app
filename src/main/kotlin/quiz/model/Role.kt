package quiz.model

import org.hibernate.annotations.NaturalId
import javax.persistence.*

@Entity
@Table(name = "roles")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NaturalId
    @Enumerated(EnumType.STRING)
    var name: RoleName? = null

    constructor(name: RoleName) {
        this.name = name
    }

    constructor()
}