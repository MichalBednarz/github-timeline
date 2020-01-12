package utils

import models.GithubNode
import play.api.libs.json.{JsArray, JsPath, JsValue, Reads}
import play.api.libs.functional.syntax._
import play.api.libs.ws.{WSClient, WSRequest}
import scala.concurrent.duration._

object GithubUtil {
  implicit private val githubNodeReads: Reads[GithubNode] = (
    (JsPath \ "createdAt").read[String] and
      (JsPath \ "author" \ "login").read[String] and
      (JsPath \ "url").read[String]
  )(GithubNode)

  def parse(json: JsValue): List[GithubNode] =
    (json \\ "nodes").head
      .as[JsArray]
      .value
      .map(_.validate[GithubNode].get)
      .toList

  def createRequest(ws: WSClient): WSRequest =
    ws.url("https://api.github.com/graphql")
      .addHttpHeaders("Authorization" -> s"bearer ${sys.env("GITHUB_KEY")}")
      .addHttpHeaders("Content-Type" -> "application/graphql")
      .withRequestTimeout(10000.millis)
}
