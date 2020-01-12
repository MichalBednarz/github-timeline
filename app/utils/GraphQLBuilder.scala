package utils

/*
  TODO
  - support mutations
  - with arguments
  - handle create in a cleaner way
  - some kind of validation
 */

/**
  * GraphQL query builder. Even in this prototypical repository this builder provides a convenient way to build queries
  * and extract logic common between similar queries.
  * TODO - mutation support, arguments, handle factory method in cleaner way, some kind of validation.
  * @param callback
  */
case class GraphQLBuilder(callback: String => String = _ => "") {
  def query: GraphQLBuilder =
    GraphQLBuilder(
      (content: String) =>
        s"{ ${'"'}query${'"'}: ${'"'}query { $content }${'"'}}"
    )

  def nest(content: String): GraphQLBuilder =
    GraphQLBuilder(
      (nestedContent: String) =>
        callback(
          s"$content ${if (!nestedContent.isEmpty) s"{ $nestedContent }" else ""}"
      )
    )

  /**
    * Provides convenient way to handle "many" case by inserting "nodes" out of the box.
    * @param content
    * @return
    */
  def nestWithNodes(content: String): GraphQLBuilder = ???

  def append(content: String): GraphQLBuilder =
    GraphQLBuilder(
      (appendedContent: String) => callback(s"$content $appendedContent")
    )

  def create: String = callback("")
}
