package dal

import javax.inject.{Inject, Singleton}

import models.{User, Users}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future


@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) {

    private val dbConfig = dbConfigProvider.get[JdbcProfile]

    import dbConfig._
    import driver.api._

    private val users = TableQuery[Users]

    def add(name: String): Future[User] = db.run {
        (users.map(user => (user.name))
            returning users.map(_.id)
            into((nameU, id) => User(id, nameU))
        ) += (name)
    }

    /**
      * List all the users in the database.
      */
    def list(): Future[Seq[User]] = db.run {
        users.result
    }
}
