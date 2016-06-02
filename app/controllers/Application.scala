package controllers

import java.sql.SQLException
import java.util.concurrent.TimeoutException
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
    // get future delete
    val futureDel = userRepo.delete(id)

    val successMessage = "User #" + id.toString + " have been deleted"

    // Redirect to home page with Success flash message
    futureDel.map { result => Home.flashing("success" -> successMessage) }.recover {
      // if anything goes wrong
      case ex: TimeoutException =>
        Logger("User #" + id.toString + " can not be deleted. " + ex.getMessage)
        InternalServerError(ex.getMessage)
      case ex: SQLException =>
        Logger("User #" + id.toString + " can not be deleted. " + ex.getMessage)
        InternalServerError(ex.getMessage)
    }
  }

}

case class CreateUserForm(name: String)