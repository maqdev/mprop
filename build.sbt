name := "mprop"

version := "0.1"

organization := "com.maqdev"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "io.monix" %% "monix-reactive" % "2.2.1",
  "org.parboiled" %% "parboiled" % "2.1.4",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % "test"
)

resolvers ++= Seq(
	Resolver.sonatypeRepo("public")
)
