import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Events : Table("events") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description")
    val startTime = datetime("start_time")
    val endTime = datetime("end_time")
    val createdBy = integer("created_by")

    override val primaryKey = PrimaryKey(id)
}

data class Event(
    val id: Int? = null,
    val name: String,
    val description: String,
    val startTime: java.time.LocalDateTime,
    val endTime: java.time.LocalDateTime,
    val createdBy: Int
)
