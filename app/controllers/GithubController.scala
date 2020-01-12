package controllers

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.data.Form
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.{
  AnyContent,
  MessagesAbstractController,
  MessagesControllerComponents,
  MessagesRequest
}
import utils.{GithubUtil, Query}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class GithubController @Inject()(cc: MessagesControllerComponents, ws: WSClient)
    extends MessagesAbstractController(cc) {
  private val logger: Logger = Logger(this.getClass)

  /**
    * POST /github/repository
    */
  private val postRepository = routes.GithubController.submitRepository()

  /**
    * POST /github/user
    */
  private val postUser = routes.GithubController.submitUser()

  /**
    * Displays repository form.
    * @return
    */
  def repository = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.repository(RepositoryForm.form, postRepository))
  }

  /**
    * Handles repository form POST. Handles GraphQL query creation based on form data and redirects to /github/:query.
    * @return
    */
  def submitRepository = Action {
    implicit request: MessagesRequest[AnyContent] =>
      val errorFunction = { formWithErrors: Form[RepositoryForm.Data] =>
        BadRequest(views.html.repository(formWithErrors, postRepository))
      }

      val successFunction = { data: RepositoryForm.Data =>
        val query = Query.makeNode(data.node)(data)

        Redirect(routes.GithubController.fetchNodes(query))
      }

      val formValidationResult = RepositoryForm.form.bindFromRequest
      formValidationResult.fold(errorFunction, successFunction)
  }

  /**
    * Displays user form.
    * @return
    */
  def user = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user(UserForm.form, postUser))
  }

  /**
    * Handles user form POST. Handles GraphQL query creation based on form data and redirects to /github/:query.
    * @return
    */
  def submitUser = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[UserForm.Data] =>
      BadRequest(views.html.user(formWithErrors, postUser))
    }

    val successFunction = { data: UserForm.Data =>
      val query = Query.makeNode(data.node)(data)

      Redirect(routes.GithubController.fetchNodes(query))
    }

    val formValidationResult = UserForm.form.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

  /**
    * Fetches, parses and displays queried data.
    * @param query GraphQL query
    * @return
    */
  def fetchNodes(query: String) = Action.async {
    implicit req: MessagesRequest[AnyContent] =>
      val wsRes: Future[WSResponse] = GithubUtil.createRequest(ws).post(query)

      // todo error handling
      wsRes map { result =>
        Ok(views.html.timeline(GithubUtil.parse(result.json)))
      }
  }
}
