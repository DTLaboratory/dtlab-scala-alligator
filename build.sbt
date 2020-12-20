name := "DtLab"
organization := "org.somind"
javacOptions ++= Seq("-source", "1.11", "-target", "1.11") 
scalacOptions ++= Seq(
  "-target:jvm-1.8"
)
fork := true
javaOptions in test ++= Seq(
  "-Xms128M", "-Xmx256M",
  "-XX:+CMSClassUnloadingEnabled"
)

version := "0.1.0"

parallelExecution in test := false

crossScalaVersions := List("2.12.11")
version := "1.0"

val akkaHttpVersion = "10.2.2"
val akkaVersion = "2.6.10"
val swaggerVersion = "2.0.8"

libraryDependencies ++=
  Seq(
    "io.altoo" %% "akka-kryo-serialization" % "2.0.1",
    "tech.navicore" %% "navipath" % "4.0.2",
    "org.postgresql" % "postgresql" % "42.2.18",
    "com.lightbend.akka" %% "akka-persistence-jdbc" % "4.0.0",
    "ch.megard" %% "akka-http-cors" % "1.1.1",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe" % "config" % "1.4.1",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "org.json4s" %% "json4s-native" % "3.6.10",
    "com.github.nscala-time" %% "nscala-time" % "2.26.0",
    "org.scalatest" %% "scalatest" % "3.2.3" % "test"
  )

assemblyJarName in assembly := s"${name.value}.jar"

assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

