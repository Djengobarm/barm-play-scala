package controllers

import java.sql.SQLException
import javax.inject.Inject

import dal.UserRepository
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class Application @Inject() (userRepo: UserRepository, val messagesApi: MessagesApi) (implicit ec: ExecutionContext) extends Controller with I18nSupport {

    val Home = Redirect(routes.Application.index())

    val userForm: Form[CreateUserForm] = Form {
        mapping(
            "name" -> nonEmptyText
        )(CreateUserForm.apply)(CreateUserForm.unapply)
    }

    def index = Action { implicit request =>
        Ok(views.html.index(userForm))
    }

    def addUser = Action.async { implicit request =>
        userForm.bindFromRequest.fold(
            errorForm => {
                Future.successful(Ok(views.html.index(errorForm)))
            },
            user => {
                userRepo.add(user.name).map { _ =>
                    Redirect(routes.Application.index())
                }
            }
        )
    }

    def getUsers = Action.async {
        userRepo.list().map { users =>
            Ok(Json.toJson(users))
        }
    }

    def deleteUser(id: Int) = Action.async { implicit request =>

        val futureExists = userRepo.exists(id)

        futureExists.map { result => {
            if ( result ) {
                try {
                    userRepo.delete(id)
                    val successMessage = "User #" + id.toString + " have been deleted"
                    Home.flashing("success" -> successMessage)
                } catch {
                    case e: SQLException => {
                        Home.flashing("error" -> e.getMessage)
                    }
                    case e: Exception => {
                        Home.flashing("error" -> e.getMessage)
                    }
                }
            } else {
                val notFoundMessage = "User #" + id.toString + " was not found"
                Home.flashing("warning" -> notFoundMessage)
            }
        }}
    }

}

case class CreateUserForm(name: String)