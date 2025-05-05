import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/efevntify_db",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = ""
        )

        transaction {
            SchemaUtils.create(Admins, Members, Events, EventMembers, EventTasks, PersonalTasks)
        }
    }
}

