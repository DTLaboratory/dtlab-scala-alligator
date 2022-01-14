name := "DtLab"
organization := "com.dtlaboratory"
javacOptions ++= Seq("-source", "1.11", "-target", "1.11")
scalacOptions ++= Seq(
  "-target:jvm-1.8"
)
fork := true
javaOptions in test ++= Seq(
  "-Xms128M",
  "-Xmx256M",
  "-XX:+CMSClassUnloadingEnabled"
)

version := "0.1.0"

parallelExecution in test := false

crossScalaVersions := List("2.13.8")
version := "1.0"

val akkaHttpVersion = "10.2.7"
val akkaVersion = "2.6.18"
val swaggerVersion = "2.0.8"

libraryDependencies ++=
  Seq(
    "io.altoo" %% "akka-kryo-serialization" % "2.3.0",
    "tech.navicore" %% "navipath" % "4.1.0",
    "org.postgresql" % "postgresql" % "42.3.1",
    "com.lightbend.akka" %% "akka-persistence-jdbc" % "5.0.4",
    "ch.megard" %% "akka-http-cors" % "1.1.2",
    "ch.qos.logback" % "logback-classic" % "1.2.10",
    "com.typesafe" % "config" % "1.4.1",
    "com.typesafe" %% "ssl-config-core" % "0.6.0",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "org.json4s" %% "json4s-native" % "4.0.3",
    "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    "com.github.nscala-time" %% "nscala-time" % "2.30.0"
  )

assemblyJarName in assembly := s"${name.value}.jar"

assemblyMergeStrategy in assembly := {
  case PathList("reference.conf")                      => MergeStrategy.concat
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case PathList("META-INF", _ @_*)                     => MergeStrategy.discard
  case _                                               => MergeStrategy.first
}
