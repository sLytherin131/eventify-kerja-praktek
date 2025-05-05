import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object EventTasks : Table("event_tasks") {
    val id = integer("id").autoIncrement()
    val eventId = integer("event_id")
    val description = text("description")
    val taskType = varchar("task_type", 20) // normal or urgent
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

data class EventTask(
    val id: Int? = null,
    val eventId: Int,
    val description: String,
    val taskType: String
)
