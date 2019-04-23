organization := "com.igeolise"

name := "optimize"

version := "1.0.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",  // yes, this is 2 args
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
)
scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import"))
scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value

mainClass in (Compile, run) := Some("task1.Main")

resolvers ++= List(
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  "bintray/denisrosset" at "http://dl.bintray.com/denisrosset/maven"
)

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core"   % "7.2.15",
  "org.scalaz" %% "scalaz-effect" % "7.2.15",
  "org.scala-metal" %% "metal-core" % "0.16.0.0",
  "org.scala-metal" %% "metal-library" % "0.16.0.0",
  "org.scalatest" %% "scalatest" % "3.0.7" % Test
)
