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

    def add(name: String, email: String, password: String): Future[User] = db.run {
        (users.map(user => (user.name, user.email, user.password))
            returning users.map(_.id)
            into((nameU, id) => User(id, nameU))
        ) += name
    }

    def delete(id: Int): Future[Int] = db.run {
        this.findById(id).delete
    }

    def exists(id: Int): Future[Boolean] = db.run {
        users.filter( i => i.id === id ).exists.result
    }

    /**
      * List all the users in the database.
      */
    def list(): Future[Seq[User]] = db.run {
        users.result
    }

    def findById(id: Int) = {
        //for ( user <- users if user.id === id ) yield user
        users.filter(_.id === id)
    }
}
