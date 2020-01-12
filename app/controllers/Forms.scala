package controllers

import play.api.data.Form
import play.api.data.Forms._

sealed trait FormData {
  def node: String
}

object RepositoryForm {
  case class Data(node: String, name: String, owner: String) extends FormData

  private def validate(node: String, name: String, owner: String) =
    node match {
      case "Issue" | "PullRequest" | "CommitComment" =>
        Some(Data(node, name, owner))
      case _ => None
    }

  val form = Form(
    mapping(
      "node" -> nonEmptyText,
      "name" -> nonEmptyText,
      "owner" -> nonEmptyText
    )(Data.apply)(Data.unapply).verifying(fields => {
      fields match {
        case Data(node, name, owner) =>
          validate(node, name, owner).isDefined
      }
    })
  )
}

object UserForm {
  case class Data(node: String, login: String) extends FormData

  private def validate(node: String, login: String) = node match {
    case "Issue" | "PullRequest" | "CommitComment" => Some(Data(node, login))
    case _                                         => None
  }

  val form = Form(
    mapping("node" -> nonEmptyText, "login" -> nonEmptyText)(Data.apply)(
      Data.unapply
    ).verifying(
      "Entity not supported.",
      fields =>
        fields match {
          case Data(node, login) => validate(node, login).isDefined
      }
    )
  )
}
