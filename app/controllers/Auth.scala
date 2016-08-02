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

class Auth @Inject() (userRepo: UserRepository, val messagesApi: MessagesApi) (implicit ec: ExecutionContext) extends Controller with I18nSupport {

    val loginForm: Form[CreateLoginForm] = Form {
        mapping(
            "email" -> nonEmptyText,
            "password" -> nonEmptyText
        )(CreateLoginForm.apply)(CreateLoginForm.unapply)
    }

    val registerForm: Form[CreateRegisterForm] = Form {
        mapping(
            "name" -> nonEmptyText,
            "email" -> nonEmptyText,
            "password" -> nonEmptyText
        )(CreateRegisterForm.apply)(CreateRegisterForm.unapply)
    }

    def login = Action { implicit request =>
        Ok(views.html.login(loginForm))
        //Redirect(routes.Auth.register()).flashing("warning" -> "Yo there!")
    }

    def register = Action { implicit request =>
        Ok(views.html.register(registerForm))
    }

    def doRegister = Action.async { implicit request =>
        registerForm.bindFromRequest.fold(
            errorForm => {
                Future.successful(Ok(views.html.register(errorForm)))
            },
            user => {
                userRepo.add(user.name, user.email, user.password).map { _ =>
                    Redirect(routes.Application.index())
                }
            }
        )
    }

}

case class CreateLoginForm(email: String, password: String)

case class CreateRegisterForm(name: String, email: String, password: String)