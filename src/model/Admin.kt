import org.jetbrains.exposed.sql.Table
import java.time.LocalDateTime

object Admins : Table("admins") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 100)
    val whatsapp = varchar("whatsapp_number", 20)
    val password = varchar("password", 255)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

data class Admin(
    val id: Int? = null,
    val name: String,
    val email: String,
    val whatsapp: String,
    val password: String
)
