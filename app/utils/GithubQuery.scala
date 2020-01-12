package utils

import controllers.{FormData, RepositoryForm, UserForm}

/**
  * Object represents bunch of custom queries created using GraphQLBuilder providing data utilised within the timeline.
  * Example of singleton - scala handles this pattern with "object" keyword.
  * TODO Implementation seems to be quite dirty right now - find a cleaner, more safe and generic way to represent it.
  */
object Query {

  /**
    * TODO Describe the visitor pattern
    * @param entity
    * @return
    */
  private def makeEntity(entity: FormData): GraphQLBuilder = entity match {
    case RepositoryForm.Data(_, name, owner) =>
      GraphQLBuilder().query
        .nest(
          "repository(name: \\\"" ++ name ++ "\\\", owner: \\\"" ++ owner ++ "\\\")"
        )
    case UserForm.Data(_, login) =>
      GraphQLBuilder().query.nest("user(login:\\\"" ++ login ++ "\\\")")
  }

  def makeNode(node: String): FormData => String = node match {
    case "PullRequest"   => pullRequests
    case "CommitComment" => commitComments
    case "Issue"         => issues
    case _               => throw new Exception("Node is not supported.")
  }

  /**
    * Takes care of providing fields common across all the GraphQL types used within the timeline.
    * @param builder
    * @return
    */
  private def queryCommonFields(builder: GraphQLBuilder): String =
    builder
      .append("createdAt")
      .append("url")
      .nest("author")
      .append("login")
      .create

  def pullRequests: FormData => String =
    (entity: FormData) =>
      queryCommonFields(
        makeEntity(entity)
          .nest("pullRequests(first: 100)")
          .nest("nodes")
          .append("title")
    )

  def issues: FormData => String =
    (entity: FormData) =>
      queryCommonFields(
        makeEntity(entity)
          .nest("issues(first: 100)")
          .nest("nodes")
    )

  def commitComments: FormData => String =
    (entity: FormData) =>
      queryCommonFields(
        makeEntity(entity)
          .nest("commitComments(first: 100)")
          .nest("nodes")
    )
}
