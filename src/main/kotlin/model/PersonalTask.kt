import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object PersonalTasks : Table("personal_tasks") {
    val id = integer("id").autoIncrement()
    val adminId = integer("admin_id")
    val description = text("description")
    val taskType = varchar("task_type", 20) // personal, work, urgent
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

data class PersonalTask(
    val id: Int? = null,
    val adminId: Int,
    val description: String,
    val taskType: String
)
