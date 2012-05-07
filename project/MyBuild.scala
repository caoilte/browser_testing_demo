import com.github.siasia.WebPlugin
import sbt._

import Keys._

object MyBuild extends Build
{

  def rootSettings: Seq[Setting[_]] = Seq(
    organization := "com.playfish.service.console",

    name := "console-system-tests",

    version := "1.0-SNAPSHOT",

    scalaVersion := "2.9.1",

    libraryDependencies ++= Seq(
      "org.seleniumhq.selenium" % "selenium-server" % "2.20.0",
      "org.seleniumhq.selenium" % "selenium-server" % "2.20.0" % "container",
      "org.seleniumhq.selenium" % "selenium-java" % "2.20.0",
      "net.liftweb" %% "lift-json" % "2.4",
      "org.scalaj" %% "scalaj-http" % "0.3.0",
      "net.debasishg" %% "sjson" % "0.17",
      "ch.qos.logback" % "logback-classic" % "0.9.18"

    ),

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "1.7.1" % "it,test",
      "junit" % "junit" % "4.8.1" % "it,test",
      "org.mortbay.jetty" % "jetty-servlet-tester" % "6.1.22" % "container",
      "org.mortbay.jetty" % "jetty-servlet-tester" % "6.1.22" % "test->default",
      "javax.servlet" % "servlet-api" % "2.5" % "provided"
    ),

  test in IntegrationTest <<= (test in IntegrationTest).dependsOn(
    com.github.siasia.PluginKeys.start in com.github.siasia.WebPlugin.container.Configuration)


  )

  override def projects = Seq(root)

  lazy val root = Project("root", file(".")).configs(IntegrationTest)
    .settings( Defaults.itSettings : _*)
    .settings( rootSettings : _*)
    .settings( WebPlugin.webSettings : _*)

  //, settings = Defaults.defaultSettings ++ WebPlugin.webSettings ++ rootSettings)
}
