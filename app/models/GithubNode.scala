package models

/**
  * Represents common structure of Issue, CommitComment and PullRequest from Github API.
  * TODO In further iterations the structure could support cases when for some nodes structure is different - for example
  * provide title or body fields so that they can be displayed within timeline.
  * @param createdAt
  * @param login
  * @param url
  */
case class GithubNode(createdAt: String, login: String, url: String)
