package controllers

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

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  def index = Action {
    Ok(views.html.index(userForm))
  }

  def addUser = Action.async { implicit request =>
      userForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(Ok(views.html.index(errorForm)))
        },
        user => {
          userRepo.add(user.name).map { _ =>
              Redirect(routes.Application.index)
          }
        }
      )
  }

  def getUsers = Action.async {
    userRepo.list().map { users =>
      Ok(Json.toJson(users))
    }
  }

}

case class CreateUserForm(name: String)