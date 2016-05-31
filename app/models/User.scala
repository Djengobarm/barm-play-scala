package models

import play.api.libs.json._
import slick.driver.MySQLDriver.api._

case class User(id: Int, name: String)

class Users(tag: Tag) extends Table[User] (tag, "Users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id, name) <> ((User.apply _).tupled, User.unapply)
}

object User {
    implicit val userFormat = Json.format[User]
}
