organization := "com.igeolise"

name := "review"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.3"

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


libraryDependencies ++= Seq(
  "eu.timepit" %% "refined" % "0.9.5",
  "org.scalatest" %% "scalatest" % "3.0.7" % Test
)