import org.jetbrains.exposed.sql.Table

object EventMembers : Table("event_members") {
    val id = integer("id").autoIncrement()
    val eventId = integer("event_id")
    val memberId = integer("member_id")

    override val primaryKey = PrimaryKey(id)
}

data class EventMember(
    val id: Int? = null,
    val eventId: Int,
    val memberId: Int
)
