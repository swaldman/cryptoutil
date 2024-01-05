import mill._
import mill.scalalib._
import mill.scalalib.publish._

object cryptoutil extends RootModule with ScalaModule with PublishModule {

  val JakartaMailVersion = "2.0.1"

  override def scalaVersion = "3.3.1"

//  def scalacOptions = T {
//    super.scalacOptions() ++ Seq("-explain")
//  }

  override def ivyDeps = T {
    super.ivyDeps() ++
    Agg(
     ivy"com.mchange:mchange-commons-java:0.2.20",
    )
  }

  override def artifactName = "cryptoutil"
  override def publishVersion = T{"0.0.2"}
  override def pomSettings    = T{
    PomSettings(
      description = "Utilities useful for crypto-related and binary-heavy work.",
      organization = "com.mchange",
      url = "https://github.com/swaldman/cryptoutil",
      licenses = Seq(License.`Apache-2.0`),
      versionControl = VersionControl.github("swaldman", "cryptoutil"),
      developers = Seq(
	Developer("swaldman", "Steve Waldman", "https://github.com/swaldman")
      )
    )
  }
}


