import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Members : Table("members") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val whatsapp = varchar("whatsapp_number", 20)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

data class Member(
    val id: Int? = null,
    val name: String,
    val whatsapp: String
)
