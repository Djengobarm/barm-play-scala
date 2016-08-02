package models

import org.joda.time.DateTime
import play.api.libs.json._
import slick.driver.MySQLDriver.api._

case class User(id: Int, name: String, email: String, password: String, created: DateTime = DateTime.now, updated: DateTime = DateTime.now)

class Users(tag: Tag) extends Table[User] (tag, "Users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def password = column[String]("password")
    def created = column[DateTime]("created_at")
    def updated = column[DateTime]("updated_at")

    def * = (id, name, email, password, created, updated) <> ((User.apply _).tupled, User.unapply)
}

object User {
    implicit val userFormat = Json.format[User]
}
