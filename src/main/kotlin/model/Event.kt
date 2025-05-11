import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Events : Table("events") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description")
    val startTime = datetime("start_time")
    val endTime = datetime("end_time")
    val createdBy = varchar("created_by", 15) // karena di DB kamu, ini foreign key ke whatsapp_number

    override val primaryKey = PrimaryKey(id)
}

data class Event(
    val id: Int? = null,
    val name: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val createdBy: String
)
